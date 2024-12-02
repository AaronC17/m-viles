package com.example.moviles_proyecto

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.moviles_proyecto.databinding.ItemResearchWorkBinding

class ResearchWorkAdapter(private val researchList: List<ResearchWork>) :
    RecyclerView.Adapter<ResearchWorkAdapter.ResearchWorkViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ResearchWorkViewHolder {
        val binding = ItemResearchWorkBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ResearchWorkViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ResearchWorkViewHolder, position: Int) {
        val researchWork = researchList[position]
        holder.bind(researchWork)
    }

    override fun getItemCount(): Int {
        return researchList.size
    }

    class ResearchWorkViewHolder(private val binding: ItemResearchWorkBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(researchWork: ResearchWork) {
            binding.titleTextView.text = researchWork.title
            binding.areaTextView.text = researchWork.area
            binding.descriptionTextView.text = researchWork.description
        }
    }
}
