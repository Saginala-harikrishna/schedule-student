package com.example.schedulestudent

import android.app.DatePickerDialog
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.util.Calendar
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch



class AddSubtopicsRangeActivity : AppCompatActivity() {

    private val subtopicsList = mutableListOf<String>()
    private lateinit var adapter: SubtopicTempAdapter

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


        // RecyclerView setup
        adapter = SubtopicTempAdapter(subtopicsList)
        rvSubtopics.layoutManager = LinearLayoutManager(this)
        rvSubtopics.adapter = adapter

        // Add subtopic logic
        btnAddSubtopic.setOnClickListener {
            val subtopicText = etSubtopic.text.toString().trim()
            if (subtopicText.isNotEmpty()) {
                subtopicsList.add(subtopicText)
                adapter.notifyItemInserted(subtopicsList.size - 1)
                etSubtopic.text.clear()
            }
        }

        // Date pickers
        etStartDate.setOnClickListener {
            showDatePicker { selectedDate ->
                etStartDate.setText(selectedDate)
            }
        }

        etEndDate.setOnClickListener {
            showDatePicker { selectedDate ->
                etEndDate.setText(selectedDate)
            }
        }

        btnSave.setOnClickListener {

            val title = etTitle.text.toString().trim()
            val startDate = etStartDate.text.toString().trim()
            val endDate = etEndDate.text.toString().trim()

            when {
                title.isEmpty() -> {
                    etTitle.error = "Title is required"
                }

                startDate.isEmpty() -> {
                    etStartDate.error = "Start date is required"
                }

                endDate.isEmpty() -> {
                    etEndDate.error = "End date is required"
                }

                subtopicsList.isEmpty() -> {
                    Toast.makeText(
                        this,
                        "Add at least one subtopic",
                        Toast.LENGTH_SHORT
                    ).show()
                }

                !isEndDateAfterStart(startDate, endDate) -> {
                    Toast.makeText(
                        this,
                        "End date must be after start date",
                        Toast.LENGTH_SHORT
                    ).show()
                }

                else -> lifecycleScope.launch {

                    val db = PlanDatabase.getDatabase(this@AddSubtopicsRangeActivity)

                    // 1️⃣ Insert main plan
                    val rangeId = db.subtopicsRangeDao().insert(
                        SubtopicsRangeEntity(
                            title = title,
                            startDate = startDate,
                            endDate = endDate
                        )
                    ).toInt()

                    // 2️⃣ Insert subtopics
                    val subtopicEntities = subtopicsList.map {
                        SubtopicEntity(
                            subtopicsRangeId = rangeId,
                            title = it
                        )
                    }

                    db.subtopicDao().insertAll(subtopicEntities)

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

    private fun showDatePicker(onDateSelected: (String) -> Unit) {
        val calendar = Calendar.getInstance()

        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            this,
            { _, selectedYear, selectedMonth, selectedDay ->
                val formattedDate =
                    "$selectedDay/${selectedMonth + 1}/$selectedYear"
                onDateSelected(formattedDate)
            },
            year,
            month,
            day
        )

        datePickerDialog.show()
    }

    private fun isEndDateAfterStart(startDate: String, endDate: String): Boolean {
        val startParts = startDate.split("/")
        val endParts = endDate.split("/")

        val startCalendar = Calendar.getInstance().apply {
            set(
                startParts[2].toInt(),
                startParts[1].toInt() - 1,
                startParts[0].toInt()
            )
        }

        val endCalendar = Calendar.getInstance().apply {
            set(
                endParts[2].toInt(),
                endParts[1].toInt() - 1,
                endParts[0].toInt()
            )
        }

        return !endCalendar.before(startCalendar)
    }

}
