package com.school.evaluations

import android.app.DatePickerDialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.SeekBar
import android.widget.Spinner
import android.widget.TextView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import java.util.*

class NewEvaluationBottomSheet : BottomSheetDialogFragment() {

    var onSaved: (() -> Unit)? = null
    var editingEvaluation: Evaluation? = null

    private lateinit var spinnerSubject: Spinner
    private lateinit var editTopic: EditText
    private lateinit var tvDate: TextView
    private lateinit var seekBarDays: SeekBar
    private lateinit var tvDaysBadge: TextView
    private lateinit var tvStudyInfo: TextView

    private var selectedDate: Calendar = Calendar.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.dialog_new_evaluation, container, false)
    }

    override fun onStart() {
        super.onStart()
        val d = dialog as? BottomSheetDialog ?: return
        val bottomSheet = d.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
        bottomSheet?.background = ColorDrawable(Color.TRANSPARENT)
        d.behavior.apply {
            state = BottomSheetBehavior.STATE_EXPANDED
            skipCollapsed = true
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        spinnerSubject = view.findViewById(R.id.spinnerSubject)
        editTopic = view.findViewById(R.id.editTopic)
        tvDate = view.findViewById(R.id.tvDate)
        seekBarDays = view.findViewById(R.id.seekBarDays)
        tvDaysBadge = view.findViewById(R.id.tvDaysBadge)
        tvStudyInfo = view.findViewById(R.id.tvStudyInfo)

        setupSpinner()
        setupDatePicker()
        setupSeekBar()
        populateFields()

        view.findViewById<Button>(R.id.btnCancel).setOnClickListener { dismiss() }
        view.findViewById<Button>(R.id.btnAdd).setOnClickListener { saveEvaluation() }
    }

    private fun setupSpinner() {
        val subjectNames = Subject.DEFAULT_SUBJECTS.map { "${it.emoji} ${it.name}" }
        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            subjectNames
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerSubject.adapter = adapter
    }

    private fun setupDatePicker() {
        tvDate.setOnClickListener {
            DatePickerDialog(
                requireContext(),
                { _, year, month, day ->
                    selectedDate.set(year, month, day)
                    tvDate.text = formatDateDisplay(selectedDate)
                    updateStudyInfo()
                },
                selectedDate.get(Calendar.YEAR),
                selectedDate.get(Calendar.MONTH),
                selectedDate.get(Calendar.DAY_OF_MONTH)
            ).show()
        }
    }

    private fun setupSeekBar() {
        seekBarDays.max = 14
        seekBarDays.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                val days = maxOf(1, progress)
                tvDaysBadge.text = "${days}d"
                updateStudyInfo()
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {}
            override fun onStopTrackingTouch(seekBar: SeekBar) {}
        })
    }

    private fun populateFields() {
        val eval = editingEvaluation
        if (eval != null) {
            val subjectIndex = Subject.DEFAULT_SUBJECTS.indexOfFirst { it.name == eval.subject }
            if (subjectIndex >= 0) spinnerSubject.setSelection(subjectIndex)
            editTopic.setText(eval.topic)

            val parts = eval.date.split("-")
            if (parts.size == 3) {
                try {
                    selectedDate.set(parts[0].toInt(), parts[1].toInt() - 1, parts[2].toInt())
                } catch (_: Exception) { }
            }

            seekBarDays.progress = eval.studyDaysBefore
            tvDaysBadge.text = "${eval.studyDaysBefore}d"
        } else {
            seekBarDays.progress = 5
            tvDaysBadge.text = "5d"
        }

        tvDate.text = formatDateDisplay(selectedDate)
        updateStudyInfo()
    }

    private fun updateStudyInfo() {
        val days = maxOf(1, seekBarDays.progress)
        val studyStart = selectedDate.clone() as Calendar
        studyStart.add(Calendar.DAY_OF_YEAR, -days)

        val dayNames = arrayOf(
            "domingo", "lunes", "martes", "miércoles",
            "jueves", "viernes", "sábado"
        )
        val monthNames = arrayOf(
            "enero", "febrero", "marzo", "abril", "mayo", "junio",
            "julio", "agosto", "septiembre", "octubre", "noviembre", "diciembre"
        )

        val dayName = dayNames[studyStart.get(Calendar.DAY_OF_WEEK) - 1]
        val dayNum = studyStart.get(Calendar.DAY_OF_MONTH)
        val monthName = monthNames[studyStart.get(Calendar.MONTH)]
        val year = studyStart.get(Calendar.YEAR)

        tvStudyInfo.text = "💡 Empezarías a estudiar el $dayName, $dayNum de $monthName de $year"
    }

    private fun formatDateDisplay(cal: Calendar): String {
        return String.format(
            "%02d / %02d / %d",
            cal.get(Calendar.MONTH) + 1,
            cal.get(Calendar.DAY_OF_MONTH),
            cal.get(Calendar.YEAR)
        )
    }

    private fun saveEvaluation() {
        val topic = editTopic.text.toString().trim()
        if (topic.isEmpty()) {
            editTopic.error = "El tema no puede estar vacío"
            return
        }

        val subjectIndex = spinnerSubject.selectedItemPosition
        val subject = Subject.DEFAULT_SUBJECTS[subjectIndex]

        val dateStr = String.format(
            "%04d-%02d-%02d",
            selectedDate.get(Calendar.YEAR),
            selectedDate.get(Calendar.MONTH) + 1,
            selectedDate.get(Calendar.DAY_OF_MONTH)
        )

        val studyDays = maxOf(1, seekBarDays.progress)

        val evaluation = editingEvaluation?.copy(
            subject = subject.name,
            topic = topic,
            date = dateStr,
            studyDaysBefore = studyDays,
            emoji = subject.emoji
        ) ?: Evaluation(
            subject = subject.name,
            topic = topic,
            date = dateStr,
            studyDaysBefore = studyDays,
            color = subject.color,
            emoji = subject.emoji
        )

        if (editingEvaluation != null) {
            DataManager.updateEvaluation(requireContext(), evaluation)
        } else {
            DataManager.addEvaluation(requireContext(), evaluation)
        }

        onSaved?.invoke()
        dismiss()
    }
}
