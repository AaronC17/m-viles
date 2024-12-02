package com.example.moviles_proyecto

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.moviles_proyecto.databinding.FragmentResearchWorkDetailBinding
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.DocumentSnapshot
import org.w3c.dom.Comment

class ResearchWorkDetailFragment : Fragment() {

    private lateinit var firestore: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    private lateinit var commentsRecyclerView: RecyclerView
    private lateinit var commentEditText: EditText
    private lateinit var submitCommentButton: View
    private lateinit var commentAdapter: CommentAdapter
    private lateinit var researchWorkId: String // El ID del trabajo de investigación seleccionado

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentResearchWorkDetailBinding.inflate(inflater, container, false)

        // Inicialización
        firestore = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()
        commentsRecyclerView = binding.commentsRecyclerView
        commentEditText = binding.commentEditText
        submitCommentButton = binding.submitCommentButton

        commentsRecyclerView.layoutManager = LinearLayoutManager(context)
        commentAdapter = CommentAdapter(mutableListOf())
        commentsRecyclerView.adapter = commentAdapter

        // Obtener el ID del trabajo de investigación desde los argumentos del fragmento
        researchWorkId = arguments?.getString("researchWorkId") ?: ""

        // Cargar detalles del trabajo
        loadResearchDetails()
        // Cargar los comentarios
        loadComments()

        submitCommentButton.setOnClickListener {
            val commentText = commentEditText.text.toString().trim()
            if (commentText.isNotEmpty()) {
                submitComment(commentText)
            } else {
                Toast.makeText(context, "Por favor escribe un comentario", Toast.LENGTH_SHORT).show()
            }
        }

        return binding.root
    }

    private fun loadResearchDetails() {
        firestore.collection("research_works")
            .document(researchWorkId)
            .get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val researchWork = document.toObject(ResearchWork::class.java)
                    // Actualiza la UI con los detalles del trabajo
                    // Por ejemplo: binding.titleTextView.text = researchWork?.title
                    // Similar para otras vistas (área, descripción, conclusiones, etc.)
                }
            }
            .addOnFailureListener {
                Toast.makeText(context, "Error al cargar los detalles", Toast.LENGTH_SHORT).show()
            }
    }

    private fun loadComments() {
        firestore.collection("research_works")
            .document(researchWorkId)
            .collection("comments")
            .orderBy("timestamp", Query.Direction.ASCENDING)
            .get()
            .addOnSuccessListener { querySnapshot ->
                val comments = querySnapshot.documents.map { document ->
                    document.toObject(Comment::class.java)!!
                }
                commentAdapter.updateComments(comments)
            }
            .addOnFailureListener {
                Toast.makeText(context, "Error al cargar los comentarios", Toast.LENGTH_SHORT).show()
            }
    }

    private fun submitComment(commentText: String) {
        val comment = Comment(
            user = auth.currentUser?.email ?: "Anonimo",
            commentText = commentText,
            timestamp = System.currentTimeMillis()
        )

        firestore.collection("research_works")
            .document(researchWorkId)
            .collection("comments")
            .add(comment)
            .addOnSuccessListener {
                Toast.makeText(context, "Comentario enviado", Toast.LENGTH_SHORT).show()
                commentEditText.text.clear()
                loadComments() // Recargar los comentarios después de enviar uno nuevo
            }
            .addOnFailureListener {
                Toast.makeText(context, "Error al enviar el comentario", Toast.LENGTH_SHORT).show()
            }
    }
}
