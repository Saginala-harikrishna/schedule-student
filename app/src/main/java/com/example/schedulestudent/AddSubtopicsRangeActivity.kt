package com.example.schedulestudent

import android.app.DatePickerDialog
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.launch
import java.util.Calendar

class AddSubtopicsRangeActivity : AppCompatActivity() {

    private val subtopicsList = mutableListOf<EditableSubtopic>()
    private lateinit var adapter: SubtopicTempAdapter

    // Edit mode support
    private var editRangeId: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_subtopics_range)

        val etTitle = findViewById<EditText>(R.id.etTitle)
        val etStartDate = findViewById<EditText>(R.id.etStartDate)
        val etEndDate = findViewById<EditText>(R.id.etEndDate)
        val etSubtopic = findViewById<EditText>(R.id.etSubtopic)
        val btnAddSubtopic = findViewById<Button>(R.id.btnAddSubtopic)
        val rvSubtopics = findViewById<RecyclerView>(R.id.rvSubtopics)
        val btnSave = findViewById<Button>(R.id.btnSave)

        // Detect edit mode
        editRangeId = intent.getIntExtra("EDIT_RANGE_ID", -1)
            .takeIf { it != -1 }

        // RecyclerView setup
        adapter = SubtopicTempAdapter(subtopicsList)
        rvSubtopics.layoutManager = LinearLayoutManager(this)
        rvSubtopics.adapter = adapter

        // STEP 3 — Load existing data if editing
        if (editRangeId != null) {
            lifecycleScope.launch {
                val db = PlanDatabase.getDatabase(this@AddSubtopicsRangeActivity)

                val range = db.subtopicsRangeDao().getById(editRangeId!!)
                val subtopics = db.subtopicDao().getByRangeId(editRangeId!!)

                etTitle.setText(range.title)
                etStartDate.setText(range.startDate)
                etEndDate.setText(range.endDate)

                subtopicsList.clear()
                subtopicsList.addAll(
                    subtopics.map {
                        EditableSubtopic(
                            id = it.id,
                            title = it.title,
                            isCompleted = it.isCompleted
                        )
                    }
                )

                adapter.notifyDataSetChanged()
            }
        }

        // Add subtopic (UI only)
        btnAddSubtopic.setOnClickListener {
            val text = etSubtopic.text.toString().trim()
            if (text.isNotEmpty()) {
                subtopicsList.add(
                    EditableSubtopic(
                        id = null,
                        title = text,
                        isCompleted = false
                    )
                )
                adapter.notifyItemInserted(subtopicsList.size - 1)
                etSubtopic.text.clear()
            }
        }

        // Date pickers
        etStartDate.setOnClickListener {
            showDatePicker { etStartDate.setText(it) }
        }

        etEndDate.setOnClickListener {
            showDatePicker { etEndDate.setText(it) }
        }

        // Save logic (CREATE + EDIT with preservation)
        btnSave.setOnClickListener {

            val title = etTitle.text.toString().trim()
            val startDate = etStartDate.text.toString().trim()
            val endDate = etEndDate.text.toString().trim()

            when {
                title.isEmpty() -> etTitle.error = "Title is required"
                startDate.isEmpty() -> etStartDate.error = "Start date is required"
                endDate.isEmpty() -> etEndDate.error = "End date is required"
                subtopicsList.isEmpty() ->
                    Toast.makeText(this, "Add at least one subtopic", Toast.LENGTH_SHORT).show()
                !isEndDateAfterStart(startDate, endDate) ->
                    Toast.makeText(this, "End date must be after start date", Toast.LENGTH_SHORT).show()

                else -> lifecycleScope.launch {

                    val db = PlanDatabase.getDatabase(this@AddSubtopicsRangeActivity)

                    // ---------------- CREATE MODE ----------------
                    if (editRangeId == null) {

                        val rangeId = db.subtopicsRangeDao().insert(
                            SubtopicsRangeEntity(
                                title = title,
                                startDate = startDate,
                                endDate = endDate
                            )
                        ).toInt()

                        val entities = subtopicsList.map {
                            SubtopicEntity(
                                subtopicsRangeId = rangeId,
                                title = it.title,
                                isCompleted = false
                            )
                        }

                        db.subtopicDao().insertAll(entities)

                    }
                    // ---------------- EDIT MODE (STEP 4) ----------------
                    else {

                        // Update parent
                        db.subtopicsRangeDao().update(
                            SubtopicsRangeEntity(
                                id = editRangeId!!,
                                title = title,
                                startDate = startDate,
                                endDate = endDate
                            )
                        )

                        val existingDbSubtopics =
                            db.subtopicDao().getByRangeId(editRangeId!!)

                        val existingUi = subtopicsList.filter { it.id != null }
                        val newUi = subtopicsList.filter { it.id == null }

                        // Update existing subtopics
                        existingUi.forEach {
                            db.subtopicDao().update(
                                SubtopicEntity(
                                    id = it.id!!,
                                    subtopicsRangeId = editRangeId!!,
                                    title = it.title,
                                    isCompleted = it.isCompleted
                                )
                            )
                        }

                        // Insert new subtopics
                        val newEntities = newUi.map {
                            SubtopicEntity(
                                subtopicsRangeId = editRangeId!!,
                                title = it.title,
                                isCompleted = false
                            )
                        }
                        db.subtopicDao().insertAll(newEntities)

                        // Delete removed subtopics
                        val uiIds = existingUi.map { it.id!! }.toSet()
                        existingDbSubtopics.forEach {
                            if (it.id !in uiIds) {
                                db.subtopicDao().deleteById(it.id)
                            }
                        }
                    }

                    Toast.makeText(
                        this@AddSubtopicsRangeActivity,
                        "Subtopics Range saved successfully",
                        Toast.LENGTH_SHORT
                    ).show()

                    finish()
                }
            }
        }
    }

    // ---------------- Helpers (UNCHANGED) ----------------

    private fun showDatePicker(onDateSelected: (String) -> Unit) {
        val calendar = Calendar.getInstance()
        DatePickerDialog(
            this,
            { _, y, m, d -> onDateSelected("$d/${m + 1}/$y") },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    private fun isEndDateAfterStart(startDate: String, endDate: String): Boolean {
        val startParts = startDate.split("/")
        val endParts = endDate.split("/")

        val startCalendar = Calendar.getInstance().apply {
            set(startParts[2].toInt(), startParts[1].toInt() - 1, startParts[0].toInt())
        }

        val endCalendar = Calendar.getInstance().apply {
            set(endParts[2].toInt(), endParts[1].toInt() - 1, endParts[0].toInt())
        }

        return !endCalendar.before(startCalendar)
    }
}
