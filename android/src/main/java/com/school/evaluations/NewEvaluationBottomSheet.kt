package com.school.evaluations

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import java.util.Calendar

class NewEvaluationBottomSheet : BottomSheetDialogFragment() {

    var onSaved: (() -> Unit)? = null
    var editingEvaluation: Evaluation? = null

    private lateinit var spinnerSubject: Spinner
    private lateinit var editTextTopic: EditText
    private lateinit var editTextDate: TextView

    private var selectedDate: Calendar = Calendar.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.dialog_new_evaluation, container, false)

    override fun onStart() {
        super.onStart()
        val d = dialog as? BottomSheetDialog ?: return
        val bottomSheet = d.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
        bottomSheet?.let {
            val behavior = BottomSheetBehavior.from(it)
            behavior.state = BottomSheetBehavior.STATE_EXPANDED
            behavior.skipCollapsed = true
            it.layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT
        }
        d.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        spinnerSubject = view.findViewById(R.id.spinnerSubject)
        editTextTopic = view.findViewById(R.id.editTextTopic)
        editTextDate = view.findViewById(R.id.editTextDate)

        setupSpinner()
        setupDatePicker()
        populateFields()

        view.findViewById<Button>(R.id.btnCancel).setOnClickListener { dismiss() }
        val btnAdd = view.findViewById<Button>(R.id.btnAdd)
        btnAdd.setOnClickListener { saveEvaluation() }
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
        editTextDate.setOnClickListener {
            DatePickerDialog(
                requireContext(),
                { _, year, month, day ->
                    selectedDate.set(year, month, day)
                    editTextDate.text = formatDateDisplay(selectedDate)
                },
                selectedDate.get(Calendar.YEAR),
                selectedDate.get(Calendar.MONTH),
                selectedDate.get(Calendar.DAY_OF_MONTH)
            ).show()
        }
    }

    private fun populateFields() {
        val eval = editingEvaluation
        if (eval != null) {
            val normalized = Subject.normalizeName(eval.subject)
            val subjectIndex = Subject.DEFAULT_SUBJECTS.indexOfFirst { it.name == normalized }
            if (subjectIndex >= 0) spinnerSubject.setSelection(subjectIndex)
            editTextTopic.setText(eval.topic)

            val parts = eval.date.split("-")
            if (parts.size == 3) {
                try {
                    selectedDate.set(parts[0].toInt(), parts[1].toInt() - 1, parts[2].toInt())
                } catch (_: Exception) {
                }
            }
        }

        editTextDate.text = formatDateDisplay(selectedDate)
    }

    private fun formatDateDisplay(cal: Calendar): String {
        return String.format(
            "%02d / %02d / %d",
            cal.get(Calendar.DAY_OF_MONTH),
            cal.get(Calendar.MONTH) + 1,
            cal.get(Calendar.YEAR)
        )
    }

    private fun saveEvaluation() {
        val topic = editTextTopic.text.toString().trim()
        if (topic.isEmpty()) {
            editTextTopic.error = "El tema no puede estar vacío"
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

        val studyDays = DataManager.getStudyDaysForSubject(requireContext(), subject.name)

        val evaluation = editingEvaluation?.copy(
            subject = subject.name,
            topic = topic,
            date = dateStr,
            studyDaysBefore = studyDays,
            color = subject.color,
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
