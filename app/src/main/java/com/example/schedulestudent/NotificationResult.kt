package com.example.schedulestudent

data class NotificationResult(
    val pendingCurrentTargets: List<String>,
    val endingTomorrowRangeTargets: List<String>,
    val overdueRangeTargets: List<String>
)
