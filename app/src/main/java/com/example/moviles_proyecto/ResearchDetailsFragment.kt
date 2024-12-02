package com.example.moviles_proyecto

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.RatingBar
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.moviles_proyecto.databinding.FragmentResearchDetailsBinding
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FieldValue


class ResearchDetailsFragment : Fragment() {

    private lateinit var firestore: FirebaseFirestore
    private lateinit var titleTextView: TextView
    private lateinit var descriptionTextView: TextView
    private lateinit var gradeTextView: TextView
    private lateinit var topicTextView: TextView
    private lateinit var commentsTextView: TextView
    private lateinit var commentEditText: EditText
    private lateinit var addCommentButton: Button
    private var researchId: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Usamos el ViewBinding correctamente con el nombre generado
        val binding = FragmentResearchDetailsBinding.inflate(inflater, container, false)

        firestore = FirebaseFirestore.getInstance()

        researchId = arguments?.getString("researchId")

        // Inicializar las vistas con el binding
        titleTextView = binding.titleTextView
        descriptionTextView = binding.descriptionTextView
        gradeTextView = binding.gradeTextView
        topicTextView = binding.topicTextView
        commentsTextView = binding.commentsTextView
        commentEditText = binding.commentEditText
        addCommentButton = binding.addCommentButton

        // Cargar los detalles del trabajo de investigación
        loadResearchDetails()

        // Acción para agregar comentario
        addCommentButton.setOnClickListener {
            val comment = commentEditText.text.toString()
            if (comment.isNotEmpty()) {
                addComment(comment)
            }
        }

        return binding.root
    }

    private fun loadResearchDetails() {
        researchId?.let {
            firestore.collection("research_papers").document(it)
                .get()
                .addOnSuccessListener { document ->
                    titleTextView.text = document.getString("title")
                    descriptionTextView.text = document.getString("description")
                    gradeTextView.text = document.getString("grade")
                    topicTextView.text = document.getString("topic")
                    commentsTextView.text = document.getString("comments") ?: "No hay comentarios"
                }
        }
    }

    private fun addComment(comment: String) {
        researchId?.let {
            // Usar arrayUnion para agregar un nuevo comentario al campo "comments"
            firestore.collection("research_papers").document(it)
                .update("comments", FieldValue.arrayUnion(comment))
                .addOnSuccessListener {
                    Toast.makeText(requireContext(), "Comentario agregado", Toast.LENGTH_SHORT).show()
                    loadResearchDetails() // Recargar los detalles para mostrar el nuevo comentario
                }
                .addOnFailureListener {
                    Toast.makeText(requireContext(), "Error al agregar comentario", Toast.LENGTH_SHORT).show()
                }
        }
    }
}
