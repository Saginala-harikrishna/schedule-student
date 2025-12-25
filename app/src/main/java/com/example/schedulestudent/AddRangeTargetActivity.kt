package com.example.schedulestudent

import android.app.DatePickerDialog
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class AddRangeTargetActivity : AppCompatActivity() {

    private lateinit var etTitle: EditText
    private lateinit var etStartDate: EditText
    private lateinit var etEndDate: EditText
    private lateinit var etProgress: EditText
    private lateinit var btnSave: Button

    private lateinit var database: PlanDatabase
    private var rangeId: Int = -1   // -1 = Create, else Edit

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_range_target)

        etTitle = findViewById(R.id.etTitle)
        etStartDate = findViewById(R.id.etStartDate)
        etEndDate = findViewById(R.id.etEndDate)
        etProgress = findViewById(R.id.etProgress)
        btnSave = findViewById(R.id.btnSave)

        database = PlanDatabase.getDatabase(this)

        // Disable keyboard for date fields
        etStartDate.isFocusable = false
        etEndDate.isFocusable = false

        etStartDate.setOnClickListener {
            showDatePicker { etStartDate.setText(it) }
        }

        etEndDate.setOnClickListener {
            showDatePicker { etEndDate.setText(it) }
        }

        // Check Edit Mode
        rangeId = intent.getIntExtra("RANGE_ID", -1)
        if (rangeId != -1) {
            loadRangeTargetForEdit()
        }

        btnSave.setOnClickListener {
            saveRangeTarget()
        }
    }

    // ---------------- DATE PICKER ----------------

    private fun showDatePicker(onDateSelected: (String) -> Unit) {
        val cal = Calendar.getInstance()
        DatePickerDialog(
            this,
            { _, year, month, day ->
                val date = "%02d/%02d/%04d".format(day, month + 1, year)
                onDateSelected(date)
            },
            cal.get(Calendar.YEAR),
            cal.get(Calendar.MONTH),
            cal.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    // ---------------- LOAD FOR EDIT ----------------

    private fun loadRangeTargetForEdit() {
        lifecycleScope.launch {
            val target = database.rangeTargetDao().getRangeTargetById(rangeId)
            target?.let {
                etTitle.setText(it.title)
                etStartDate.setText(it.startDate)
                etEndDate.setText(it.endDate)
                etProgress.setText(it.progress.toString())
            }
        }
    }

    // ---------------- SAVE / UPDATE ----------------

    private fun saveRangeTarget() {
        val title = etTitle.text.toString().trim()
        val startDate = etStartDate.text.toString()
        val endDate = etEndDate.text.toString()
        val progressText = etProgress.text.toString()

        // ---- VALIDATIONS ----

        if (title.isEmpty() || startDate.isEmpty() || endDate.isEmpty() || progressText.isEmpty()) {
            toast("All fields are required")
            return
        }

        val progress = progressText.toIntOrNull()
        if (progress == null || progress !in 0..100) {
            toast("Progress must be between 0 and 100")
            return
        }

        if (!isStartBeforeEnd(startDate, endDate)) {
            toast("Start date must be before end date")
            return
        }

        // ---- SAVE TO ROOM ----

        lifecycleScope.launch {
            if (rangeId == -1) {
                database.rangeTargetDao().insertRangeTarget(
                    RangeTarget(
                        title = title,
                        startDate = startDate,
                        endDate = endDate,
                        progress = progress
                    )
                )
            } else {
                database.rangeTargetDao().updateRangeTarget(
                    RangeTarget(
                        id = rangeId,
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

    // ---------------- HELPERS ----------------

    private fun isStartBeforeEnd(start: String, end: String): Boolean {
        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val startDate = sdf.parse(start)
        val endDate = sdf.parse(end)
        return startDate != null && endDate != null && startDate.before(endDate)
    }

    private fun toast(msg: String) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }
}
