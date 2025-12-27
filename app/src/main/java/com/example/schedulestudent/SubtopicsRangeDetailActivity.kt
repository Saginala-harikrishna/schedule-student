package com.example.schedulestudent

import android.os.Bundle
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.launch

class SubtopicsRangeDetailActivity : AppCompatActivity() {

    private var rangeId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_subtopics_range_detail)

        rangeId = intent.getIntExtra("RANGE_ID", -1)
        if (rangeId == -1) {
            finish()
            return
        }

        val tvProgress = findViewById<TextView>(R.id.tvProgress)
        val progressBar = findViewById<ProgressBar>(R.id.progressBar)
        val recyclerView = findViewById<RecyclerView>(R.id.rvSubtopics)

        recyclerView.layoutManager = LinearLayoutManager(this)

        lifecycleScope.launch {
            val db = PlanDatabase.getDatabase(this@SubtopicsRangeDetailActivity)
            val subtopics = db.subtopicDao().getByRangeId(rangeId)

            val adapter = SubtopicsCheckboxAdapter(
                subtopics.toMutableList()
            ) {
                // checkbox toggled → update DB & progress
                lifecycleScope.launch {
                    db.subtopicDao().updateCompletion(it.id, it.isCompleted)
                    updateProgress(db, tvProgress, progressBar)
                }
            }

            recyclerView.adapter = adapter
            updateProgress(db, tvProgress, progressBar)
        }
    }

    private suspend fun updateProgress(
        db: PlanDatabase,
        tv: TextView,
        bar: ProgressBar
    ) {
        val total = db.subtopicDao().getTotalCount(rangeId)
        val completed = db.subtopicDao().getCompletedCount(rangeId)

        val percent =
            if (total == 0) 0 else (completed * 100) / total

        tv.text = "Progress: $completed / $total ($percent%)"
        bar.progress = percent
    }
}
