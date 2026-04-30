package com.school.evaluations

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class PriorityFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView

    companion object {
        fun newInstance() = PriorityFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_priority, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recyclerView = view.findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        loadConfig()
    }

    fun refreshData() {
        if (!isAdded) return
        loadConfig()
    }

    private fun loadConfig() {
        val studyConfig = DataManager.getStudyConfigWithDefaults(requireContext()).toMutableMap()
        recyclerView.adapter = PriorityAdapter(
            Subject.DEFAULT_SUBJECTS,
            studyConfig
        ) { subjectName, days ->
            studyConfig[subjectName] = days
            DataManager.saveStudyConfig(requireContext(), studyConfig)
            (activity as? MainActivity)?.refreshCalendar()
        }
    }
}
