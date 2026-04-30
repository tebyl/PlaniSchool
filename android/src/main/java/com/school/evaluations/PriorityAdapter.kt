package com.school.evaluations

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import java.util.*

class PriorityAdapter(
    private var evaluations: MutableList<Evaluation>,
    private val onComplete: (Evaluation) -> Unit,
    private val onEdit: (Evaluation) -> Unit,
    private val onDelete: (Evaluation) -> Unit
) : RecyclerView.Adapter<PriorityAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val emojiText: TextView = view.findViewById(R.id.emojiText)
        val subjectText: TextView = view.findViewById(R.id.subjectText)
        val topicText: TextView = view.findViewById(R.id.topicText)
        val btnCheck: ImageButton = view.findViewById(R.id.btnCheck)
        val tvCompleted: TextView = view.findViewById(R.id.tvCompleted)
        val studyInfoText: TextView = view.findViewById(R.id.studyInfoText)
        val dateText: TextView = view.findViewById(R.id.dateText)
        val btnEdit: Button = view.findViewById(R.id.btnEdit)
        val btnDelete: ImageButton = view.findViewById(R.id.btnDelete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_priority, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val eval = evaluations[position]
        holder.emojiText.text = eval.emoji
        holder.subjectText.text = eval.subject
        holder.topicText.text = eval.topic.ifEmpty { "Sin tema" }
        holder.dateText.text = formatDisplayDate(eval.date)

        val isDone = eval.isDone()

        if (isDone) {
            holder.tvCompleted.visibility = View.VISIBLE
            holder.studyInfoText.visibility = View.GONE
            holder.btnCheck.setImageResource(R.drawable.ic_check)
            holder.btnCheck.imageTintList = ContextCompat.getColorStateList(
                holder.itemView.context, R.color.green_500
            )
        } else {
            holder.tvCompleted.visibility = View.GONE
            holder.studyInfoText.visibility = View.VISIBLE
            holder.btnCheck.imageTintList = ContextCompat.getColorStateList(
                holder.itemView.context, R.color.gray_300
            )

            val days = eval.getDaysUntil()
            val studyStart = eval.getStudyStartDate()
            holder.studyInfoText.text = when {
                days == 0 -> "🎯 ¡Hoy es el día de la evaluación!"
                studyStart.before(Calendar.getInstance()) -> "📚 ¡Es hora de estudiar! Quedan ${days}d"
                else -> "💡 Empieza a estudiar el ${studyStart.get(Calendar.DAY_OF_MONTH)}/${studyStart.get(Calendar.MONTH) + 1}"
            }
        }

        holder.btnCheck.setOnClickListener { onComplete(eval) }
        holder.btnEdit.setOnClickListener { onEdit(eval) }
        holder.btnDelete.setOnClickListener { onDelete(eval) }
    }

    override fun getItemCount() = evaluations.size

    fun updateData(newEvals: List<Evaluation>) {
        evaluations = newEvals.toMutableList()
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
