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
                    etStartDate.setText(dateFormat.format(it.startDate))
                    etEndDate.setText(dateFormat.format(it.endDate))
                    etProgress.setText(it.progress.toString())
                }
            }
        }

        // 🔹 SAVE / UPDATE
        btnSave.setOnClickListener {
            lifecycleScope.launch {

                val title = etTitle.text.toString().trim()
                val progress = etProgress.text.toString().toIntOrNull() ?: 0

                val startDateMillis =
                    dateFormat.parse(etStartDate.text.toString())?.time
                        ?: return@launch

                val endDateMillis =
                    dateFormat.parse(etEndDate.text.toString())?.time
                        ?: return@launch

                if (editTargetId == -1) {
                    db.rangeTargetDao().insertRangeTarget(
                        RangeTarget(
                            title = title,
                            startDate = startDateMillis,
                            endDate = endDateMillis,
                            progress = progress
                        )
                    )
                } else {
                    db.rangeTargetDao().updateRangeTarget(
                        RangeTarget(
                            id = editTargetId,
                            title = title,
                            startDate = startDateMillis,
                            endDate = endDateMillis,
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
