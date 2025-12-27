package com.example.schedulestudent

import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.launch

class SubtopicsRangeDetailActivity : AppCompatActivity() {

    private var rangeId: Int = -1
    private val tempSubtopics = mutableListOf<SubtopicEntity>()

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
        val btnUpdate = findViewById<Button>(R.id.btnUpdate)
        val btnBack = findViewById<ImageView>(R.id.ivBack)

        recyclerView.layoutManager = LinearLayoutManager(this)

        lifecycleScope.launch {
            val db = PlanDatabase.getDatabase(this@SubtopicsRangeDetailActivity)

            // Load once from DB
            tempSubtopics.clear()
            tempSubtopics.addAll(db.subtopicDao().getByRangeId(rangeId))

            recyclerView.adapter = SubtopicsCheckboxAdapter(
                tempSubtopics
            ) { updatedItem ->
                // 🔁 Only update memory
                val index = tempSubtopics.indexOfFirst { it.id == updatedItem.id }
                if (index != -1) {
                    tempSubtopics[index] = updatedItem
                }
                updateProgress(tvProgress, progressBar)
            }

            updateProgress(tvProgress, progressBar)
        }

        // ✅ Save ONLY on Update
        btnUpdate.setOnClickListener {
            lifecycleScope.launch {
                val db = PlanDatabase.getDatabase(this@SubtopicsRangeDetailActivity)

                tempSubtopics.forEach {
                    db.subtopicDao().updateCompletion(it.id, it.isCompleted)
                }

                finish()
            }
        }

        // Back without saving
        btnBack.setOnClickListener {
            finish()
        }
    }

    private fun updateProgress(
        tv: TextView,
        bar: ProgressBar
    ) {
        val total = tempSubtopics.size
        val completed = tempSubtopics.count { it.isCompleted }

        val percent =
            if (total == 0) 0 else (completed * 100) / total

        tv.text = "Progress: $completed / $total ($percent%)"
        bar.progress = percent
    }
}
