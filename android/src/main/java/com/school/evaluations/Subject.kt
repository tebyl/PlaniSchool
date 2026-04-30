package com.school.evaluations

data class Subject(
    val name: String,
    val emoji: String,
    val color: String
) {
    companion object {
        val DEFAULT_SUBJECTS = listOf(
            Subject("Matemáticas", "🔢", "from-blue-400 to-blue-600"),
            Subject("Español", "📝", "from-pink-400 to-pink-600"),
            Subject("Ciencias", "🔬", "from-green-400 to-green-600"),
            Subject("Historia", "📜", "from-amber-400 to-amber-600"),
            Subject("Inglés", "🌍", "from-purple-400 to-purple-600"),
            Subject("Arte", "🎨", "from-red-400 to-red-600"),
            Subject("Educación Física", "⚽", "from-teal-400 to-teal-600"),
            Subject("Música", "🎵", "from-indigo-400 to-indigo-600"),
            Subject("Tecnología", "💻", "from-cyan-400 to-cyan-600"),
            Subject("Otra", "📚", "from-gray-400 to-gray-600")
        )
    }
}
