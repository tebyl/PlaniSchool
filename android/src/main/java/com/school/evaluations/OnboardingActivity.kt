package com.school.evaluations

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.viewpager2.widget.ViewPager2

class OnboardingActivity : AppCompatActivity() {

    private lateinit var viewPager: ViewPager2
    private lateinit var dotsContainer: LinearLayout
    private lateinit var btnNext: Button
    private lateinit var btnSkip: TextView

    private val pages = listOf(
        OnboardingPage(
            title = "📚 Bienvenido a PlaniSchool",
            description = "Organiza tus pruebas y tareas de forma fácil y divertida 🎉",
            visual = "🧒📅"
        ),
        OnboardingPage(
            title = "🧠 Planifica sin estrés",
            description = "La app te dice cuándo empezar a estudiar para cada evaluación",
            visual = "🗓️✨"
        ),
        OnboardingPage(
            title = "🎯 Define tus prioridades",
            description = "Elige cuántos días necesitas estudiar cada materia",
            visual = "📚🎚️"
        ),
        OnboardingPage(
            title = "🚀 ¡Todo listo!",
            description = "Comienza a organizar tus evaluaciones como un pro 💪",
            visual = "✅🌟"
        )
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_onboarding)

        viewPager = findViewById(R.id.viewPagerOnboarding)
        dotsContainer = findViewById(R.id.dotsContainer)
        btnNext = findViewById(R.id.btnNext)
        btnSkip = findViewById(R.id.btnSkip)

        viewPager.adapter = OnboardingPagerAdapter(pages)
        setupDots()
        updateDots(0)
        updateButtons(0)

        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                updateDots(position)
                updateButtons(position)
            }
        })

        btnSkip.setOnClickListener { finishOnboarding() }

        btnNext.setOnClickListener {
            val next = viewPager.currentItem + 1
            if (next < pages.size) {
                viewPager.currentItem = next
            } else {
                finishOnboarding()
            }
        }
    }

    private fun setupDots() {
        dotsContainer.removeAllViews()
        repeat(pages.size) {
            val dot = TextView(this).apply {
                text = "•"
                textSize = 24f
                setTextColor(ContextCompat.getColor(this@OnboardingActivity, R.color.gray_300))
                setPadding(8, 0, 8, 0)
            }
            dotsContainer.addView(dot)
        }
    }

    private fun updateDots(position: Int) {
        for (i in 0 until dotsContainer.childCount) {
            val dot = dotsContainer.getChildAt(i) as TextView
            val color = if (i == position) R.color.grad_purple else R.color.gray_300
            dot.setTextColor(ContextCompat.getColor(this, color))
        }
    }

    private fun updateButtons(position: Int) {
        val isLast = position == pages.lastIndex
        btnNext.text = if (isLast) "Empezar" else "Siguiente"
    }

    private fun finishOnboarding() {
        getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
            .edit()
            .putBoolean(KEY_ONBOARDING_COMPLETED, true)
            .apply()

        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }

    companion object {
        const val PREFS_NAME = "plani_school_prefs"
        const val KEY_ONBOARDING_COMPLETED = "onboarding_completed"
    }
}
