package com.example.moviles_proyecto

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RatingBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class CommentAdapter(private var comments: MutableList<Comment>) :
    RecyclerView.Adapter<CommentAdapter.CommentViewHolder>() {

    // ViewHolder para los comentarios
    inner class CommentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val userTextView: TextView = itemView.findViewById(R.id.userTextView)
        val commentTextView: TextView = itemView.findViewById(R.id.commentTextView)
        val commentRatingBar: RatingBar = itemView.findViewById(R.id.commentRatingBar)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_comment, parent, false)
        return CommentViewHolder(view)
    }

    override fun onBindViewHolder(holder: CommentViewHolder, position: Int) {
        val comment = comments[position]

        // Asignar valores del comentario al ViewHolder
        holder.userTextView.text = comment.user
        holder.commentTextView.text = comment.commentText
        holder.commentRatingBar.rating = comment.rating.toFloat()
    }

    override fun getItemCount(): Int = comments.size

    // Actualizar los comentarios del adaptador
    fun updateComments(newComments: List<Comment>) {
        comments.clear()
        comments.addAll(newComments)
        notifyDataSetChanged()
    }
}
