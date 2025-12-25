package com.example.schedulestudent

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.launch

class CurrentTargetFragment : Fragment() {

    private lateinit var rvPending: RecyclerView
    private lateinit var rvCompleted: RecyclerView

    private lateinit var pendingAdapter: PlanAdapter
    private lateinit var completedAdapter: PlanAdapter

    private lateinit var planDatabase: PlanDatabase

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val view = inflater.inflate(
            R.layout.fragment_current_target,
            container,
            false
        )

        // RecyclerViews
        rvPending = view.findViewById(R.id.rvPending)
        rvCompleted = view.findViewById(R.id.rvCompleted)

        val addButton = view.findViewById<Button>(R.id.btnAddPlan)

        rvPending.layoutManager = LinearLayoutManager(requireContext())
        rvCompleted.layoutManager = LinearLayoutManager(requireContext())

        planDatabase = PlanDatabase.getDatabase(requireContext())

        // Pending Adapter
        pendingAdapter = PlanAdapter(
            mutableListOf(),
            onStatusChanged = { plan ->
                lifecycleScope.launch {
                    plan.isCompleted = true
                    planDatabase.planDao().updatePlan(plan)
                    loadPlans()
                }
            },
            onDeleteClicked = { plan ->
                lifecycleScope.launch {
                    planDatabase.planDao().deletePlan(plan)
                    loadPlans()
                }
            },
            onEditClicked = { plan ->
                openEditScreen(plan)
            }
        )

        // Completed Adapter
        completedAdapter = PlanAdapter(
            mutableListOf(),
            onStatusChanged = { plan ->
                lifecycleScope.launch {
                    plan.isCompleted = false
                    planDatabase.planDao().updatePlan(plan)
                    loadPlans()
                }
            },
            onDeleteClicked = { plan ->
                lifecycleScope.launch {
                    planDatabase.planDao().deletePlan(plan)
                    loadPlans()
                }
            },
            onEditClicked = { plan ->
                openEditScreen(plan)
            }
        )

        rvPending.adapter = pendingAdapter
        rvCompleted.adapter = completedAdapter

        loadPlans()

        addButton.setOnClickListener {
            val intent = Intent(requireContext(), AddPlanActivity::class.java)
            startActivity(intent)
        }

        return view
    }

    override fun onResume() {
        super.onResume()
        loadPlans()
    }

    private fun loadPlans() {
        lifecycleScope.launch {
            val pending = planDatabase.planDao().getPendingPlans()
            val completed = planDatabase.planDao().getCompletedPlans()

            pendingAdapter.updateList(pending)
            completedAdapter.updateList(completed)
        }
    }

    private fun openEditScreen(plan: Plan) {
        val intent = Intent(requireContext(), AddPlanActivity::class.java)
        intent.putExtra("PLAN_ID", plan.id)
        startActivity(intent)
    }
}
