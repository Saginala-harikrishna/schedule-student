package com.example.schedulestudent

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HomeActivity : AppCompatActivity() {

    private lateinit var planDao: PlanDao

    private val uncompletedPlans = mutableListOf<Plan>()
    private val completedPlans = mutableListOf<Plan>()

    private lateinit var uncompletedAdapter: PlanAdapter
    private lateinit var completedAdapter: PlanAdapter

    private lateinit var tvUncompleted: View
    private lateinit var tvCompleted: View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        planDao = PlanDatabase.getDatabase(this).planDao()

        val rvUncompleted = findViewById<RecyclerView>(R.id.rvUncompleted)
        val rvCompleted = findViewById<RecyclerView>(R.id.rvCompleted)
        val fabAddPlan = findViewById<FloatingActionButton>(R.id.fabAddPlan)

        tvUncompleted = findViewById(R.id.tvUncompleted)
        tvCompleted = findViewById(R.id.tvCompleted)

        uncompletedAdapter = PlanAdapter(
            uncompletedPlans,
            { plan -> updateCompletion(plan) },
            { plan -> deletePlan(plan) },
            { plan -> editPlan(plan) }
        )

        completedAdapter = PlanAdapter(
            completedPlans,
            { plan -> updateCompletion(plan) },
            { plan -> deletePlan(plan) },
            { plan -> editPlan(plan) }
        )

        rvUncompleted.layoutManager = LinearLayoutManager(this)
        rvCompleted.layoutManager = LinearLayoutManager(this)

        rvUncompleted.adapter = uncompletedAdapter
        rvCompleted.adapter = completedAdapter

        fabAddPlan.setOnClickListener {
            startActivity(Intent(this, AddPlanActivity::class.java))
        }
    }

    override fun onResume() {
        super.onResume()
        refreshPlans()
    }

    private fun refreshPlans() {
        lifecycleScope.launch {
            val allPlans = withContext(Dispatchers.IO) {
                planDao.getAllPlans()
            }

            uncompletedPlans.clear()
            completedPlans.clear()

            for (plan in allPlans) {
                if (plan.isCompleted) {
                    completedPlans.add(plan)
                } else {
                    uncompletedPlans.add(plan)
                }
            }

            tvUncompleted.visibility =
                if (uncompletedPlans.isEmpty()) View.GONE else View.VISIBLE

            tvCompleted.visibility =
                if (completedPlans.isEmpty()) View.GONE else View.VISIBLE

            uncompletedAdapter.notifyDataSetChanged()
            completedAdapter.notifyDataSetChanged()
        }
    }

    private fun updateCompletion(plan: Plan) {
        lifecycleScope.launch(Dispatchers.IO) {
            planDao.updatePlan(plan)
            refreshPlans()
        }
    }

    private fun deletePlan(plan: Plan) {
        lifecycleScope.launch(Dispatchers.IO) {
            planDao.deletePlan(plan)
            refreshPlans()
        }
    }

    private fun editPlan(plan: Plan) {
        val intent = Intent(this, AddPlanActivity::class.java)
        intent.putExtra("PLAN_ID", plan.id)
        startActivity(intent)
    }
}
