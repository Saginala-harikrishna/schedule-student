package com.example.schedulestudent

data class SubtopicsRangeUiModel(
    val id: Int,
    val title: String,
    val startDate: String,
    val endDate: String,
    val completedCount: Int,
    val totalCount: Int
) {
    val progressPercent: Int
        get() = if (totalCount == 0) 0 else (completedCount * 100) / totalCount
}
