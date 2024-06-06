package com.example.myapplication.MainWindow
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.example.myapplication.LocalDB.CockTailData
import com.example.myapplication.databinding.MainCocktailRecyclerviewItemBinding

class MainCockTailAdapter(val itemClickCallback : (CockTailData) -> Unit)
    : ListAdapter<CockTailData, MainCockTailViewHolder>(CockTailDataDiff()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainCockTailViewHolder
    = MainCockTailViewHolder(
        MainCocktailRecyclerviewItemBinding.inflate(LayoutInflater.from(parent.context),
            parent,
            false)
        )

    override fun onBindViewHolder(holder: MainCockTailViewHolder, position: Int) {
        val current = getItem(position)

        holder.itemView.setOnClickListener {
            itemClickCallback(getItem(position))
        }

        holder.binding.CockTailNameStr.text = current.name
        if(current.imagePath != null)
            holder.binding.imageView2.setImageURI(Uri.parse(current.imagePath))
    }

}

class CockTailDataDiff : DiffUtil.ItemCallback<CockTailData>() {
    override fun areItemsTheSame(oldItem: CockTailData, newItem: CockTailData): Boolean {
        return oldItem == newItem;
    }

    override fun areContentsTheSame(oldItem: CockTailData, newItem: CockTailData): Boolean {
        return oldItem.name == newItem.name
                && oldItem.desc == newItem.desc
                && oldItem.imagePath == newItem.imagePath
                && oldItem.baseId == newItem.baseId;
    }
}