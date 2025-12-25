package com.example.schedulestudent

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class PlanAdapter(
    private val plans: MutableList<Plan>,
    private val onStatusChanged: (Plan) -> Unit,
    private val onDeleteClicked: (Plan) -> Unit,
    private val onEditClicked: (Plan) -> Unit
) : RecyclerView.Adapter<PlanAdapter.PlanViewHolder>() {

    class PlanViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val cbCompleted: CheckBox = itemView.findViewById(R.id.cbCompleted)
        val tvTitle: TextView = itemView.findViewById(R.id.tvPlanTitle)
        val tvDate: TextView = itemView.findViewById(R.id.tvPlanDate)
        val btnEdit: ImageButton = itemView.findViewById(R.id.btnEdit)
        val btnDelete: ImageButton = itemView.findViewById(R.id.btnDelete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlanViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_plan, parent, false)
        return PlanViewHolder(view)
    }

    override fun onBindViewHolder(holder: PlanViewHolder, position: Int) {
        val plan = plans[position]

        holder.tvTitle.text = plan.title
        holder.tvDate.text = plan.date

        holder.cbCompleted.setOnCheckedChangeListener(null)
        holder.cbCompleted.isChecked = plan.isCompleted

        holder.cbCompleted.setOnCheckedChangeListener { _, isChecked ->
            if (holder.adapterPosition != RecyclerView.NO_POSITION) {
                plan.isCompleted = isChecked
                onStatusChanged(plan)
            }
        }

        holder.btnDelete.setOnClickListener {
            onDeleteClicked(plan)
        }

        holder.btnEdit.setOnClickListener {
            onEditClicked(plan)
        }
    }

    override fun getItemCount(): Int = plans.size
}
