package com.example.schedulestudent

import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

object NotificationEngine {

    fun compute(
        plans: List<Plan>,
        rangeTargets: List<RangeTarget>
    ): NotificationResult {

        val today = LocalDate.now()

        // 🔹 Pending Current Targets
        val pendingCurrentTargets =
            plans
                .filter { !it.isCompleted }
                .map { it.title }

        // 🔹 Range Targets ending tomorrow
        val endingTomorrowRangeTargets =
            rangeTargets
                .filter {
                    val endDate = it.endDate.toLocalDate()
                    endDate == today.plusDays(1)
                }
                .map { it.title }

        // 🔹 Overdue Range Targets
        val overdueRangeTargets =
            rangeTargets
                .filter {
                    val endDate = it.endDate.toLocalDate()
                    endDate.isBefore(today)
                }
                .map { it.title }

        return NotificationResult(
            pendingCurrentTargets = pendingCurrentTargets,
            endingTomorrowRangeTargets = endingTomorrowRangeTargets,
            overdueRangeTargets = overdueRangeTargets
        )
    }

    // 🔹 EXTENSION FUNCTION (KEY FIX)
    private fun Long.toLocalDate(): LocalDate =
        Instant.ofEpochMilli(this)
            .atZone(ZoneId.systemDefault())
            .toLocalDate()
}
