package com.example.myapplication.DetailedView

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import com.bumptech.glide.Glide
import com.example.myapplication.EditCockTail.EditCockTail
import com.example.myapplication.LocalDB.CockTailLocalDB
import com.example.myapplication.LocalDB.DbModule
import com.example.myapplication.R
import com.example.myapplication.databinding.DetailedViewReadonlyBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DetailedActivity: ComponentActivity() {
    private lateinit var binding: DetailedViewReadonlyBinding
    private lateinit var database: CockTailLocalDB

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        database = DbModule().provideDbInstance(this)
        binding = DetailedViewReadonlyBinding.inflate(layoutInflater)


        binding.materialToolbar2.inflateMenu(R.menu.edit_menu)

        binding.materialToolbar2.title = "상세보기"

        val editBtn = binding.materialToolbar2.menu.findItem(R.id.edit)
        val itemId = intent.extras!!.getInt("item_id")

        editBtn.setOnMenuItemClickListener {
            Log.d("edit btn", "clicked")
            val intent = Intent(this, EditCockTail::class.java)
            intent.putExtra("item_id", itemId)

            startActivity(intent)
            return@setOnMenuItemClickListener true
        }


        Log.d("detailed view", "show $itemId")
        val data = database.getCocktailDao().findRowById(itemId)

        data.observe(this) { data ->
            val baseTxt = database.getBaseDao().findById(data.baseId)

            binding.addName.setText(data.name)
            binding.addRecipeText.setText(data.recipe)
            binding.addShortDesc.setText(data.desc)
            binding.addBaseTxt.setText(baseTxt)

            if (data.imagePath != null) {
                Glide.with(this)
                    .load(data.imagePath)
                    .into(binding.addImage2)
            }
        }
        setContentView(binding.root)

    }
}