package com.school.evaluations

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.GridView
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.google.android.material.bottomsheet.BottomSheetDialog
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
            val subjectName = Subject.normalizeName(eval.subject)
            val studyDays = studyConfig[subjectName] ?: DataManager.getDefaultStudyDaysForSubject(subjectName)
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

        val dialog = BottomSheetDialog(requireContext())
        val content = layoutInflater.inflate(R.layout.dialog_day_detail, null, false)
        dialog.setContentView(content)

        content.findViewById<TextView>(R.id.tvDayTitle).text = "📅 ${formatLongDate(dayUi.dateKey)}"

        val containerEvaluations = content.findViewById<LinearLayout>(R.id.containerEvaluations)
        val containerStudy = content.findViewById<LinearLayout>(R.id.containerStudy)
        val tvEmptyEvaluations = content.findViewById<TextView>(R.id.tvEmptyEvaluations)
        val tvEmptyStudy = content.findViewById<TextView>(R.id.tvEmptyStudy)

        if (dayUi.evaluations.isEmpty()) {
            tvEmptyEvaluations.visibility = View.VISIBLE
        } else {
            tvEmptyEvaluations.visibility = View.GONE
            dayUi.evaluations.forEach { eval ->
                containerEvaluations.addView(
                    createDetailEntry(
                        emoji = eval.emoji,
                        title = eval.subject,
                        subtitle = eval.topic.ifBlank { "Sin tema" },
                        chip = "Evaluación"
                    )
                )
            }
        }

        if (dayUi.studyItems.isEmpty()) {
            tvEmptyStudy.visibility = View.VISIBLE
        } else {
            tvEmptyStudy.visibility = View.GONE
            dayUi.studyItems.forEach { item ->
                containerStudy.addView(
                    createDetailEntry(
                        emoji = Subject.findByName(item.subjectName)?.emoji ?: "📘",
                        title = item.subjectName,
                        subtitle = if (item.topic.isBlank()) "Revisión general" else item.topic,
                        chip = "Estudiar hoy"
                    )
                )
            }
        }

        content.findViewById<Button>(R.id.btnCloseDayDetail).setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun createDetailEntry(
        emoji: String,
        title: String,
        subtitle: String,
        chip: String
    ): View {
        val entryView = layoutInflater.inflate(R.layout.item_day_detail_entry, null, false)
        entryView.findViewById<TextView>(R.id.tvEntryEmoji).text = emoji
        entryView.findViewById<TextView>(R.id.tvEntryTitle).text = title
        entryView.findViewById<TextView>(R.id.tvEntrySubtitle).text = subtitle
        entryView.findViewById<TextView>(R.id.tvEntryChip).text = chip
        return entryView
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
