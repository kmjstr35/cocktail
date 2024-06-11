package com.example.myapplication.RemoteRatingView

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import com.example.myapplication.R
import com.example.myapplication.databinding.DetailedViewReadonlyBinding
import com.google.firebase.firestore.AggregateSource
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DetailedViewWithRemote : ComponentActivity() {
    private lateinit var binding: DetailedViewReadonlyBinding
    var starState = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DetailedViewReadonlyBinding.inflate(layoutInflater)
        binding.materialToolbar2.inflateMenu(R.menu.iine_menu)
        binding.materialToolbar2.title = "상세보기"



        binding.addShortDesc.setHorizontallyScrolling(false)
        binding.addRecipeText.setHorizontallyScrolling(false)
        val itemId = intent.extras!!.getInt("item_id")


        Firebase.firestore.collection("likes")
            .whereEqualTo("cockId", itemId)
            .count().get(AggregateSource.SERVER).addOnCompleteListener {
                if(it.isSuccessful) {
                    val cnt = it.result.count
                    starState = cnt.toInt() != 0

                    if(starState) {
                        binding.materialToolbar2.menu.getItem(0).setIcon(R.drawable.star)
                    } else {
                        binding.materialToolbar2.menu.getItem(0).setIcon(R.drawable.star_outline)
                    }

                    binding.materialToolbar2.menu.getItem(0).setOnMenuItemClickListener {
                            menu ->
                        if(!starState) {
                            Firebase.firestore.collection("likes")
                                .add(hashMapOf("cockId" to itemId))

                            Firebase.firestore.collection("cocktails")
                                .whereEqualTo("id", itemId)
                                .get().addOnSuccessListener {
                                    it.documents.forEach {
                                        val prev = it.get("rating").toString().toInt()
                                        it.reference.update("rating" , prev + 1)
                                            .addOnSuccessListener {
                                                menu.setIcon(R.drawable.star)
                                                starState = !starState
                                                Log.d("update plus", "success")
                                            }
                                    }
                                }

                        } else {


                            Firebase.firestore.collection("likes")
                                .whereEqualTo("cockId", itemId)
                                .get().addOnSuccessListener {
                                    it.documents.forEach {
                                        it.reference.delete()
                                    }
                                }

                            Firebase.firestore.collection("cocktails")
                                .whereEqualTo("id", itemId)
                                .get().addOnSuccessListener {
                                    it.documents.forEach {
                                        val prev = it.get("rating").toString().toInt()
                                        it.reference.update("rating" , prev - 1)
                                            .addOnSuccessListener {
                                                Log.d("update minus", "success")
                                                menu.setIcon(R.drawable.star_outline)
                                                starState = !starState
                                            }
                                    }
                                }
                        }
                        return@setOnMenuItemClickListener false
                    }
                }
            }
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