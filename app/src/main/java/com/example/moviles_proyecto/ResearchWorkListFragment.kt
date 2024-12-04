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
import androidx.recyclerview.widget.RecyclerView
import com.example.moviles_proyecto.databinding.FragmentResearchWorkListBinding
import com.google.firebase.firestore.FirebaseFirestore

class ResearchWorkListFragment : Fragment() {

    private lateinit var firestore: FirebaseFirestore
    private lateinit var researchList: MutableList<ResearchWork>
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ResearchWorkAdapter
    private lateinit var gradeSpinner: Spinner

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentResearchWorkListBinding.inflate(inflater, container, false)

        // Inicializa Firebase y las listas
        firestore = FirebaseFirestore.getInstance()
        researchList = mutableListOf()

        // Configura el RecyclerView
        recyclerView = binding.recyclerView
        recyclerView.layoutManager = LinearLayoutManager(context)

        // Inicializa el adaptador y configura el clic en los elementos
        adapter = ResearchWorkAdapter(researchList) { selectedWork ->
            val bundle = Bundle().apply {
                putString("work_id", selectedWork.id) // Pasa el ID del trabajo
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
        recyclerView.adapter = adapter

        // Configura el Spinner para el filtro por grado
        gradeSpinner = binding.gradeSpinner
        val gradeOptions = listOf("Todos", "Primaria", "Secundaria", "Preparatoria", "Universidad")
        val spinnerAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, gradeOptions)
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        gradeSpinner.adapter = spinnerAdapter

        // Configura la lógica del filtro
        gradeSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val selectedGrade = gradeOptions[position]
                loadResearchWorks(if (selectedGrade == "Todos") null else selectedGrade)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        // Carga inicial: todos los trabajos
        loadResearchWorks(null)

        return binding.root
    }


    private fun loadResearchWorks(grade: String?) {
        val query = if (grade != null) {
            firestore.collection("research_works").whereEqualTo("authorGrade", grade)
        } else {
            firestore.collection("research_works")
        }

        query.get()
            .addOnSuccessListener { documents ->
                researchList.clear()
                for (document in documents) {
                    val research = document.toObject(ResearchWork::class.java)
                    research.id = document.id // Asigna el ID del documento

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
