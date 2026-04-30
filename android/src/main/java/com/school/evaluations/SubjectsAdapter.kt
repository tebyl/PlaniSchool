package com.school.evaluations

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.util.*

class SubjectsAdapter(
    private var subjects: List<Subject>,
    private var evaluations: List<Evaluation>,
    private val onComplete: (Evaluation) -> Unit,
    private val onEdit: (Evaluation) -> Unit,
    private val onDelete: (Evaluation) -> Unit
) : RecyclerView.Adapter<SubjectsAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val subjectEmoji: TextView = view.findViewById(R.id.subjectEmoji)
        val subjectName: TextView = view.findViewById(R.id.subjectName)
        val evalCount: TextView = view.findViewById(R.id.evalCount)
        val evalsContainer: LinearLayout = view.findViewById(R.id.evalsContainer)
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

            evalView.findViewById<TextView>(R.id.topicText).text =
                eval.topic.ifEmpty { "Sin tema" }
            evalView.findViewById<TextView>(R.id.dateText).text =
                formatDisplayDate(eval.date)

            evalView.findViewById<ImageButton>(R.id.btnCheck).setOnClickListener {
                onComplete(eval)
            }
            evalView.findViewById<ImageButton>(R.id.btnEdit).setOnClickListener {
                onEdit(eval)
            }
            evalView.findViewById<ImageButton>(R.id.btnDelete).setOnClickListener {
                onDelete(eval)
            }

            holder.evalsContainer.addView(evalView)
        }
    }

    override fun getItemCount() = subjects.size

    fun updateData(newSubjects: List<Subject>, newEvals: List<Evaluation>) {
        subjects = newSubjects
        evaluations = newEvals
        notifyDataSetChanged()
    }

    private fun formatDisplayDate(dateStr: String): String {
        val parts = dateStr.split("-")
        if (parts.size != 3) return dateStr
        return try {
            val cal = Calendar.getInstance()
            cal.set(parts[0].toInt(), parts[1].toInt() - 1, parts[2].toInt())
            val dayNames = arrayOf("domingo", "lunes", "martes", "miércoles", "jueves", "viernes", "sábado")
            val monthNames = arrayOf("enero", "febrero", "marzo", "abril", "mayo", "junio",
                "julio", "agosto", "septiembre", "octubre", "noviembre", "diciembre")
            val dayName = dayNames[cal.get(Calendar.DAY_OF_WEEK) - 1]
            val dayNum = cal.get(Calendar.DAY_OF_MONTH)
            val monthName = monthNames[cal.get(Calendar.MONTH)]
            val year = cal.get(Calendar.YEAR)
            "$dayName, $dayNum de $monthName de $year"
        } catch (e: Exception) {
            dateStr
        }
    }
}
