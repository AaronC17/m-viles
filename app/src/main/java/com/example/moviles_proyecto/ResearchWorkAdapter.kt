package com.example.moviles_proyecto

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ResearchWorkAdapter(
    private var researchWorks: List<ResearchWork>, // Cambiado a 'var' para permitir actualizaciones
    private val onItemClicked: (ResearchWork) -> Unit
) : RecyclerView.Adapter<ResearchWorkAdapter.ResearchWorkViewHolder>() {

    inner class ResearchWorkViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val titleTextView: TextView = itemView.findViewById(R.id.titleTextView)
        private val areaTextView: TextView = itemView.findViewById(R.id.areaTextView)
        private val authorTextView: TextView = itemView.findViewById(R.id.authorTextView)

        fun bind(researchWork: ResearchWork) {
            // Asigna los datos al diseño
            titleTextView.text = researchWork.title
            areaTextView.text = researchWork.area
            authorTextView.text = "Autor: ${researchWork.authorName}"

            // Configura el clic para enviar el objeto completo al fragmento
            itemView.setOnClickListener { onItemClicked(researchWork) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ResearchWorkViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_research_work, parent, false)
        return ResearchWorkViewHolder(view)
    }

    override fun onBindViewHolder(holder: ResearchWorkViewHolder, position: Int) {
        holder.bind(researchWorks[position])
    }

    override fun getItemCount(): Int = researchWorks.size

    // Método para actualizar los datos del adaptador
    fun updateData(newResearchWorks: List<ResearchWork>) {
        researchWorks = newResearchWorks
        notifyDataSetChanged() // Notifica a RecyclerView que los datos han cambiado
    }
}
