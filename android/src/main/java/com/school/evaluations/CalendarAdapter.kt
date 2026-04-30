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
    private val currentMonth: Calendar,
    private val dayUiMap: Map<String, CalendarDayUi>
) : BaseAdapter() {

    override fun getCount(): Int = days.size
    override fun getItem(position: Int): Any = days[position]
    override fun getItemId(position: Int): Long = position.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view = convertView ?: LayoutInflater.from(context)
            .inflate(R.layout.item_calendar_day, parent, false)

        val dayText = view.findViewById<TextView>(R.id.dayText)
        val dotEval = view.findViewById<View>(R.id.dotEval)
        val dotStudy = view.findViewById<View>(R.id.dotStudy)
        val pillSubject = view.findViewById<TextView>(R.id.pillSubject)
        val moreText = view.findViewById<TextView>(R.id.moreText)

        val day = days[position]

        dotEval.visibility = View.GONE
        dotStudy.visibility = View.GONE
        pillSubject.visibility = View.GONE
        moreText.visibility = View.GONE
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

        val dayUi = dayUiMap[dateStr]
        val dayEvaluations = dayUi?.evaluations ?: emptyList()
        val dayStudyItems = dayUi?.studyItems ?: emptyList()

        if (dayEvaluations.isNotEmpty()) {
            dotEval.visibility = View.VISIBLE
            pillSubject.text = dayEvaluations.first().subject
            pillSubject.visibility = View.VISIBLE
            if (dayEvaluations.size > 1) {
                moreText.text = "+${dayEvaluations.size - 1} más"
                moreText.visibility = View.VISIBLE
            }
        }

        if (dayStudyItems.isNotEmpty()) {
            dotStudy.visibility = View.VISIBLE
        }

        return view
    }
}
