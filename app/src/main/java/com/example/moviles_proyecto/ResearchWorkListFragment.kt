package com.example.moviles_proyecto

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentResearchWorkListBinding.inflate(inflater, container, false)

        firestore = FirebaseFirestore.getInstance()
        recyclerView = binding.recyclerView
        recyclerView.layoutManager = LinearLayoutManager(context)

        researchList = mutableListOf()
        adapter = ResearchWorkAdapter(researchList)
        recyclerView.adapter = adapter

        // Cargar los trabajos desde Firestore
        loadResearchWorks()

        return binding.root
    }

    private fun loadResearchWorks() {
        firestore.collection("research_works")
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    val research = document.toObject(ResearchWork::class.java)
                    researchList.add(research)
                }
                adapter.notifyDataSetChanged()
            }
            .addOnFailureListener { e ->
                Toast.makeText(context, "Error al cargar los trabajos", Toast.LENGTH_SHORT).show()
            }
    }
}
