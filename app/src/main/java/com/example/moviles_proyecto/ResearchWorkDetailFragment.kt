package com.example.moviles_proyecto

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.moviles_proyecto.databinding.FragmentResearchWorkDetailBinding
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.DocumentSnapshot
import com.example.moviles_proyecto.Comment


class ResearchWorkDetailFragment : Fragment() {

    private lateinit var firestore: FirebaseFirestore
    private lateinit var commentAdapter: CommentAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var commentEditText: EditText
    private lateinit var submitCommentButton: Button
    private var comments: MutableList<Comment> = mutableListOf()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentResearchWorkDetailBinding.inflate(inflater, container, false)

        firestore = FirebaseFirestore.getInstance()
        recyclerView = binding.commentsRecyclerView
        commentEditText = binding.commentEditText
        submitCommentButton = binding.submitCommentButton

        recyclerView.layoutManager = LinearLayoutManager(context)
        commentAdapter = CommentAdapter(comments)
        recyclerView.adapter = commentAdapter

        // Cargar comentarios desde Firebase
        loadComments()

        // Configurar botón de enviar comentario
        submitCommentButton.setOnClickListener {
            val newCommentText = commentEditText.text.toString().trim()
            if (newCommentText.isNotEmpty()) {
                val newComment = Comment(user = "Anonymous", commentText = newCommentText)
                saveCommentToFirestore(newComment)
            }
        }

        return binding.root
    }

    // Cargar comentarios desde Firestore
    private fun loadComments() {
        firestore.collection("research_works")
            .document("work_id") // Aquí debes colocar el ID del trabajo
            .collection("comments")
            .get()
            .addOnSuccessListener { documents ->
                comments.clear()
                for (document in documents) {
                    val comment = document.toObject(Comment::class.java)
                    comments.add(comment)
                }
                commentAdapter.notifyDataSetChanged()
            }
            .addOnFailureListener { e ->
                Toast.makeText(context, "Error al cargar los comentarios", Toast.LENGTH_SHORT).show()
            }
    }

    // Guardar un nuevo comentario en Firestore
    private fun saveCommentToFirestore(comment: Comment) {
        val workId = "work_id" // Aquí debes colocar el ID del trabajo
        firestore.collection("research_works")
            .document(workId)
            .collection("comments")
            .add(comment)
            .addOnSuccessListener {
                comments.add(comment)
                commentAdapter.notifyItemInserted(comments.size - 1)
                commentEditText.text.clear()
            }
            .addOnFailureListener { e ->
                Toast.makeText(context, "Error al enviar el comentario", Toast.LENGTH_SHORT).show()
            }
    }
}

