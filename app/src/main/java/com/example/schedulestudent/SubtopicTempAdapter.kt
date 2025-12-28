package com.example.schedulestudent

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class SubtopicTempAdapter(
    private val subtopics: MutableList<EditableSubtopic>
)
 : RecyclerView.Adapter<SubtopicTempAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title: TextView = itemView.findViewById(R.id.tvSubtopicTitle)
        val remove: ImageView = itemView.findViewById(R.id.ivRemove)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_subtopic_temp, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.title.text = subtopics[position].title


        holder.remove.setOnClickListener {
            subtopics.removeAt(position)
            notifyItemRemoved(position)
            notifyItemRangeChanged(position, subtopics.size)
        }
    }

    override fun getItemCount(): Int = subtopics.size
}
