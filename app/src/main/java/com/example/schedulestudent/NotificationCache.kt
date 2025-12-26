package com.example.schedulestudent

import java.time.LocalDate

object NotificationCache {

    private var cachedDate: LocalDate? = null
    private var cachedResult: NotificationResult? = null

    /**
     * Save today's notification result
     */
    fun save(result: NotificationResult) {
        cachedDate = LocalDate.now()
        cachedResult = result
    }

    /**
     * Get today's notification result if valid
     */
    fun get(): NotificationResult? {
        return if (cachedDate == LocalDate.now()) {
            cachedResult
        } else {
            clear()
            null
        }
    }

    /**
     * Clear cache (called automatically on date change)
     */
    fun clear() {
        cachedDate = null
        cachedResult = null
    }
}
