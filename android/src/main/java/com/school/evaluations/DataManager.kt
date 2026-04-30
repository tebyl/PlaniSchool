package com.school.evaluations

import android.content.Context
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import com.google.gson.reflect.TypeToken

object DataManager {
    private const val PREFS_NAME = "school_evaluations"
    private const val KEY_EVALUATIONS = "evaluations"
    
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
}
