package com.example.schedulestudent

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.work.Worker
import androidx.work.WorkerParameters

class NotificationWorker(
    context: Context,
    workerParams: WorkerParameters
) : Worker(context, workerParams) {

    override fun doWork(): Result {

        Log.d("NOTIFY_WORKER", "✅ NotificationWorker executed")

        // 🔹 STEP 1: Compute notifications (TEMP TEST DATA)
        val result = NotificationResult(
            pendingCurrentTargets = listOf("Math Homework", "DSA Practice"),
            endingTomorrowRangeTargets = listOf("Semester Study"),
            overdueRangeTargets = listOf("Fitness Goal")
        )

        // 🔹 STEP 2: Save to cache
        NotificationCache.save(result)
        Log.d("NOTIFY_WORKER", "📦 Cache updated: $result")

        // 🔹 STEP 3: Show system notification
        showNotification(result)

        return Result.success()
    }

    private fun showNotification(result: NotificationResult) {

        val channelId = "daily_notifications"

        val notificationManager =
            applicationContext.getSystemService(Context.NOTIFICATION_SERVICE)
                    as NotificationManager

        // 🔔 Create channel (Android 8+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Daily Notifications",
                NotificationManager.IMPORTANCE_HIGH // ✅ IMPORTANT
            ).apply {
                description = "Daily task reminders"
            }
            notificationManager.createNotificationChannel(channel)
        }

        // 🔹 Build meaningful notification text
        val totalCount =
            result.pendingCurrentTargets.size +
                    result.endingTomorrowRangeTargets.size +
                    result.overdueRangeTargets.size

        val contentText =
            if (totalCount > 0)
                "You have $totalCount tasks that need attention"
            else
                "You are all caught up 🎉"

        // 🔹 Intent to open NotificationCenter
        val intent = Intent(applicationContext, MainActivity::class.java).apply {
            putExtra("open_notification_center", true)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        val pendingIntent = PendingIntent.getActivity(
            applicationContext,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(applicationContext, channelId)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("Today's Tasks Reminder")
            .setContentText(contentText)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH) // ✅ For pre-O devices
            .build()

        notificationManager.notify(1001, notification)

        Log.d("NOTIFY_WORKER", "🔔 System notification shown")
    }
}
