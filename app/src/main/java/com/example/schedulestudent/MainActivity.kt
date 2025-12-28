package com.example.schedulestudent

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.addCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.work.WorkManager
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.ExistingPeriodicWorkPolicy
import android.content.Intent



import com.google.android.material.bottomnavigation.BottomNavigationView
import java.time.Duration
import java.time.LocalDateTime
import java.util.concurrent.TimeUnit




class MainActivity : AppCompatActivity() {

    private lateinit var bottomNav: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        if (android.os.Build.VERSION.SDK_INT >= 33) {
            requestPermissions(
                arrayOf(android.Manifest.permission.POST_NOTIFICATIONS),
                1001
            )
        }


        bottomNav = findViewById(R.id.bottom_navigation)

        val source = intent.getStringExtra(NotificationNav.ARG_SOURCE)

        if (savedInstanceState == null) {
            when (source) {
                NotificationNav.FROM_CURRENT -> {
                    loadFragment(CurrentTargetFragment())
                    bottomNav.selectedItemId = R.id.nav_current
                }

                NotificationNav.FROM_RANGE -> {
                    loadFragment(RangeTargetFragment())
                    bottomNav.selectedItemId = R.id.nav_range
                }

                NotificationNav.FROM_SUBTOPICS_RANGE -> {
                    loadFragment(SubtopicsRangeFragment())
                    bottomNav.selectedItemId = R.id.nav_subtopics_range
                }

                else -> {
                    // App launched normally (not from Home)
                    loadFragment(CurrentTargetFragment())
                    bottomNav.selectedItemId = R.id.nav_current
                }
            }
        }


        // -------------------------
        // Toolbar
        // -------------------------
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        toolbar.setNavigationOnClickListener {
            navigateToHome()
        }




        // -------------------------
        // Bottom Navigation
        // -------------------------



        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_current -> {
                    loadFragment(CurrentTargetFragment())
                    true
                }
                R.id.nav_range -> {
                    loadFragment(RangeTargetFragment())
                    true
                }
                R.id.nav_subtopics_range -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.fragment_container, SubtopicsRangeFragment())
                        .commit()
                    true
                }

                else -> false
            }
        }

        // -------------------------
        // 🔔 Open NotificationCenter if launched from system notification
        // -------------------------
        if (intent.getBooleanExtra("open_notification_center", false)) {
            openNotificationCenter()
        }

        // -------------------------
        // 🔔 Schedule daily notification (6 AM)

        // -------------------------
        scheduleDailyNotification()

        // -------------------------
        // 🔙 Modern back handling
        // -------------------------
        onBackPressedDispatcher.addCallback(this) {
            if (supportFragmentManager.backStackEntryCount > 0) {
                supportFragmentManager.popBackStack()
                bottomNav.isEnabled = true
            } else {
                finishAffinity()
            }
        }
    }

    // -------------------------
    // Toolbar menu
    // -------------------------

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_notifications -> {
                openNotificationCenter()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    // -------------------------
    // Navigation helpers
    // -------------------------

    private fun openNotificationCenter() {
        bottomNav.isEnabled = false

        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, NotificationCenterFragment())
            .addToBackStack("notification_center")
            .commit()
    }

    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }

    fun selectBottomTab(tab: String) {
        bottomNav.selectedItemId =
            if (tab == NotificationNav.FROM_CURRENT)
                R.id.nav_current
            else
                R.id.nav_range
    }

    private fun navigateToHome() {
        val intent = Intent(this, HomeActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
        startActivity(intent)
    }


    // -------------------------
    // 🔔 WorkManager scheduling
    // -------------------------

    private fun scheduleDailyNotification() {

        val workRequest =
            PeriodicWorkRequestBuilder<NotificationWorker>(1, TimeUnit.DAYS)
                .setInitialDelay(calculateInitialDelay(), TimeUnit.MILLISECONDS)
                .build()

        WorkManager.getInstance(this)
            .enqueueUniquePeriodicWork(
                "daily_notification_work",
                ExistingPeriodicWorkPolicy.UPDATE,
                workRequest
            )
    }

    private fun calculateInitialDelay(): Long {
        val now = LocalDateTime.now()
        val nextRun = now.withHour(6).withMinute(0).withSecond(0)

        val scheduled =
            if (now.isAfter(nextRun)) nextRun.plusDays(1)
            else nextRun

        return Duration.between(now, scheduled).toMillis()
    }

}
