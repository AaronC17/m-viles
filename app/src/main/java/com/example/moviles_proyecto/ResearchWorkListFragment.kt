package com.example.moviles_proyecto

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.moviles_proyecto.databinding.FragmentResearchWorkListBinding
import com.google.firebase.firestore.FirebaseFirestore

class ResearchWorkListFragment : Fragment() {

    private lateinit var firestore: FirebaseFirestore
    private lateinit var researchList: MutableList<ResearchWork>
    private lateinit var gradeSpinner: Spinner
    private lateinit var subjectSpinner: Spinner
    private lateinit var adapter: ResearchWorkAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentResearchWorkListBinding.inflate(inflater, container, false)

        // Inicializar Firestore y listas
        firestore = FirebaseFirestore.getInstance()
        researchList = mutableListOf()

        // Configurar RecyclerView
        binding.recyclerView.layoutManager = LinearLayoutManager(context)
        adapter = ResearchWorkAdapter(researchList) { selectedWork ->
            val bundle = Bundle().apply {
                putString("work_id", selectedWork.id)
                putString("title", selectedWork.title)
                putString("area", selectedWork.area)
                putString("description", selectedWork.description)
                putString("conclusions", selectedWork.conclusions)
                putString("recommendations", selectedWork.recommendations)
                putStringArrayList("imageUrls", ArrayList(selectedWork.imageUrls))
                putString("pdfUrl", selectedWork.pdfUrl)
                putString("authorName", selectedWork.authorName)
            }
            val fragment = ResearchWorkDetailFragment()
            fragment.arguments = bundle
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit()
        }
        binding.recyclerView.adapter = adapter

        // Inicializar Spinners
        gradeSpinner = binding.root.findViewById(R.id.gradeSpinner)
        subjectSpinner = binding.root.findViewById(R.id.subjectSpinner)

        // Configuración del Spinner de grado
        val gradeOptions = listOf("Todos", "Primaria", "Secundaria", "Preparatoria", "Universidad")
        gradeSpinner.adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, gradeOptions)

        // Configuración del Spinner de materias
        val subjectOptions = listOf("Todos", "Matemáticas", "Biología", "Química", "Física", "Historia", "Ingeniería")
        subjectSpinner.adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, subjectOptions)

        // Configurar lógica de selección de Spinners
        gradeSpinner.onItemSelectedListener = createFilterListener()
        subjectSpinner.onItemSelectedListener = createFilterListener()

        // Cargar datos iniciales
        loadResearchWorks(null, null)

        return binding.root
    }

    private fun createFilterListener() = object : AdapterView.OnItemSelectedListener {
        override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
            val selectedGrade = if (gradeSpinner.selectedItemPosition == 0) null else gradeSpinner.selectedItem.toString()
            val selectedSubject = if (subjectSpinner.selectedItemPosition == 0) null else subjectSpinner.selectedItem.toString()
            loadResearchWorks(selectedGrade, selectedSubject)
        }

        override fun onNothingSelected(parent: AdapterView<*>) {}
    }

    private fun loadResearchWorks(grade: String?, subject: String?) {
        val query = if (grade != null && subject != null) {
            firestore.collection("research_works")
                .whereEqualTo("authorGrade", grade)
                .whereEqualTo("area", subject) // Filtrar por área (materia)
        } else if (grade != null) {
            firestore.collection("research_works").whereEqualTo("authorGrade", grade)
        } else if (subject != null) {
            firestore.collection("research_works").whereEqualTo("area", subject) // Filtrar por área (materia)
        } else {
            firestore.collection("research_works")
        }

        query.get()
            .addOnSuccessListener { documents ->
                researchList.clear()
                for (document in documents) {
                    val research = document.toObject(ResearchWork::class.java)
                    research.id = document.id

                    // Log para verificar los datos cargados
                    Log.d("ResearchWorkList", "ID: ${research.id}")
                    Log.d("ResearchWorkList", "PDF URL: ${research.pdfUrl}")
                    Log.d("ResearchWorkList", "Image URLs: ${research.imageUrls}")

                    researchList.add(research)
                }
                adapter.notifyDataSetChanged()
            }
            .addOnFailureListener { e ->
                Toast.makeText(context, "Error al cargar los trabajos: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}
