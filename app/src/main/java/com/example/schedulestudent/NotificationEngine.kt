package com.example.schedulestudent

import java.time.LocalDate
import java.time.format.DateTimeFormatter

object NotificationEngine {

    private val formatter =
        DateTimeFormatter.ofPattern("dd/MM/yyyy")

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
                    val endDate = parseDate(it.endDate)
                    endDate != null && endDate == today.plusDays(1)
                }
                .map { it.title }

        // 🔹 Overdue Range Targets
        val overdueRangeTargets =
            rangeTargets
                .filter {
                    val endDate = parseDate(it.endDate)
                    endDate != null && endDate.isBefore(today)
                }
                .map { it.title }

        return NotificationResult(
            pendingCurrentTargets = pendingCurrentTargets,
            endingTomorrowRangeTargets = endingTomorrowRangeTargets,
            overdueRangeTargets = overdueRangeTargets
        )
    }

    // 🔹 SAFE STRING → LocalDate PARSER
    private fun parseDate(date: String): LocalDate? {
        return try {
            LocalDate.parse(date, formatter)
        } catch (e: Exception) {
            null   // prevents crash if data is malformed
        }
    }
}
