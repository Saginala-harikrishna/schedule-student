package com.example.schedulestudent

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class SubtopicTempAdapter(
    private val subtopics: List<String>
) : RecyclerView.Adapter<SubtopicTempAdapter.SubtopicViewHolder>() {

    class SubtopicViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title: TextView = itemView.findViewById(R.id.tvSubtopicTitle)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SubtopicViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_subtopic_temp, parent, false)
        return SubtopicViewHolder(view)
    }

    override fun onBindViewHolder(holder: SubtopicViewHolder, position: Int) {
        holder.title.text = subtopics[position]
    }

    override fun getItemCount(): Int = subtopics.size
}
