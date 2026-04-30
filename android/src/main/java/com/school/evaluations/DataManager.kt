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
        "Lenguaje" to 4,
        "Matemáticas" to 6,
        "Música" to 2,
        "Ed. Física" to 2,
        "Orientación" to 2,
        "Historia" to 4,
        "Ciencias" to 5,
        "Inglés" to 3,
        "Artes" to 2,
        "Tecnología" to 3
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
        val normalized = Subject.normalizeName(subjectName)
        return DEFAULT_STUDY_DAYS[normalized] ?: 3
    }

    fun getStudyDaysForSubject(context: Context, subjectName: String): Int {
        val normalized = Subject.normalizeName(subjectName)
        val configured = getStudyConfig(context)

        val byNormalized = configured[normalized]
        if (byNormalized != null) return byNormalized

        // Compatibilidad: si hay config guardada bajo nombre antiguo, usarla.
        val byLegacyName = configured[subjectName]
        if (byLegacyName != null) return byLegacyName

        return getDefaultStudyDaysForSubject(normalized)
    }

    fun getStudyConfigWithDefaults(context: Context): Map<String, Int> {
        val merged = linkedMapOf<String, Int>()
        for (subject in Subject.DEFAULT_SUBJECTS) {
            merged[subject.name] = getStudyDaysForSubject(context, subject.name)
        }
        return merged
    }
}
