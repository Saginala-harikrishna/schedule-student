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

    private lateinit var recyclerView: RecyclerView
    private lateinit var emptyState: TextView
    private lateinit var addButton: Button

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

        recyclerView = view.findViewById(R.id.rvSubtopicsRange)
        emptyState = view.findViewById(R.id.tvEmptyState)
        addButton = view.findViewById(R.id.btnAddSubtopicsRange)

        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        addButton.setOnClickListener {
            startActivity(
                Intent(
                    requireContext(),
                    AddSubtopicsRangeActivity::class.java
                )
            )
        }

        // Initial load
        loadSubtopicsRanges()

        return view
    }

    override fun onResume() {
        super.onResume()
        // 🔁 Refresh every time fragment comes back
        loadSubtopicsRanges()
    }

    private fun loadSubtopicsRanges() {
        viewLifecycleOwner.lifecycleScope.launch {
            val db = PlanDatabase.getDatabase(requireContext())
            val ranges = db.subtopicsRangeDao().getAll()

            if (ranges.isEmpty()) {
                emptyState.visibility = View.VISIBLE
                recyclerView.visibility = View.GONE
            } else {
                emptyState.visibility = View.GONE
                recyclerView.visibility = View.VISIBLE

                val uiModels = ranges.map { range ->
                    val total = db.subtopicDao().getTotalCount(range.id)
                    val completed = db.subtopicDao().getCompletedCount(range.id)

                    SubtopicsRangeUiModel(
                        id = range.id,
                        title = range.title,
                        startDate = range.startDate,
                        endDate = range.endDate,
                        completedCount = completed,
                        totalCount = total
                    )
                }

                recyclerView.adapter = SubtopicsRangeAdapter(uiModels) { selected ->
                    val intent = Intent(
                        requireContext(),
                        SubtopicsRangeDetailActivity::class.java
                    )
                    intent.putExtra("RANGE_ID", selected.id)
                    startActivity(intent)
                }
            }
        }
    }
}
