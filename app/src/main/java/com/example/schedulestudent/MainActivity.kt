package com.example.schedulestudent

import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private lateinit var btnGetStarted: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Link button with XML
        btnGetStarted = findViewById(R.id.btnGetStarted)

        // Button click action
        btnGetStarted.setOnClickListener {
            startActivity(
                android.content.Intent(
                    this@MainActivity,
                    HomeActivity::class.java
                )
            )
        }

    }
    }

