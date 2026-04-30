package com.school.evaluations

data class Subject(
    val name: String,
    val emoji: String,
    val color: String
) {
    companion object {
        val DEFAULT_SUBJECTS = listOf(
            Subject("Matemáticas", "📐", "#3B82F6"),
            Subject("Español", "📝", "#EC4899"),
            Subject("Ciencias", "🔬", "#22C55E"),
            Subject("Historia", "📜", "#F59E0B"),
            Subject("Inglés", "🌍", "#8B5CF6"),
            Subject("Arte", "🎨", "#EF4444"),
            Subject("Educación Física", "⚽", "#14B8A6"),
            Subject("Música", "🎵", "#6366F1"),
            Subject("Tecnología", "💻", "#06B6D4"),
            Subject("Otra", "📚", "#6B7280")
        )

        fun findByName(name: String): Subject? = DEFAULT_SUBJECTS.firstOrNull { it.name == name }
    }
}
