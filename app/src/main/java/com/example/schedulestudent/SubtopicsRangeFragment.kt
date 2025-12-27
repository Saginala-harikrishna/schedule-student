package com.example.schedulestudent

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.launch

class SubtopicsRangeFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val view = inflater.inflate(
            R.layout.fragment_subtopics_range,
            container,
            false
        )

        val recyclerView = view.findViewById<RecyclerView>(R.id.rvSubtopicsRange)
        val emptyState = view.findViewById<TextView>(R.id.tvEmptyState)
        val addButton = view.findViewById<Button>(R.id.btnAddSubtopicsRange)

        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        // Load data from DB
        viewLifecycleOwner.lifecycleScope.launch {
            val db = PlanDatabase.getDatabase(requireContext())
            val list = db.subtopicsRangeDao().getAll()

            if (list.isEmpty()) {
                emptyState.visibility = View.VISIBLE
                recyclerView.visibility = View.GONE
            } else {
                emptyState.visibility = View.GONE
                recyclerView.visibility = View.VISIBLE

                recyclerView.adapter = SubtopicsRangeAdapter(list) { selectedItem ->
                    val intent = Intent(
                        requireContext(),
                        SubtopicsRangeDetailActivity::class.java
                    )
                    intent.putExtra("RANGE_ID", selectedItem.id)
                    startActivity(intent)
                }
            }
        }

        addButton.setOnClickListener {
            startActivity(
                Intent(
                    requireContext(),
                    AddSubtopicsRangeActivity::class.java
                )
            )
        }

        return view
    }
}
