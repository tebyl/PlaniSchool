package com.school.evaluations

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.GridView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat
import java.util.*

class CalendarFragment : Fragment() {
    
    private lateinit var calendarGrid: GridView
    private lateinit var monthText: TextView
    private lateinit var evaluations: MutableList<Evaluation>
    private var currentMonth = Calendar.getInstance()
    
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
        
        updateCalendar()
    }
    
    private fun updateCalendar() {
        val monthFormat = SimpleDateFormat("MMMM yyyy", Locale("es"))
        monthText.text = monthFormat.format(currentMonth.time).replaceFirstChar { 
            if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() 
        }
        
        val days = generateCalendarDays()
        calendarGrid.adapter = CalendarAdapter(requireContext(), days, evaluations, currentMonth)
    }
    
    private fun generateCalendarDays(): List<CalendarDay> {
        val days = mutableListOf<CalendarDay>()
        val calendar = currentMonth.clone() as Calendar
        calendar.set(Calendar.DAY_OF_MONTH, 1)
        
        val firstDayOfWeek = calendar.get(Calendar.DAY_OF_WEEK) - 1
        val maxDay = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
        
        // Empty days before month starts
        for (i in 0 until firstDayOfWeek) {
            days.add(CalendarDay(0, false))
        }
        
        // Days of the month
        for (day in 1..maxDay) {
            days.add(CalendarDay(day, true))
        }
        
        return days
    }
}

data class CalendarDay(val day: Int, val isCurrentMonth: Boolean)