package com.example.moviles_proyecto

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.example.moviles_proyecto.databinding.ItemImageBinding

class UriImageAdapter(private val imageUris: List<Uri>) :
    RecyclerView.Adapter<UriImageAdapter.UriImageViewHolder>() {

    class UriImageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.imageView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UriImageViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_image, parent, false)
        return UriImageViewHolder(view)
    }

    override fun onBindViewHolder(holder: UriImageViewHolder, position: Int) {
        val uri = imageUris[position]
        holder.imageView.setImageURI(uri)
    }

    override fun getItemCount(): Int = imageUris.size
}
