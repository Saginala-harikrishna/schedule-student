package com.example.schedulestudent

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters

class NotificationWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {

        // 🔹 Get DB
        val db = PlanDatabase.getDatabase(applicationContext)

        // 🔹 REAL DATA FROM ROOM
        val plans = db.planDao().getAllPlans()
        val rangeTargets = db.rangeTargetDao().getAllRangeTargets()

        // 🔹 Compute notifications (REAL LOGIC)
        val result = NotificationEngine.compute(plans, rangeTargets)

        // 🔹 Save for NotificationCenter screen
        NotificationCache.save(result)

        // 🔹 Show notification only if needed
        if (
            result.pendingCurrentTargets.isNotEmpty() ||
            result.endingTomorrowRangeTargets.isNotEmpty() ||
            result.overdueRangeTargets.isNotEmpty()
        ) {
            showSystemNotification()
        }

        return Result.success()
    }

    private fun showSystemNotification() {

        val channelId = "daily_notifications"
        val manager =
            applicationContext.getSystemService(Context.NOTIFICATION_SERVICE)
                    as NotificationManager

        // 🔹 Channel (Android 8+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Daily Task Reminders",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            manager.createNotificationChannel(channel)
        }

        // 🔹 Open NotificationCenter
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
            .setContentText("You have tasks that need attention today")
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        manager.notify(1001, notification)
    }
}
