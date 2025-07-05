package com.moral.mirror

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private val questions = listOf(
        "Is it okay to lie if it helps someone?",
        "Would you steal to feed your family?",
        "Should you always obey the law?",
        "Is revenge ever justified?",
        "Should you sacrifice one life to save many?"
    )
    private val answers = mutableMapOf<String, Boolean>()
    private var currentQuestion = 0
    private var score = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(32, 32, 32, 32)
        }

        val questionText = TextView(this).apply {
            textSize = 18f
            text = questions[currentQuestion]
        }

        val yesButton = Button(this).apply { text = "Yes" }
        val noButton = Button(this).apply { text = "No" }
        val scoreText = TextView(this).apply {
            textSize = 16f
            text = "Score: $score"
        }

        yesButton.setOnClickListener {
            answers[questions[currentQuestion]] = true
            score += 1
            nextQuestion(questionText, scoreText)
        }

        noButton.setOnClickListener {
            answers[questions[currentQuestion]] = false
            nextQuestion(questionText, scoreText)
        }

        layout.addView(questionText)
        layout.addView(yesButton)
        layout.addView(noButton)
        layout.addView(scoreText)

        setContentView(layout)
    }

    private fun nextQuestion(questionText: TextView, scoreText: TextView) {
        currentQuestion += 1
        if (currentQuestion < questions.size) {
            questionText.text = questions[currentQuestion]
            scoreText.text = "Score: $score"
        } else {
            questionText.text = "Your moral mirror score: $score / ${questions.size}"
        }
    }
}
