package com.example.schedulestudent

import android.app.AlertDialog
import android.content.Context
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class RangeTargetAdapter(
    private val items: MutableList<RangeTarget>,
    private val onUpdate: (RangeTarget) -> Unit,
    private val onDelete: (RangeTarget) -> Unit,
    private val onEdit: (RangeTarget) -> Unit
) : RecyclerView.Adapter<RangeTargetAdapter.RangeViewHolder>() {

    inner class RangeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title: TextView = itemView.findViewById(R.id.tvTitle)
        val dateRange: TextView = itemView.findViewById(R.id.tvDateRange)
        val progressBar: ProgressBar = itemView.findViewById(R.id.progressBar)
        val progressText: TextView = itemView.findViewById(R.id.tvProgress)
        val btnEdit: ImageButton = itemView.findViewById(R.id.btnEdit)
        val btnDelete: ImageButton = itemView.findViewById(R.id.btnDelete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RangeViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_range_target, parent, false)
        return RangeViewHolder(view)
    }

    override fun onBindViewHolder(holder: RangeViewHolder, position: Int) {
        val item = items[position]

        holder.title.text = item.title
        holder.dateRange.text = "${item.startDate} – ${item.endDate}"
        holder.progressBar.progress = item.progress
        holder.progressText.text = "${item.progress}%"

        // EDIT → open edit screen
        holder.btnEdit.setOnClickListener {
            onEdit(item)
        }

        // DELETE → DB + UI
        holder.btnDelete.setOnClickListener {
            onDelete(item)
        }

        // CLICK ITEM → update progress
        holder.itemView.setOnClickListener {
            showUpdateProgressDialog(holder.itemView.context, item)
        }
    }

    override fun getItemCount(): Int = items.size

    fun updateList(newItems: List<RangeTarget>) {
        items.clear()
        items.addAll(newItems)
        notifyDataSetChanged()
    }

    private fun showUpdateProgressDialog(
        context: Context,
        item: RangeTarget
    ) {
        val input = EditText(context)
        input.hint = "Enter progress (0–100)"
        input.inputType = InputType.TYPE_CLASS_NUMBER

        AlertDialog.Builder(context)
            .setTitle("Update Progress")
            .setView(input)
            .setPositiveButton("Update") { _, _ ->
                val value = input.text.toString().toIntOrNull()
                if (value != null && value in 0..100) {
                    item.progress = value
                    onUpdate(item)   // 🔥 persist to DB
                    notifyDataSetChanged()
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
}
