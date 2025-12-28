package com.example.schedulestudent

/**
 * UI-only model used during Create/Edit of Subtopics Range.
 * This is NOT a Room entity.
 */
data class EditableSubtopic(
    val id: Int? = null,        // null → new subtopic
    val title: String,
    val isCompleted: Boolean    // preserved during edit
)

