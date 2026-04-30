package com.school.evaluations

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class SubjectsAdapter(
    private val subjects: List<Subject>,
    private val evaluations: List<Evaluation>
) : RecyclerView.Adapter<SubjectsAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val subjectHeader: View = view.findViewById(R.id.subjectHeader)
        val subjectEmoji: TextView = view.findViewById(R.id.subjectEmoji)
        val subjectName: TextView = view.findViewById(R.id.subjectName)
        val evalCount: TextView = view.findViewById(R.id.evalCount)
        val evalsContainer: ViewGroup = view.findViewById(R.id.evalsContainer)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_subject, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val subject = subjects[position]
        val subjectEvals = evaluations.filter { it.subject == subject.name }
            .sortedBy { it.date }

        holder.subjectEmoji.text = subject.emoji
        holder.subjectName.text = subject.name
        holder.evalCount.text =
            "${subjectEvals.size} evaluación${if (subjectEvals.size != 1) "es" else ""}"

        holder.evalsContainer.removeAllViews()

        subjectEvals.forEach { eval ->
            val evalView = LayoutInflater.from(holder.itemView.context)
                .inflate(R.layout.item_subject_eval, holder.evalsContainer, false)

            val topicText = evalView.findViewById<TextView>(R.id.topicText)
            val dateText = evalView.findViewById<TextView>(R.id.dateText)
            val daysText = evalView.findViewById<TextView>(R.id.daysText)

            topicText.text = eval.topic.ifEmpty { "Sin tema" }
            dateText.text = eval.date

            val days = eval.getDaysUntil()
            daysText.text = when {
                days < 0 -> "✓"
                else -> "${days}d"
            }

            holder.evalsContainer.addView(evalView)
        }
    }

    override fun getItemCount() = subjects.size
}
