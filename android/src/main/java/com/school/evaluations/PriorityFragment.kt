package com.school.evaluations

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView

class PriorityFragment : Fragment() {
    
    private lateinit var recyclerView: RecyclerView
    private lateinit var evaluations: MutableList<Evaluation>
    
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
        
        evaluations = DataManager.getEvaluations(requireContext())
        recyclerView = view.findViewById(R.id.recyclerView)
        
        val sorted = evaluations.sortedBy { it.getDaysUntil() }
        recyclerView.adapter = PriorityAdapter(sorted) { evaluation ->
            // Handle click
        }
    }
}