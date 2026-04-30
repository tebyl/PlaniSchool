package com.school.evaluations

import java.util.*

data class Evaluation(
    val id: String = UUID.randomUUID().toString(),
    val subject: String,
    val topic: String,
    val date: String, // YYYY-MM-DD format
    val studyDaysBefore: Int,
    val color: String,
    val emoji: String
) {
    fun getDaysUntil(): Int {
        val today = Calendar.getInstance()
        today.set(Calendar.HOUR_OF_DAY, 0)
        today.set(Calendar.MINUTE, 0)
        today.set(Calendar.SECOND, 0)
        today.set(Calendar.MILLISECOND, 0)
        
        val evalDate = Calendar.getInstance()
        val parts = date.split("-")
        evalDate.set(parts[0].toInt(), parts[1].toInt() - 1, parts[2].toInt())
        evalDate.set(Calendar.HOUR_OF_DAY, 0)
        evalDate.set(Calendar.MINUTE, 0)
        evalDate.set(Calendar.SECOND, 0)
        evalDate.set(Calendar.MILLISECOND, 0)
        
        val diff = evalDate.timeInMillis - today.timeInMillis
        return (diff / (1000 * 60 * 60 * 24)).toInt().let { if (it >= 0) it else -1 }
    }
    
    fun getStudyStartDate(): Calendar {
        val evalDate = Calendar.getInstance()
        val parts = date.split("-")
        evalDate.set(parts[0].toInt(), parts[1].toInt() - 1, parts[2].toInt())
        evalDate.add(Calendar.DAY_OF_YEAR, -studyDaysBefore)
        return evalDate
    }
}