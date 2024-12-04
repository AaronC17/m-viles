package com.example.moviles_proyecto

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.moviles_proyecto.databinding.FragmentResearchWorkDetailBinding
import com.google.firebase.firestore.FirebaseFirestore

class ResearchWorkDetailFragment : Fragment() {

    private lateinit var binding: FragmentResearchWorkDetailBinding
    private lateinit var firestore: FirebaseFirestore
    private lateinit var commentsAdapter: CommentAdapter

    private var imageUrls: List<String> = emptyList()
    private var pdfUrl: String? = null
    private var workId: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentResearchWorkDetailBinding.inflate(inflater, container, false)
        firestore = FirebaseFirestore.getInstance()

        // Configura RecyclerView para comentarios
        configureCommentsRecyclerView()

        // Carga los datos del trabajo
        loadArguments()

        // Configura el RecyclerView para imágenes
        configureImageRecyclerView()

        // Configura el botón para ver PDF
        configurePdfButton()

        // Configura el envío de comentarios
        configureCommentSubmission()

        // Carga los comentarios desde Firebase
        loadComments()

        return binding.root
    }

    private fun loadArguments() {
        binding.titleTextView.text = arguments?.getString("title")
        binding.areaTextView.text = arguments?.getString("area")
        binding.descriptionTextView.text = arguments?.getString("description")
        binding.conclusionsTextView.text = arguments?.getString("conclusions")
        binding.recommendationsTextView.text = arguments?.getString("recommendations")
        pdfUrl = arguments?.getString("pdfUrl")
        imageUrls = arguments?.getStringArrayList("imageUrls") ?: emptyList()
        workId = arguments?.getString("work_id")

        if (pdfUrl.isNullOrEmpty()) {
            binding.viewPdfButton.isEnabled = false
            Toast.makeText(context, "El PDF no está disponible", Toast.LENGTH_SHORT).show()
        }
        if (imageUrls.isEmpty()) {
            Toast.makeText(context, "No hay imágenes disponibles", Toast.LENGTH_SHORT).show()
        }
    }

    private fun configurePdfButton() {
        if (!pdfUrl.isNullOrEmpty()) {
            binding.viewPdfButton.setOnClickListener {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(pdfUrl))
                startActivity(intent)
            }
        }
    }

    private fun configureImageRecyclerView() {
        binding.imagesRecyclerView.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        if (imageUrls.isNotEmpty()) {
            binding.imagesRecyclerView.adapter = ImageAdapter(imageUrls)
        }
    }

    private fun configureCommentsRecyclerView() {
        binding.commentsRecyclerView.layoutManager = LinearLayoutManager(context)
        commentsAdapter = CommentAdapter(mutableListOf())
        binding.commentsRecyclerView.adapter = commentsAdapter
    }

    private fun configureCommentSubmission() {
        binding.submitCommentButton.setOnClickListener {
            val commentText = binding.commentEditText.text.toString().trim()
            val rating = binding.commentRatingBar.rating
            if (commentText.isNotEmpty() && rating > 0) {
                addCommentToFirebase(commentText, rating)
            } else {
                Toast.makeText(context, "Escribe un comentario y asigna una calificación", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun addCommentToFirebase(comment: String, rating: Float) {
        if (workId.isNullOrEmpty()) {
            Toast.makeText(context, "No se puede agregar un comentario sin el ID del trabajo", Toast.LENGTH_SHORT).show()
            return
        }

        val commentData = hashMapOf(
            "user" to "Anónimo", // Cambia esto si tienes usuarios registrados
            "comment" to comment,
            "rating" to rating,
            "timestamp" to System.currentTimeMillis()
        )

        firestore.collection("research_works")
            .document(workId!!)
            .collection("comments")
            .add(commentData)
            .addOnSuccessListener {
                Toast.makeText(context, "Comentario agregado exitosamente", Toast.LENGTH_SHORT).show()
                binding.commentEditText.text.clear()
                binding.commentRatingBar.rating = 0f
                loadComments() // Recarga los comentarios después de agregar uno nuevo
            }
            .addOnFailureListener { e ->
                Toast.makeText(context, "Error al agregar el comentario: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun loadComments() {
        if (workId.isNullOrEmpty()) {
            Toast.makeText(context, "No se pueden cargar los comentarios sin el ID del trabajo", Toast.LENGTH_SHORT).show()
            return
        }

        firestore.collection("research_works")
            .document(workId!!)
            .collection("comments")
            .orderBy("timestamp")
            .get()
            .addOnSuccessListener { documents ->
                val comments = documents.map { document ->
                    Comment(
                        user = document.getString("user") ?: "Anónimo",
                        commentText = document.getString("comment") ?: "",
                        rating = document.getDouble("rating")?.toInt() ?: 0,
                        timestamp = document.getLong("timestamp") ?: 0L
                    )
                }
                commentsAdapter.updateComments(comments)
            }
            .addOnFailureListener { e ->
                Toast.makeText(context, "Error al cargar los comentarios: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}
