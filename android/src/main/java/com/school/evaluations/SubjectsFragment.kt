package com.school.evaluations

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class SubjectsFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var evaluations: MutableList<Evaluation>

    companion object {
        fun newInstance() = SubjectsFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_subjects, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        evaluations = DataManager.getEvaluations(requireContext())
        recyclerView = view.findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        setupAdapter()
    }

    fun refreshData() {
        if (!isAdded) return
        evaluations = DataManager.getEvaluations(requireContext())
        setupAdapter()
    }

    private fun setupAdapter() {
        val subjectsWithEvals = Subject.DEFAULT_SUBJECTS.filter { subject ->
            evaluations.any { it.subject == subject.name }
        }

        recyclerView.adapter = SubjectsAdapter(
            subjectsWithEvals,
            evaluations,
            onComplete = { eval ->
                if (isAdded) {
                    DataManager.updateEvaluation(requireContext(), eval.copy(completed = !eval.completed))
                    refreshData()
                }
            },
            onEdit = { eval ->
                (activity as? MainActivity)?.showNewEvaluationSheet(eval)
            },
            onDelete = { eval ->
                if (isAdded) {
                    DataManager.deleteEvaluation(requireContext(), eval.id)
                    refreshData()
                }
            }
        )
    }
}
