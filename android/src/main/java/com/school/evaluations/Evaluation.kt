package com.school.evaluations

import java.util.*

data class Evaluation(
    val id: String = UUID.randomUUID().toString(),
    val subject: String,
    val topic: String,
    val date: String, // YYYY-MM-DD format
    val studyDaysBefore: Int,
    val color: String,
    val emoji: String,
    val completed: Boolean = false
) {
    private fun parseDateOrNull(): Calendar? {
        val parts = date.split("-")
        if (parts.size != 3) return null

        val year = parts[0].toIntOrNull() ?: return null
        val month = parts[1].toIntOrNull() ?: return null
        val day = parts[2].toIntOrNull() ?: return null

        if (month !in 1..12 || day !in 1..31) return null

        return Calendar.getInstance().apply {
            isLenient = false
            set(Calendar.YEAR, year)
            set(Calendar.MONTH, month - 1)
            set(Calendar.DAY_OF_MONTH, day)
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
            try {
                timeInMillis
            } catch (_: IllegalArgumentException) {
                return null
            }
        }
    }

    fun getDaysUntil(): Int {
        val today = Calendar.getInstance()
        today.set(Calendar.HOUR_OF_DAY, 0)
        today.set(Calendar.MINUTE, 0)
        today.set(Calendar.SECOND, 0)
        today.set(Calendar.MILLISECOND, 0)

        val evalDate = parseDateOrNull() ?: return Int.MAX_VALUE
        val diff = evalDate.timeInMillis - today.timeInMillis
        return (diff / (1000 * 60 * 60 * 24)).toInt()
    }

    fun getStudyStartDate(): Calendar {
        val evalDate = parseDateOrNull() ?: Calendar.getInstance()
        evalDate.add(Calendar.DAY_OF_YEAR, -studyDaysBefore)
        return evalDate
    }

    fun isDone(): Boolean = completed || getDaysUntil() < 0
}
