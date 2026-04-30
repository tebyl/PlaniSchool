package com.school.evaluations

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.util.*

class PriorityAdapter(
    private val evaluations: List<Evaluation>,
    private val onItemClick: (Evaluation) -> Unit
) : RecyclerView.Adapter<PriorityAdapter.ViewHolder>() {
    
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val subjectText: TextView = view.findViewById(R.id.subjectText)
        val topicText: TextView = view.findViewById(R.id.topicText)
        val dateText: TextView = view.findViewById(R.id.dateText)
        val daysText: TextView = view.findViewById(R.id.daysText)
        val emojiText: TextView = view.findViewById(R.id.emojiText)
        val studyInfoText: TextView = view.findViewById(R.id.studyInfoText)
    }
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_priority, parent, false)
        return ViewHolder(view)
    }
    
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val evaluation = evaluations[position]
        holder.subjectText.text = evaluation.subject
        holder.topicText.text = evaluation.topic.ifEmpty { "Sin tema" }
        holder.dateText.text = evaluation.date
        holder.emojiText.text = evaluation.emoji
        
        val days = evaluation.getDaysUntil()
        val studyStart = evaluation.getStudyStartDate()
        
        when {
            days < 0 -> {
                holder.daysText.text = "✓"
                holder.studyInfoText.text = "Evaluación completada"
            }
            days == 0 -> {
                holder.daysText.text = "¡Hoy!"
                holder.studyInfoText.text = "¡Es día de evaluación!"
            }
            studyStart.before(Calendar.getInstance()) -> {
                holder.daysText.text = "${days}d"
                holder.studyInfoText.text = "📚 ¡Es hora de estudiar!"
            }
            else -> {
                holder.daysText.text = "${days}d"
                holder.studyInfoText.text = "Empezar estudio: ${studyStart.get(Calendar.DAY_OF_MONTH)}/${studyStart.get(Calendar.MONTH) + 1}"
            }
        }
        
        holder.itemView.setOnClickListener { onItemClick(evaluation) }
    }
    
    override fun getItemCount() = evaluations.size
}