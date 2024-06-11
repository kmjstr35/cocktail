package com.example.myapplication.RemoteRatingView

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
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myapplication.LocalDB.CockTailData
import com.example.myapplication.R
import com.example.myapplication.databinding.RemoteRatingBinding
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch

class RemoteRatingActivity : ComponentActivity() {
    private lateinit var binding: RemoteRatingBinding
    var listFilter = { _ : CockTailData -> true }
    override fun onCreate(savedInstanceState: Bundle?) {
        binding = RemoteRatingBinding.inflate(layoutInflater)
        binding.materialToolbar.inflateMenu(R.menu.spinner)

        binding.materialToolbar.title = "칵테일 찾기"
        val spinner = binding.materialToolbar.menu.getItem(0).actionView!! as Spinner



        binding.mainRecyclerView.layoutManager = LinearLayoutManager(this);

        binding.mainRecyclerView.addItemDecoration(
            DividerItemDecoration(this,
            LinearLayoutManager.VERTICAL)
        )

        val adapter = RemoteRatingAdapter(::detailedViewCallback)


        binding.mainRecyclerView.adapter = adapter
        super.onCreate(savedInstanceState)

        val viewModel = ViewModelProvider(this)[RemoteRatingViewModel::class.java]

        lifecycleScope.launch {
            viewModel.collection.flowWithLifecycle(lifecycle).collect {

                list -> adapter.submitList(list.filter(listFilter))
            }
        }

        Firebase.firestore
            .collection("base")
            .get()
            .addOnSuccessListener {
                var baseDatum =  it.map {
                    val id = it.get("id").toString().toInt()
                    val base = it.get("name").toString()

                    return@map Pair(base, id)
                }.toMutableStateMap()


                val spinnerSelections = baseDatum.keys.toMutableList()

                spinnerSelections.add(0, "전체")
                baseDatum["전체"] = -1

                val spinnerAdapter = ArrayAdapter(this,
                    android.R.layout.simple_spinner_dropdown_item,
                    spinnerSelections)

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
                            { it.baseId == baseDatum[name]}
                        }

                        Log.d("menu", "select $name menu")

                        lifecycleScope.launch {
                            viewModel.collection.flowWithLifecycle(lifecycle).collect {

                                    list -> adapter.submitList(list.filter(listFilter))
                            }
                        }
                    }
                    override fun onNothingSelected(parent: AdapterView<*>?) {
                        return
                    }

                }
            }

        setContentView(binding.root)
    }

    private val activityLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        if(it.resultCode != 0) {
            Toast.makeText(this, "error", Toast.LENGTH_SHORT)
                .show()
        }
    }
    private fun detailedViewCallback(Item : CockTailData) {
        val intent = Intent(this, DetailedViewWithRemote::class.java)
        intent.putExtra("item_id", Item.id)
        activityLauncher.launch(intent)
    }
}

