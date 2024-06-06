package com.example.myapplication.RemoteRatingView

import android.os.Bundle
import androidx.activity.ComponentActivity
import com.example.myapplication.databinding.DetailedViewReadonlyBinding
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DetailedViewWithRemote : ComponentActivity() {
    private lateinit var binding: DetailedViewReadonlyBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DetailedViewReadonlyBinding.inflate(layoutInflater)
        binding.materialToolbar2.title = "상세보기"


        binding.addShortDesc.setHorizontallyScrolling(false)
        binding.addRecipeText.setHorizontallyScrolling(false)
        val itemId = intent.extras!!.getInt("item_id")
        Firebase.firestore
            .collection("cocktails")
            .whereEqualTo("id", itemId)
            .get()
            .addOnSuccessListener {
                val it = it.documents.get(0)

                val baseId = it.get("baseId")!!.toString().toInt()
                val desc = it.get("desc")!!.toString()
                val name = it.get("name")!!.toString()
                val recipe = it.get("recipe")!!.toString()
                Firebase.firestore.collection("base")
                    .whereEqualTo("id", baseId)
                    .get()
                    .addOnSuccessListener {
                        val it = it.documents.get(0)
                        val baseTxt = it.get("name")!!.toString()
                        binding.addName.setText(name)

                        binding.addRecipeText.setText(recipe)
                        binding.addShortDesc.setText(desc)
                        binding.addBaseTxt.setText(baseTxt)
                    }


         }



        setContentView(binding.root)
    }
}