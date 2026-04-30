package com.school.evaluations

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class SubjectsFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var tvStatProximas: TextView
    private lateinit var tvStatCompletadas: TextView
    private lateinit var tvStatUrgentes: TextView
    private lateinit var tvStatTemas: TextView

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

        recyclerView = view.findViewById(R.id.recyclerView)
        tvStatProximas = view.findViewById(R.id.tvStatProximas)
        tvStatCompletadas = view.findViewById(R.id.tvStatCompletadas)
        tvStatUrgentes = view.findViewById(R.id.tvStatUrgentes)
        tvStatTemas = view.findViewById(R.id.tvStatTemas)

        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        loadData()
    }

    fun refreshData() {
        if (!isAdded) return
        loadData()
    }

    private fun loadData() {
        val evaluations = DataManager.getEvaluations(requireContext())

        val proximas = evaluations.count { !it.isDone() }
        val completadas = evaluations.count { it.isDone() }
        val urgentes = evaluations.count { !it.isDone() && it.getDaysUntil() in 0..2 }
        val temas = evaluations.size

        tvStatProximas.text = proximas.toString()
        tvStatCompletadas.text = completadas.toString()
        tvStatUrgentes.text = urgentes.toString()
        tvStatTemas.text = temas.toString()

        val sorted = evaluations.sortedWith(compareBy(
            { it.isDone() },
            { it.getDaysUntil() }
        ))

        recyclerView.adapter = SubjectsAdapter(
            sorted,
            onComplete = { eval ->
                if (isAdded) {
                    DataManager.updateEvaluation(requireContext(), eval.copy(completed = !eval.completed))
                    loadData()
                }
            },
            onEdit = { eval ->
                (activity as? MainActivity)?.showNewEvaluationSheet(eval)
            },
            onDelete = { eval ->
                if (isAdded) {
                    DataManager.deleteEvaluation(requireContext(), eval.id)
                    loadData()
                }
            }
        )
    }
}
