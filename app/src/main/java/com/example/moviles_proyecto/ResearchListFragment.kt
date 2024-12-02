package com.example.moviles_proyecto

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Spinner
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.moviles_proyecto.databinding.FragmentResearchListBinding
import com.google.firebase.firestore.FirebaseFirestore

class ResearchListFragment : Fragment() {

    private lateinit var firestore: FirebaseFirestore
    private lateinit var topicSpinner: Spinner
    private lateinit var gradeSpinner: Spinner
    private lateinit var workButton1: Button
    private lateinit var workButton2: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentResearchListBinding.inflate(inflater, container, false)

        firestore = FirebaseFirestore.getInstance()

        topicSpinner = binding.topicSpinner
        gradeSpinner = binding.gradeSpinner
        workButton1 = binding.workButton1
        workButton2 = binding.workButton2

        // Cargar los trabajos desde Firestore
        loadResearchPapers()

        // Acción al hacer clic en los botones de trabajo
        workButton1.setOnClickListener {
            openResearchDetails("1")  // Pasamos el ID del trabajo (por ejemplo, "1")
        }
        workButton2.setOnClickListener {
            openResearchDetails("2")  // Pasamos el ID del trabajo (por ejemplo, "2")
        }

        return binding.root
    }

    private fun loadResearchPapers() {
        firestore.collection("research_papers")
            .get()
            .addOnSuccessListener { result ->
                // Aquí se podrían actualizar los botones o añadir más
                // trabajoButton1.text = result.first().getString("title") o algo similar
            }
            .addOnFailureListener {
                Toast.makeText(requireContext(), "Error al cargar los trabajos", Toast.LENGTH_SHORT).show()
            }
    }

    private fun openResearchDetails(researchId: String) {
        val fragment = ResearchDetailsFragment()
        val bundle = Bundle()
        bundle.putString("researchId", researchId)
        fragment.arguments = bundle
        parentFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .addToBackStack(null)
            .commit()
    }
}
