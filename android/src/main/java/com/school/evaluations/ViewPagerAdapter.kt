package com.school.evaluations

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

class ViewPagerAdapter(fragmentActivity: FragmentActivity) :
    FragmentStateAdapter(fragmentActivity) {

    private val fragmentRefs = arrayOfNulls<Fragment>(3)

    override fun getItemCount(): Int = 3

    override fun createFragment(position: Int): Fragment {
        val fragment = when (position) {
            0 -> CalendarFragment.newInstance()
            1 -> PriorityFragment.newInstance()
            2 -> SubjectsFragment.newInstance()
            else -> CalendarFragment.newInstance()
        }
        fragmentRefs[position] = fragment
        return fragment
    }

    fun getFragment(position: Int): Fragment? = fragmentRefs[position]
}
