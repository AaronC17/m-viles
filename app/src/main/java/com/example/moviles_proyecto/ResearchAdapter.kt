package com.example.moviles_proyecto

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.moviles_proyecto.databinding.ItemResearchBinding

class ResearchAdapter(
    private val researchList: List<ResearchPaper>,
    private val onItemClick: (ResearchPaper) -> Unit
) : RecyclerView.Adapter<ResearchAdapter.ResearchViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ResearchViewHolder {
        // Inflar el layout de la vista de cada ítem en el RecyclerView
        val binding = ItemResearchBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ResearchViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ResearchViewHolder, position: Int) {
        // Obtener el ResearchPaper de la lista y vincularlo al ViewHolder
        val researchPaper = researchList[position]
        holder.bind(researchPaper)
    }

    override fun getItemCount(): Int = researchList.size

    inner class ResearchViewHolder(private val binding: ItemResearchBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(researchPaper: ResearchPaper) {
            // Asignar los datos a las vistas del layout
            binding.titleTextView.text = researchPaper.title
            binding.descriptionTextView.text = researchPaper.description
            binding.topicTextView.text = researchPaper.topic
            binding.gradeTextView.text = researchPaper.grade

            // Configurar el clic sobre el item del RecyclerView
            binding.root.setOnClickListener {
                onItemClick(researchPaper)
            }
        }
    }
}
