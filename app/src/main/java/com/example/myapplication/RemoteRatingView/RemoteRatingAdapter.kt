package com.example.myapplication.RemoteRatingView

import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.LocalDB.CockTailData
import com.example.myapplication.MainWindow.CockTailDataDiff

import com.example.myapplication.databinding.RemoteRatingItemBinding
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject

//
class RemoteRatingAdapter(val itemClickCallback : (CockTailData) -> Unit)
    : ListAdapter<CockTailData, RemoteRatingViewHolder>(CockTailDataDiff()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RemoteRatingViewHolder =
        RemoteRatingViewHolder(
            RemoteRatingItemBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    override fun onBindViewHolder(holder: RemoteRatingViewHolder, position: Int) {
        val current = getItem(position)

        holder.itemView.setOnClickListener {
            itemClickCallback(getItem(position))
        }

        holder.binding.CockTailNameStr.text = current.name
        holder.binding.starCount.text = "${current.rating}"
        if(current.imagePath != null)
            holder.binding.imageView2.setImageURI(Uri.parse(current.imagePath))
        }
    }


class RemoteRatingViewHolder(val binding: RemoteRatingItemBinding,
)
    : RecyclerView.ViewHolder(binding.root)

@HiltViewModel
class RemoteRatingViewModel
@Inject constructor (val handle : SavedStateHandle) : ViewModel() {

    val collection: Flow<List<CockTailData>> =
         callbackFlow {
            val subscription = Firebase.firestore
                .collection("cocktails")
                .orderBy("rating", Query.Direction.DESCENDING)
                .addSnapshotListener { query, error ->
                    if(error != null) { // handling error
                        Log.d("db", "connection error")
                        return@addSnapshotListener
                    }
                    val docs = query?.documents?.map {
                        val baseId = it.get("baseId")!!.toString().toInt()
                        val desc = it.get("desc")!!.toString()
                        val name = it.get("name")!!.toString()
                        val recipe = it.get("recipe")!!.toString()
                        val rating = it.get("rating")!!.toString().toInt()
                        val id = it.get("id")!!.toString().toInt()
                        CockTailData(
                            id = id,
                            desc = desc,
                            baseId = baseId,
                            imagePath = null,
                            name = name,
                            recipe =  recipe,
                            rating = rating)
                    }

                    trySend(docs!!)
                }

            awaitClose { subscription.remove() }
        }
}