package com.school.evaluations

data class Subject(
    val name: String,
    val emoji: String,
    val color: String
) {
    companion object {
        val DEFAULT_SUBJECTS = listOf(
            Subject("Lenguaje", "📖", "#EC4899"),
            Subject("Matemáticas", "📐", "#3B82F6"),
            Subject("Música", "🎵", "#6366F1"),
            Subject("Ed. Física", "⚽", "#14B8A6"),
            Subject("Orientación", "🧭", "#8B5CF6"),
            Subject("Historia", "📜", "#F59E0B"),
            Subject("Ciencias", "🧪", "#22C55E"),
            Subject("Inglés", "🔤", "#06B6D4"),
            Subject("Artes", "🎨", "#EF4444"),
            Subject("Tecnología", "💻", "#6B7280")
        )

        private val legacyAliases = mapOf(
            "Lengua" to "Lenguaje",
            "Español" to "Lenguaje",
            "Arte" to "Artes",
            "Educación Física" to "Ed. Física",
            "Educacion Fisica" to "Ed. Física",
            "Otra" to "Orientación"
        )

        fun normalizeName(name: String): String {
            return legacyAliases[name] ?: name
        }

        fun findByName(name: String): Subject? {
            val normalized = normalizeName(name)
            return DEFAULT_SUBJECTS.firstOrNull { it.name == normalized }
        }
    }
}
