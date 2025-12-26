package com.example.schedulestudent
import androidx.lifecycle.lifecycleScope

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

class RangeTargetFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: RangeTargetAdapter
    private lateinit var database: PlanDatabase

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val view = inflater.inflate(
            R.layout.fragment_range_target,
            container,
            false
        )

        recyclerView = view.findViewById(R.id.rvRangeTargets)
        val addButton = view.findViewById<Button>(R.id.btnAddRangeTarget)

        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        database = PlanDatabase.getDatabase(requireContext())

        adapter = RangeTargetAdapter(
            mutableListOf(),
            onUpdate = { target ->
                viewLifecycleOwner.lifecycleScope.launch {
                    database.rangeTargetDao().updateRangeTarget(target)
                }
            },
            onDelete = { target ->
                viewLifecycleOwner.lifecycleScope.launch {
                    database.rangeTargetDao().deleteRangeTarget(target)
                    loadRangeTargets()
                }
            },
            onEdit = { target ->
                val intent =
                    Intent(requireContext(), AddRangeTargetActivity::class.java)
                intent.putExtra("range_target_id", target.id) // ✅ FIXED
                startActivity(intent)
            }
        )

        recyclerView.adapter = adapter

        loadRangeTargets()

        addButton.setOnClickListener {
            startActivity(
                Intent(requireContext(), AddRangeTargetActivity::class.java)
            )
        }

        return view
    }

    override fun onResume() {
        super.onResume()
        loadRangeTargets()
    }

    private fun loadRangeTargets() {
        viewLifecycleOwner.lifecycleScope.launch {
            val list = database.rangeTargetDao().getAllRangeTargets()
            adapter.updateList(list)
        }
    }
}
