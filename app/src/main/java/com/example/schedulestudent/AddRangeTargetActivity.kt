package com.example.schedulestudent

import android.app.DatePickerDialog
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class AddRangeTargetActivity : AppCompatActivity() {

    private lateinit var db: PlanDatabase
    private var editTargetId: Int = -1

    private lateinit var etTitle: EditText
    private lateinit var etStartDate: EditText
    private lateinit var etEndDate: EditText
    private lateinit var etProgress: EditText

    // UI format (what user sees)
    private val dateFormat =
        SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_range_target)

        db = PlanDatabase.getDatabase(this)

        etTitle = findViewById(R.id.etTitle)
        etStartDate = findViewById(R.id.etStartDate)
        etEndDate = findViewById(R.id.etEndDate)
        etProgress = findViewById(R.id.etProgress)
        val btnSave = findViewById<Button>(R.id.btnSave)

        editTargetId = intent.getIntExtra("range_target_id", -1)

        // 🔹 DATE PICKERS
        etStartDate.setOnClickListener {
            showDatePicker { date ->
                etStartDate.setText(date)
            }
        }

        etEndDate.setOnClickListener {
            showDatePicker { date ->
                etEndDate.setText(date)
            }
        }

        // 🔹 EDIT MODE
        if (editTargetId != -1) {
            lifecycleScope.launch {
                val target =
                    db.rangeTargetDao().getRangeTargetById(editTargetId)

                target?.let {
                    etTitle.setText(it.title)
                    etStartDate.setText(it.startDate) // String now
                    etEndDate.setText(it.endDate)     // String now
                    etProgress.setText(it.progress.toString())
                }
            }
        }

        // 🔹 SAVE / UPDATE
        btnSave.setOnClickListener {
            lifecycleScope.launch {

                val title = etTitle.text.toString().trim()
                val startDate = etStartDate.text.toString().trim()
                val endDate = etEndDate.text.toString().trim()
                val progressText = etProgress.text.toString().trim()

                // -------------------------
                // ✅ VALIDATIONS
                // -------------------------

                if (title.isEmpty()) {
                    etTitle.error = "Title is required"
                    return@launch
                }

                if (startDate.isEmpty()) {
                    etStartDate.error = "Start date is required"
                    return@launch
                }

                if (endDate.isEmpty()) {
                    etEndDate.error = "End date is required"
                    return@launch
                }

                if (progressText.isEmpty()) {
                    etProgress.error = "Progress is required"
                    return@launch
                }

                val progress = progressText.toIntOrNull()
                if (progress == null || progress !in 0..100) {
                    etProgress.error = "Progress must be between 0 and 100"
                    return@launch
                }

                // Validate date logic
                val start = dateFormat.parse(startDate)
                val end = dateFormat.parse(endDate)

                if (start != null && end != null && end.before(start)) {
                    etEndDate.error = "End date cannot be before start date"
                    return@launch
                }

                // -------------------------
                // ✅ INSERT / UPDATE
                // -------------------------

                if (editTargetId == -1) {
                    db.rangeTargetDao().insertRangeTarget(
                        RangeTarget(
                            title = title,
                            startDate = startDate, // String
                            endDate = endDate,     // String
                            progress = progress
                        )
                    )
                } else {
                    db.rangeTargetDao().updateRangeTarget(
                        RangeTarget(
                            id = editTargetId,
                            title = title,
                            startDate = startDate,
                            endDate = endDate,
                            progress = progress
                        )
                    )
                }

                finish()
            }
        }
    }

    // 🔹 DATE PICKER FUNCTION
    private fun showDatePicker(onDateSelected: (String) -> Unit) {
        val calendar = Calendar.getInstance()

        DatePickerDialog(
            this,
            { _, year, month, day ->
                val selectedCal = Calendar.getInstance()
                selectedCal.set(year, month, day)

                onDateSelected(dateFormat.format(selectedCal.time))
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }
}
