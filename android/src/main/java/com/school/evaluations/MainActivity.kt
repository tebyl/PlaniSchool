package com.school.evaluations

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.viewpager2.widget.ViewPager2

class MainActivity : AppCompatActivity() {

    private lateinit var viewPager: ViewPager2
    private lateinit var tabCalendar: TextView
    private lateinit var tabPriority: TextView
    private lateinit var tabSubjects: TextView
    private lateinit var vpAdapter: ViewPagerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        window.statusBarColor = ContextCompat.getColor(this, R.color.grad_purple)

        viewPager = findViewById(R.id.viewPager)
        tabCalendar = findViewById(R.id.tabCalendar)
        tabPriority = findViewById(R.id.tabPriority)
        tabSubjects = findViewById(R.id.tabSubjects)

        vpAdapter = ViewPagerAdapter(this)
        viewPager.adapter = vpAdapter

        tabCalendar.setOnClickListener { viewPager.currentItem = 0 }
        tabPriority.setOnClickListener { viewPager.currentItem = 1 }
        tabSubjects.setOnClickListener { viewPager.currentItem = 2 }

        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                updateTabSelection(position)
            }
        })

        findViewById<Button>(R.id.btnAdd).setOnClickListener {
            showNewEvaluationSheet(null)
        }
    }

    private fun updateTabSelection(position: Int) {
        val tabs = listOf(tabCalendar, tabPriority, tabSubjects)
        val activeBackgrounds = listOf(
            R.drawable.bg_tab_calendar_active,
            R.drawable.bg_tab_priority_active,
            R.drawable.bg_tab_subjects_active
        )
        tabs.forEachIndexed { i, tab ->
            if (i == position) {
                tab.setBackgroundResource(activeBackgrounds[i])
                tab.setTextColor(ContextCompat.getColor(this, R.color.white))
            } else {
                tab.setBackgroundResource(R.drawable.bg_tab_inactive)
                tab.setTextColor(ContextCompat.getColor(this, R.color.text_secondary))
            }
        }
    }

    fun showNewEvaluationSheet(editEval: Evaluation?) {
        val sheet = NewEvaluationBottomSheet()
        sheet.editingEvaluation = editEval
        sheet.onSaved = { refreshAllFragments() }
        sheet.show(supportFragmentManager, "new_eval")
    }

    private fun refreshAllFragments() {
        (vpAdapter.getFragment(0) as? CalendarFragment)?.refreshData()
        (vpAdapter.getFragment(1) as? PriorityFragment)?.refreshData()
        (vpAdapter.getFragment(2) as? SubjectsFragment)?.refreshData()
    }
}
