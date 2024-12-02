package com.example.moviles_proyecto

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class AddEditResearchWorkFragment : Fragment() {

    private lateinit var firestore: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    private lateinit var titleEditText: EditText
    private lateinit var areaSpinner: Spinner
    private lateinit var descriptionEditText: EditText
    private lateinit var conclusionsEditText: EditText
    private lateinit var recommendationsEditText: EditText
    private lateinit var saveWorkButton: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_add_edit_research_work, container, false)

        firestore = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance() // Inicializamos FirebaseAuth para obtener el usuario actual
        titleEditText = view.findViewById(R.id.titleEditText)
        areaSpinner = view.findViewById(R.id.areaSpinner)
        descriptionEditText = view.findViewById(R.id.descriptionEditText)
        conclusionsEditText = view.findViewById(R.id.conclusionsEditText)
        recommendationsEditText = view.findViewById(R.id.recommendationsEditText)
        saveWorkButton = view.findViewById(R.id.saveWorkButton)

        // Configurar Spinner para las áreas de investigación
        val areas = listOf("Matemáticas", "Biología", "Ciencias Sociales")
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, areas)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        areaSpinner.adapter = adapter

        saveWorkButton.setOnClickListener {
            saveResearchWork()
        }

        return view
    }

    private fun saveResearchWork() {
        val title = titleEditText.text.toString().trim()
        val area = areaSpinner.selectedItem.toString()
        val description = descriptionEditText.text.toString().trim()
        val conclusions = conclusionsEditText.text.toString().trim()
        val recommendations = recommendationsEditText.text.toString().trim()

        if (title.isEmpty() || description.isEmpty() || conclusions.isEmpty() || recommendations.isEmpty()) {
            Toast.makeText(context, "Por favor, completa todos los campos", Toast.LENGTH_SHORT).show()
            return
        }

        val currentUser = auth.currentUser
        if (currentUser == null) {
            Toast.makeText(context, "Debes iniciar sesión para publicar un trabajo", Toast.LENGTH_SHORT).show()
            return
        }

        val researchWork = hashMapOf(
            "title" to title,
            "area" to area,
            "description" to description,
            "conclusions" to conclusions,
            "recommendations" to recommendations,
            "authorId" to currentUser.uid, // UID del autor
            "authorName" to (currentUser.displayName ?: "Usuario") // Nombre del autor
        )

        firestore.collection("research_works")
            .add(researchWork)
            .addOnSuccessListener {
                Toast.makeText(context, "Trabajo guardado exitosamente", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(context, "Error al guardar el trabajo", Toast.LENGTH_SHORT).show()
            }
    }

}
