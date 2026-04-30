package com.school.evaluations

import java.util.*

data class StudyItem(
    val evaluationId: String,
    val subjectName: String,
    val topic: String,
    val dateKey: String
)

data class CalendarDayUi(
    val dateKey: String,
    val evaluations: List<Evaluation>,
    val studyItems: List<StudyItem>
)

object StudyPlanGenerator {
    fun generateStudyDates(evaluationDate: Calendar, studyDays: Int): List<String> {
        val safeDays = studyDays.coerceAtLeast(0)
        val result = mutableListOf<String>()
        val cal = (evaluationDate.clone() as Calendar).apply { clearTime() }

        for (i in safeDays downTo 1) {
            val date = (cal.clone() as Calendar).apply { add(Calendar.DAY_OF_YEAR, -i) }
            result.add(date.toDateKey())
        }

        return result
    }

    fun parseDateKeyOrNull(dateKey: String): Calendar? {
        val parts = dateKey.split("-")
        if (parts.size != 3) return null
        val year = parts[0].toIntOrNull() ?: return null
        val month = parts[1].toIntOrNull() ?: return null
        val day = parts[2].toIntOrNull() ?: return null

        return try {
            Calendar.getInstance().apply {
                isLenient = false
                set(Calendar.YEAR, year)
                set(Calendar.MONTH, month - 1)
                set(Calendar.DAY_OF_MONTH, day)
                clearTime()
                timeInMillis
            }
        } catch (_: Exception) {
            null
        }
    }

    fun Calendar.toDateKey(): String {
        return String.format(
            Locale.US,
            "%04d-%02d-%02d",
            get(Calendar.YEAR),
            get(Calendar.MONTH) + 1,
            get(Calendar.DAY_OF_MONTH)
        )
    }

    private fun Calendar.clearTime() {
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }
}
