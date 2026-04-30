package com.school.evaluations

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

class ViewPagerAdapter(fragmentActivity: FragmentActivity) : 
    FragmentStateAdapter(fragmentActivity) {
    
    override fun getItemCount(): Int = 3
    
    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> CalendarFragment.newInstance()
            1 -> PriorityFragment.newInstance()
            2 -> SubjectsFragment.newInstance()
            else -> CalendarFragment.newInstance()
        }
    }
}