package com.school.evaluations

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import androidx.core.content.ContextCompat
import java.util.*

class CalendarAdapter(
    private val context: Context,
    private val days: List<CalendarDay>,
    private val evaluations: List<Evaluation>,
    private val currentMonth: Calendar
) : BaseAdapter() {

    override fun getCount(): Int = days.size
    override fun getItem(position: Int): Any = days[position]
    override fun getItemId(position: Int): Long = position.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view = convertView ?: LayoutInflater.from(context)
            .inflate(R.layout.item_calendar_day, parent, false)

        val dayText = view.findViewById<TextView>(R.id.dayText)
        val dotView = view.findViewById<View>(R.id.dotView)
        val pillSubject = view.findViewById<TextView>(R.id.pillSubject)

        val day = days[position]

        dotView.visibility = View.GONE
        pillSubject.visibility = View.GONE
        dayText.setBackgroundResource(0)

        if (!day.isCurrentMonth || day.day == 0) {
            dayText.text = ""
            return view
        }

        dayText.text = day.day.toString()

        val today = Calendar.getInstance()
        val isToday = today.get(Calendar.MONTH) == currentMonth.get(Calendar.MONTH) &&
                today.get(Calendar.YEAR) == currentMonth.get(Calendar.YEAR) &&
                today.get(Calendar.DAY_OF_MONTH) == day.day

        if (isToday) {
            dayText.setBackgroundResource(R.drawable.bg_today)
            dayText.setTextColor(ContextCompat.getColor(context, R.color.white))
        } else {
            dayText.setTextColor(ContextCompat.getColor(context, R.color.text_primary))
        }

        val dayStr = String.format("%02d", day.day)
        val monthStr = String.format("%02d", currentMonth.get(Calendar.MONTH) + 1)
        val yearStr = currentMonth.get(Calendar.YEAR).toString()
        val dateStr = "$yearStr-$monthStr-$dayStr"

        val dayEvaluations = evaluations.filter { it.date == dateStr }
        val studyDayEvals = evaluations.filter { eval ->
            val studyStart = eval.getStudyStartDate()
            studyStart.get(Calendar.YEAR) == currentMonth.get(Calendar.YEAR) &&
            studyStart.get(Calendar.MONTH) == currentMonth.get(Calendar.MONTH) &&
            studyStart.get(Calendar.DAY_OF_MONTH) == day.day &&
            eval.date != dateStr
        }

        when {
            dayEvaluations.isNotEmpty() -> {
                pillSubject.text = dayEvaluations.first().subject
                pillSubject.visibility = View.VISIBLE
                dotView.setBackgroundResource(R.drawable.bg_dot)
                dotView.visibility = View.VISIBLE
            }
            studyDayEvals.isNotEmpty() -> {
                dotView.setBackgroundResource(R.drawable.bg_dot_study)
                dotView.visibility = View.VISIBLE
            }
        }

        return view
    }
}
