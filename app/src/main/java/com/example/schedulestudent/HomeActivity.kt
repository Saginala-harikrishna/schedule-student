package com.example.schedulestudent

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton

class HomeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        val btnGetStarted = findViewById<MaterialButton>(R.id.btnGetStarted)

        val cardCurrentTarget = findViewById<View>(R.id.cardCurrentTarget)
        val cardRangeTarget = findViewById<View>(R.id.cardRangeTarget)
        val cardRangeSubtopics = findViewById<View>(R.id.cardRangeSubtopics)

        btnGetStarted.setOnClickListener {
            openMain(NotificationNav.FROM_CURRENT)
        }

        cardCurrentTarget.setOnClickListener {
            openMain(NotificationNav.FROM_CURRENT)
        }

        cardRangeTarget.setOnClickListener {
            openMain(NotificationNav.FROM_RANGE)
        }

        cardRangeSubtopics.setOnClickListener {
            openMain(NotificationNav.FROM_SUBTOPICS_RANGE)
        }
    }

    private fun openMain(source: String) {
        val intent = Intent(this, MainActivity::class.java).apply {
            putExtra(NotificationNav.ARG_SOURCE, source)

            // 🔑 Ensures clean navigation behavior
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
        }
        startActivity(intent)
    }
}
