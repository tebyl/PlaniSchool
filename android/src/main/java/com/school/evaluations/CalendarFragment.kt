package com.school.evaluations

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.GridView
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import java.text.SimpleDateFormat
import java.util.*

class CalendarFragment : Fragment() {

    private lateinit var calendarGrid: GridView
    private lateinit var monthText: TextView
    private lateinit var evaluations: MutableList<Evaluation>
    private var currentMonth = Calendar.getInstance()
    private var currentDays: List<CalendarDay> = emptyList()
    private var currentDayUiMap: Map<String, CalendarDayUi> = emptyMap()

    companion object {
        fun newInstance() = CalendarFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_calendar, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        evaluations = DataManager.getEvaluations(requireContext())
        calendarGrid = view.findViewById(R.id.calendarGrid)
        monthText = view.findViewById(R.id.monthText)

        view.findViewById<ImageButton>(R.id.btnPrevMonth).setOnClickListener {
            currentMonth.add(Calendar.MONTH, -1)
            updateCalendar()
        }

        view.findViewById<ImageButton>(R.id.btnNextMonth).setOnClickListener {
            currentMonth.add(Calendar.MONTH, 1)
            updateCalendar()
        }

        calendarGrid.setOnItemClickListener { _, _, position, _ ->
            val day = currentDays.getOrNull(position) ?: return@setOnItemClickListener
            if (!day.isCurrentMonth || day.day == 0) return@setOnItemClickListener

            val dateKey = String.format(
                Locale.US,
                "%04d-%02d-%02d",
                currentMonth.get(Calendar.YEAR),
                currentMonth.get(Calendar.MONTH) + 1,
                day.day
            )
            val dayUi = currentDayUiMap[dateKey] ?: CalendarDayUi(dateKey, emptyList(), emptyList())
            showDayDetailsDialog(dayUi)
        }

        updateCalendar()
    }

    fun refreshData() {
        if (!isAdded) return
        evaluations = DataManager.getEvaluations(requireContext())
        updateCalendar()
    }

    private fun updateCalendar() {
        val monthFormat = SimpleDateFormat("MMMM yyyy", Locale("es"))
        monthText.text = monthFormat.format(currentMonth.time).replaceFirstChar {
            if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString()
        }

        currentDays = generateCalendarDays()
        val studyConfig = DataManager.getStudyConfigWithDefaults(requireContext())
        currentDayUiMap = buildDayUiMap(studyConfig)

        calendarGrid.adapter = CalendarAdapter(
            requireContext(),
            currentDays,
            currentMonth,
            currentDayUiMap
        )
    }

    private fun buildDayUiMap(studyConfig: Map<String, Int>): Map<String, CalendarDayUi> {
        val evalByDate = linkedMapOf<String, MutableList<Evaluation>>()
        val studyByDate = linkedMapOf<String, MutableList<StudyItem>>()

        for (eval in evaluations) {
            if (eval.isDone()) continue

            evalByDate.getOrPut(eval.date) { mutableListOf() }.add(eval)

            val evalDate = StudyPlanGenerator.parseDateKeyOrNull(eval.date) ?: continue
            val studyDays = studyConfig[eval.subject] ?: 3
            val studyDates = StudyPlanGenerator.generateStudyDates(evalDate, studyDays)

            for (dateKey in studyDates) {
                studyByDate.getOrPut(dateKey) { mutableListOf() }.add(
                    StudyItem(
                        evaluationId = eval.id,
                        subjectName = eval.subject,
                        topic = eval.topic,
                        dateKey = dateKey
                    )
                )
            }
        }

        val allKeys = linkedSetOf<String>()
        allKeys.addAll(evalByDate.keys)
        allKeys.addAll(studyByDate.keys)

        return allKeys.associateWith { key ->
            CalendarDayUi(
                dateKey = key,
                evaluations = evalByDate[key]?.toList().orEmpty(),
                studyItems = studyByDate[key]?.toList().orEmpty()
            )
        }
    }

    private fun showDayDetailsDialog(dayUi: CalendarDayUi) {
        if (dayUi.evaluations.isEmpty() && dayUi.studyItems.isEmpty()) return

        val dateTitle = formatLongDate(dayUi.dateKey)
        val body = buildString {
            append("Evaluaciones\n")
            if (dayUi.evaluations.isEmpty()) {
                append("- Sin evaluaciones\n")
            } else {
                for (eval in dayUi.evaluations) {
                    append("- ${eval.subject}: ${eval.topic.ifBlank { "Sin tema" }}\n")
                }
            }
            append("\nEstudio sugerido\n")
            if (dayUi.studyItems.isEmpty()) {
                append("- Sin estudio sugerido")
            } else {
                for (item in dayUi.studyItems) {
                    val topic = if (item.topic.isBlank()) "Sin tema" else item.topic
                    append("- ${item.subjectName}: $topic\n")
                }
            }
        }.trim()

        AlertDialog.Builder(requireContext())
            .setTitle(dateTitle)
            .setMessage(body)
            .setPositiveButton("Cerrar", null)
            .show()
    }

    private fun formatLongDate(dateKey: String): String {
        val cal = StudyPlanGenerator.parseDateKeyOrNull(dateKey) ?: return dateKey
        val fmt = SimpleDateFormat("EEEE, d 'de' MMMM 'de' yyyy", Locale("es", "CL"))
        return fmt.format(cal.time).replaceFirstChar {
            if (it.isLowerCase()) it.titlecase(Locale("es", "CL")) else it.toString()
        }
    }

    private fun generateCalendarDays(): List<CalendarDay> {
        val days = mutableListOf<CalendarDay>()
        val calendar = currentMonth.clone() as Calendar
        calendar.set(Calendar.DAY_OF_MONTH, 1)

        val firstDayOfWeek = calendar.get(Calendar.DAY_OF_WEEK) - 1
        val maxDay = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)

        for (i in 0 until firstDayOfWeek) {
            days.add(CalendarDay(0, false))
        }

        for (day in 1..maxDay) {
            days.add(CalendarDay(day, true))
        }

        return days
    }
}

data class CalendarDay(val day: Int, val isCurrentMonth: Boolean)
