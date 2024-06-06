package com.example.myapplication.MainWindow
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myapplication.AddCockTail.AddCockTail
import com.example.myapplication.DetailedView.DetailedActivity
import com.example.myapplication.LocalDB.CockTailData
import com.example.myapplication.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {


        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)

        binding.materialToolbar.title = "나만의 레시피"

        val adapter = MainCockTailAdapter(::detailedViewCallback)
        setContentView(binding.root)

        binding.mainRecyclerView.layoutManager = LinearLayoutManager(this);

        binding.mainRecyclerView.addItemDecoration(DividerItemDecoration(this,
            LinearLayoutManager.VERTICAL))

        binding.mainRecyclerView.adapter = adapter

        val viewModel = ViewModelProvider(this)[MainCockTailViewModel::class.java]

        viewModel.cockTailList.observe(this) {
            adapter.submitList(it)
        }

        binding.add.setOnClickListener {
            Log.d("main","button clicked")
            startActivity(Intent(this, AddCockTail::class.java))
        }

    }

    private val activityLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        if(it.resultCode != 0) {
            Toast.makeText(this, "error", Toast.LENGTH_SHORT)
                .show()
        }
    }

    private fun detailedViewCallback(Item : CockTailData) {
        val intent = Intent(this, DetailedActivity::class.java)
        intent.putExtra("item_id", Item.id)
        activityLauncher.launch(intent)
    }


}