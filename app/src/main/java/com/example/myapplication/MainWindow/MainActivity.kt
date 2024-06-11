package com.example.myapplication.MainWindow
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.toMutableStateMap
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myapplication.AddCockTail.AddCockTail
import com.example.myapplication.DetailedView.DetailedActivity
import com.example.myapplication.LocalDB.CockTailData
import com.example.myapplication.LocalDB.DbModule
import com.example.myapplication.R
import com.example.myapplication.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private lateinit var binding: ActivityMainBinding
    var listFilter = { _ : CockTailData -> true }
    private lateinit var viewModel: MainCockTailViewModel
    private lateinit var adapter : MainCockTailAdapter

    override fun onCreate(savedInstanceState: Bundle?) {


        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        binding.materialToolbar.inflateMenu(R.menu.spinner)

        binding.materialToolbar.title = "나만의 레시피"
        val spinner = binding.materialToolbar.menu.getItem(0).actionView!! as Spinner


        DbModule()
            .provideDbInstance(this)
            .getBaseDao()
            .queryAll().observe(this) {
                val baseData = it.map {
                    it.name to it.id
                }.toMutableStateMap()

                val selections = baseData.keys.toMutableList()
                selections.add(0, "전체")
                baseData["전체"] = -1
                val spinnerAdapter = ArrayAdapter(this,
                    android.R.layout.simple_spinner_dropdown_item,
                    selections)
                adapter = MainCockTailAdapter(::detailedViewCallback)


                binding.mainRecyclerView.adapter = adapter

                viewModel = ViewModelProvider(this)[MainCockTailViewModel::class.java]


                viewModel.cockTailList.observe(this) {
                    adapter.submitList(it.filter(listFilter))
                }

                binding.add.setOnClickListener {
                    Log.d("main","button clicked")
                    startActivity(Intent(this, AddCockTail::class.java))
                }

                spinner.adapter = spinnerAdapter
                spinner.onItemSelectedListener = object: AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(
                        parent: AdapterView<*>?,
                        view: View?,
                        position: Int,
                        id: Long
                    ) {
                        val name = parent?.getItemAtPosition(position).toString()
                        listFilter = if(name == "전체") {
                            { true }
                        } else {
                            { it.baseId == baseData[name]}
                        }

                        viewModel.cockTailList.observe(this@MainActivity) {
                            adapter.submitList(it.filter(listFilter))
                        }

                        Log.d("menu", "select $name menu")

                    }
                    override fun onNothingSelected(parent: AdapterView<*>?) {
                        return
                    }
                }

            }



        setContentView(binding.root)

        binding.mainRecyclerView.layoutManager = LinearLayoutManager(this);

        binding.mainRecyclerView.addItemDecoration(DividerItemDecoration(this,
            LinearLayoutManager.VERTICAL))
    }

    private val activityLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        if(it.resultCode != 0) {
            Toast.makeText(this, "error", Toast.LENGTH_SHORT)
                .show()
        } else {
            val database = DbModule().provideDbInstance(this)
            database.getBaseDao().queryAllWithOutSubscribe().forEach {base ->
                if(database.getBaseDao().searchCockTailByBaseName(base.name).cocktails.isEmpty()) {
                    database.getBaseDao().Delete(base)
                }
            }
            viewModel.cockTailList.observe(this@MainActivity) {data ->
                adapter.submitList(data.filter(listFilter))
            }

        }
    }

    private fun detailedViewCallback(Item : CockTailData) {
        val intent = Intent(this, DetailedActivity::class.java)
        intent.putExtra("item_id", Item.id)
        activityLauncher.launch(intent)
    }


}