package com.example.schedulestudent

import android.app.DatePickerDialog
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Calendar

class AddPlanActivity : AppCompatActivity() {

    private lateinit var planDao: PlanDao

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_plan)

        planDao = PlanDatabase.getDatabase(this).planDao()

        val etTitle = findViewById<EditText>(R.id.etPlanTitle)
        val etDate = findViewById<EditText>(R.id.etPlanDate)
        val btnSave = findViewById<Button>(R.id.btnSavePlan)

        val calendar = Calendar.getInstance()

        val planId = intent.getIntExtra("PLAN_ID", -1)

        // Load existing plan for edit
        if (planId != -1) {
            lifecycleScope.launch(Dispatchers.IO) {
                val existingPlan = planDao.getPlanById(planId)
                existingPlan?.let {
                    runOnUiThread {
                        etTitle.setText(it.title)
                        etDate.setText(it.date)
                    }
                }
            }
        }

        etDate.setOnClickListener {
            DatePickerDialog(
                this,
                { _, y, m, d ->
                    etDate.setText("$d/${m + 1}/$y")
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            ).show()
        }

        btnSave.setOnClickListener {
            val title = etTitle.text.toString()
            val date = etDate.text.toString()

            if (title.isEmpty() || date.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            lifecycleScope.launch(Dispatchers.IO) {
                if (planId == -1) {
                    // New plan
                    planDao.insertPlan(
                        Plan(
                            title = title,
                            date = date,
                            isCompleted = false
                        )
                    )
                } else {
                    // Edit plan (preserve completion state)
                    val existingPlan = planDao.getPlanById(planId)
                    existingPlan?.let {
                        it.title = title
                        it.date = date
                        planDao.updatePlan(it)
                    }
                }
                finish()
            }
        }
    }
}
