package com.example.moviles_proyecto

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.moviles_proyecto.databinding.FragmentResearchWorkDetailBinding
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class ResearchWorkDetailFragment : Fragment() {

    private lateinit var firestore: FirebaseFirestore
    private lateinit var commentAdapter: CommentAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var commentEditText: EditText
    private lateinit var submitCommentButton: Button
    private var comments: MutableList<Comment> = mutableListOf()

    // Views para los detalles del trabajo
    private lateinit var titleTextView: TextView
    private lateinit var areaTextView: TextView
    private lateinit var descriptionTextView: TextView
    private lateinit var conclusionsTextView: TextView
    private lateinit var recommendationsTextView: TextView

    private var workId: String? = null // ID del trabajo recibido desde los argumentos

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentResearchWorkDetailBinding.inflate(inflater, container, false)

        firestore = FirebaseFirestore.getInstance()
        recyclerView = binding.commentsRecyclerView
        commentEditText = binding.commentEditText
        submitCommentButton = binding.submitCommentButton

        // Views para los detalles del trabajo
        titleTextView = binding.titleTextView
        areaTextView = binding.areaTextView
        descriptionTextView = binding.descriptionTextView
        conclusionsTextView = binding.conclusionsTextView
        recommendationsTextView = binding.recommendationsTextView

        recyclerView.layoutManager = LinearLayoutManager(context)
        commentAdapter = CommentAdapter(comments)
        recyclerView.adapter = commentAdapter

        // Obtener los argumentos pasados al fragmento
        workId = arguments?.getString("work_id")
        titleTextView.text = arguments?.getString("title")
        areaTextView.text = arguments?.getString("area")
        descriptionTextView.text = arguments?.getString("description")
        conclusionsTextView.text = arguments?.getString("conclusions")
        recommendationsTextView.text = arguments?.getString("recommendations")

        // Cargar comentarios desde Firebase
        loadComments()

        // Configurar botón de enviar comentario
        submitCommentButton.setOnClickListener {
            val newCommentText = commentEditText.text.toString().trim()
            if (newCommentText.isNotEmpty() && workId != null) {
                val newComment = Comment(user = "Anonymous", commentText = newCommentText)
                saveCommentToFirestore(newComment)
            } else {
                Toast.makeText(context, "Error: no se puede enviar el comentario", Toast.LENGTH_SHORT).show()
            }
        }

        return binding.root
    }

    // Cargar comentarios desde Firestore
    private fun loadComments() {
        if (workId == null) {
            Toast.makeText(context, "Error: ID del trabajo no encontrado", Toast.LENGTH_SHORT).show()
            return
        }

        firestore.collection("research_works")
            .document(workId!!)
            .collection("comments")
            .orderBy("timestamp", Query.Direction.ASCENDING) // Ordenar por timestamp (si está disponible)
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
        if (workId == null) {
            Toast.makeText(context, "Error: ID del trabajo no encontrado", Toast.LENGTH_SHORT).show()
            return
        }

        firestore.collection("research_works")
            .document(workId!!)
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
