package com.school.evaluations

import android.content.res.ColorStateList
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class PriorityAdapter(
    private val subjects: List<Subject>,
    private val studyConfig: MutableMap<String, Int>,
    private val onConfigChanged: (String, Int) -> Unit
) : RecyclerView.Adapter<PriorityAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val subjectEmoji: TextView = view.findViewById(R.id.subjectEmoji)
        val subjectName: TextView = view.findViewById(R.id.subjectName)
        val tvDaysBadge: TextView = view.findViewById(R.id.tvDaysBadge)
        val seekBarDays: SeekBar = view.findViewById(R.id.seekBarDays)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_priority_slider, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val subject = subjects[position]
        val days = (studyConfig[subject.name] ?: 5).coerceIn(1, 10)

        holder.subjectEmoji.text = subject.emoji
        holder.subjectName.text = subject.name
        holder.tvDaysBadge.text = days.toString()
        val color = Color.parseColor(subject.color)
        holder.tvDaysBadge.backgroundTintList = ColorStateList.valueOf(color)
        holder.seekBarDays.thumbTintList = ColorStateList.valueOf(color)
        holder.seekBarDays.progressTintList = ColorStateList.valueOf(color)

        holder.seekBarDays.setOnSeekBarChangeListener(null)
        holder.seekBarDays.progress = days

        holder.seekBarDays.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                val d = maxOf(1, progress)
                holder.tvDaysBadge.text = d.toString()
                if (fromUser) {
                    studyConfig[subject.name] = d
                    onConfigChanged(subject.name, d)
                }
            }
            override fun onStartTrackingTouch(seekBar: SeekBar) {}
            override fun onStopTrackingTouch(seekBar: SeekBar) {}
        })
    }

    override fun getItemCount() = subjects.size
}
