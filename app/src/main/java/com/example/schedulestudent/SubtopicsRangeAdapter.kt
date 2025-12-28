package com.example.schedulestudent

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class SubtopicsRangeAdapter(
    private val items: List<SubtopicsRangeUiModel>,
    private val onItemClick: (SubtopicsRangeUiModel) -> Unit,
    private val onEditClick: (SubtopicsRangeUiModel) -> Unit,
    private val onDeleteClick: (SubtopicsRangeUiModel) -> Unit
) : RecyclerView.Adapter<SubtopicsRangeAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title: TextView = itemView.findViewById(R.id.tvTitle)
        val dates: TextView = itemView.findViewById(R.id.tvDates)
        val progressText: TextView = itemView.findViewById(R.id.tvProgressSummary)
        val progressBar: ProgressBar = itemView.findViewById(R.id.progressBar)

        val editIcon: ImageView = itemView.findViewById(R.id.ivEdit)

        val deleteIcon: ImageView = itemView.findViewById(R.id.ivDelete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_subtopics_range, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]

        holder.title.text = item.title
        holder.dates.text = "${item.startDate} - ${item.endDate}"
        holder.progressText.text =
            "${item.completedCount} / ${item.totalCount} completed (${item.progressPercent}%)"
        holder.progressBar.progress = item.progressPercent

        // Open details
        holder.itemView.setOnClickListener {
            onItemClick(item)
        }


        holder.editIcon.setOnClickListener {
            onEditClick(item)
        }


        // Delete icon click (NO deletion yet)
        holder.deleteIcon.setOnClickListener {
            onDeleteClick(item)
        }
    }

    override fun getItemCount(): Int = items.size
}
