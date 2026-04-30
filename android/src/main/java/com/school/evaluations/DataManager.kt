package com.school.evaluations

import android.content.Context
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import com.google.gson.reflect.TypeToken

object DataManager {
    private const val PREFS_NAME = "school_evaluations"
    private const val KEY_EVALUATIONS = "evaluations"
    private const val KEY_STUDY_CONFIG = "study_config"

    private val DEFAULT_STUDY_DAYS = mapOf(
        "Matemáticas" to 6,
        "Ciencias" to 7,
        "Lengua" to 3,
        "Español" to 3,
        "Historia" to 4,
        "Inglés" to 3,
        "Educación Física" to 2,
        "Arte" to 2,
        "Música" to 2
    )
    
    fun getEvaluations(context: Context): MutableList<Evaluation> {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val json = prefs.getString(KEY_EVALUATIONS, null)

        if (json.isNullOrBlank()) return mutableListOf()

        return try {
            val type = object : TypeToken<MutableList<Evaluation>>() {}.type
            Gson().fromJson<MutableList<Evaluation>>(json, type) ?: mutableListOf()
        } catch (_: JsonSyntaxException) {
            mutableListOf()
        }
    }
    
    fun saveEvaluations(context: Context, evaluations: List<Evaluation>) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val json = Gson().toJson(evaluations)
        prefs.edit().putString(KEY_EVALUATIONS, json).apply()
    }
    
    fun addEvaluation(context: Context, evaluation: Evaluation) {
        val evaluations = getEvaluations(context)
        evaluations.add(evaluation)
        saveEvaluations(context, evaluations)
    }
    
    fun updateEvaluation(context: Context, updated: Evaluation) {
        val evaluations = getEvaluations(context)
        val index = evaluations.indexOfFirst { it.id == updated.id }
        if (index != -1) {
            evaluations[index] = updated
            saveEvaluations(context, evaluations)
        }
    }
    
    fun deleteEvaluation(context: Context, evaluationId: String) {
        val evaluations = getEvaluations(context)
        evaluations.removeAll { it.id == evaluationId }
        saveEvaluations(context, evaluations)
    }

    fun getStudyConfig(context: Context): Map<String, Int> {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val json = prefs.getString(KEY_STUDY_CONFIG, null)
        if (json.isNullOrBlank()) return emptyMap()
        return try {
            val type = object : TypeToken<Map<String, Int>>() {}.type
            Gson().fromJson<Map<String, Int>>(json, type) ?: emptyMap()
        } catch (_: JsonSyntaxException) {
            emptyMap()
        }
    }

    fun saveStudyConfig(context: Context, config: Map<String, Int>) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putString(KEY_STUDY_CONFIG, Gson().toJson(config)).apply()
    }

    fun getDefaultStudyDaysForSubject(subjectName: String): Int {
        return DEFAULT_STUDY_DAYS[subjectName] ?: 3
    }

    fun getStudyDaysForSubject(context: Context, subjectName: String): Int {
        val configured = getStudyConfig(context)[subjectName]
        return configured ?: getDefaultStudyDaysForSubject(subjectName)
    }

    fun getStudyConfigWithDefaults(context: Context): Map<String, Int> {
        val configured = getStudyConfig(context)
        val subjects = Subject.DEFAULT_SUBJECTS.map { it.name }
        val merged = linkedMapOf<String, Int>()

        for (subject in subjects) {
            merged[subject] = configured[subject] ?: getDefaultStudyDaysForSubject(subject)
        }

        return merged
    }
}
