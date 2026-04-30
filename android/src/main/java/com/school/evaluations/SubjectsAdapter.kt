package com.school.evaluations

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat
import java.util.*

class SubjectsAdapter(
    private var evaluations: List<Evaluation>,
    private val onComplete: (Evaluation) -> Unit,
    private val onEdit: (Evaluation) -> Unit,
    private val onDelete: (Evaluation) -> Unit
) : RecyclerView.Adapter<SubjectsAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val rootLayout: View = view.findViewById(R.id.rootLayout)
        val btnCheck: ImageButton = view.findViewById(R.id.btnCheck)
        val emojiText: TextView = view.findViewById(R.id.emojiText)
        val subjectText: TextView = view.findViewById(R.id.subjectText)
        val topicText: TextView = view.findViewById(R.id.topicText)
        val dateText: TextView = view.findViewById(R.id.dateText)
        val tvTodayBadge: TextView = view.findViewById(R.id.tvTodayBadge)
        val btnEdit: ImageButton = view.findViewById(R.id.btnEdit)
        val btnDelete: ImageButton = view.findViewById(R.id.btnDelete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_subject_dashboard, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val eval = evaluations[position]
        val isDone = eval.isDone()
        val isToday = eval.getDaysUntil() == 0

        holder.emojiText.text = eval.emoji
        holder.subjectText.text = eval.subject
        holder.topicText.text = eval.topic.ifEmpty { "Sin tema" }
        holder.dateText.text = formatDisplayDate(eval.date)

        holder.tvTodayBadge.visibility = if (isToday && !isDone) View.VISIBLE else View.GONE
        holder.rootLayout.alpha = if (isDone) 0.55f else 1.0f

        holder.btnCheck.imageTintList = ContextCompat.getColorStateList(
            holder.itemView.context,
            if (isDone) R.color.green_500 else R.color.gray_300
        )

        holder.btnCheck.setOnClickListener { onComplete(eval) }
        holder.btnEdit.setOnClickListener { onEdit(eval) }
        holder.btnDelete.setOnClickListener { onDelete(eval) }
    }

    override fun getItemCount() = evaluations.size

    fun updateData(newEvals: List<Evaluation>) {
        evaluations = newEvals
        notifyDataSetChanged()
    }

    private fun formatDisplayDate(dateStr: String): String {
        val parts = dateStr.split("-")
        if (parts.size != 3) return dateStr
        return try {
            val cal = Calendar.getInstance()
            cal.set(parts[0].toInt(), parts[1].toInt() - 1, parts[2].toInt())
            val formatter = SimpleDateFormat("EEEE, d 'de' MMMM 'de' yyyy", Locale("es", "CL"))
            val value = formatter.format(cal.time)
            value.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale("es", "CL")) else it.toString() }
        } catch (_: Exception) {
            dateStr
        }
    }
}
