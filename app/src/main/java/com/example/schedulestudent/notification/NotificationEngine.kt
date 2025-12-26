package com.example.schedulestudent.notification

import com.example.schedulestudent.NotificationResult

object NotificationEngine {

    fun computeNotifications(
        pendingCurrent: List<String>,
        endingTomorrow: List<String>,
        overdue: List<String>
    ): NotificationResult {

        return NotificationResult(
            pendingCurrentTargets = pendingCurrent,
            endingTomorrowRangeTargets = endingTomorrow,
            overdueRangeTargets = overdue
        )
    }
}
