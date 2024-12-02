package com.example.moviles_proyecto

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.moviles_proyecto.databinding.ItemResearchWorkBinding

class ResearchWorkAdapter(private val researchList: List<ResearchWork>) :
    RecyclerView.Adapter<ResearchWorkAdapter.ResearchWorkViewHolder>() {

    class ResearchWorkViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val titleTextView: TextView = itemView.findViewById(R.id.titleTextView)
        val areaTextView: TextView = itemView.findViewById(R.id.areaTextView)
        val authorTextView: TextView = itemView.findViewById(R.id.authorTextView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ResearchWorkViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_research_work, parent, false)
        return ResearchWorkViewHolder(view)
    }

    override fun onBindViewHolder(holder: ResearchWorkViewHolder, position: Int) {
        val researchWork = researchList[position]
        holder.titleTextView.text = researchWork.title
        holder.areaTextView.text = researchWork.area
        holder.authorTextView.text = "Autor: ${researchWork.authorName ?: "Anónimo"}"
    }

    override fun getItemCount(): Int = researchList.size
}
