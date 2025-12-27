package com.example.schedulestudent

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.CheckBox
import androidx.recyclerview.widget.RecyclerView

class SubtopicsCheckboxAdapter(
    private val items: MutableList<SubtopicEntity>,
    private val onChecked: (SubtopicEntity) -> Unit
) : RecyclerView.Adapter<SubtopicsCheckboxAdapter.ViewHolder>() {

    class ViewHolder(val checkBox: CheckBox) :
        RecyclerView.ViewHolder(checkBox)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val cb = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_subtopic_checkbox, parent, false) as CheckBox
        return ViewHolder(cb)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.checkBox.text = item.title
        holder.checkBox.setOnCheckedChangeListener(null)
        holder.checkBox.isChecked = item.isCompleted

        holder.checkBox.setOnCheckedChangeListener { _, isChecked ->
            items[position] = item.copy(isCompleted = isChecked)
            onChecked(items[position])
        }
    }

    override fun getItemCount(): Int = items.size
}
