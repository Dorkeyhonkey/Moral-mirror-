














































package com.moral.mirror

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.ViewGroup
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
    private var currentQuestion = 0
    private lateinit var sharedPrefs: SharedPreferences
    private val commitmentsKey = "commitments"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedPrefs = getSharedPreferences("MoralMirrorPrefs", Context.MODE_PRIVATE)
        showQuestion()
    }

    private fun showQuestion() {
        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(32, 32, 32, 32)
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        }

        if (currentQuestion < questions.size) {
            val question = questions[currentQuestion]

            val questionText = TextView(this).apply {
                textSize = 18f
                text = question
            }

            val answerGroup = RadioGroup(this)

            val yesButton = RadioButton(this).apply { text = "Yes" }
            val maybeButton = RadioButton(this).apply { text = "Maybe" }
            val noButton = RadioButton(this).apply { text = "No" }

            answerGroup.addView(yesButton)
            answerGroup.addView(maybeButton)
            answerGroup.addView(noButton)

            val commitPrompt = TextView(this).apply {
                text = "Make a personal commitment based on your answer:"
                textSize = 16f
                setPadding(0, 20, 0, 0)
            }

            val commitInput = EditText(this).apply {
                hint = "Enter your commitment here"
            }

            val submitButton = Button(this).apply {
                text = "Save Commitment & Next"
                isEnabled = false
            }

            answerGroup.setOnCheckedChangeListener { _, _ ->
                submitButton.isEnabled = true
            }

            submitButton.setOnClickListener {
                val selectedId = answerGroup.checkedRadioButtonId
                if (selectedId != -1) {
                    val answer = findViewById<RadioButton>(selectedId).text.toString()
                    val commitmentText = commitInput.text.toString().trim()
                    if (commitmentText.isNotEmpty()) {
                        saveCommitment(question, answer, commitmentText)
                        currentQuestion++
                        showQuestion()
                    } else {
                        Toast.makeText(this, "Please enter a commitment", Toast.LENGTH_SHORT).show()
                    }
                }
            }

            layout.addView(questionText)
            layout.addView(answerGroup)
            layout.addView(commitPrompt)
            layout.addView(commitInput)
            layout.addView(submitButton)

            setContentView(layout)
        } else {
            showCommitments()
        }
    }

    private fun saveCommitment(question: String, answer: String, commitment: String) {
        val existing = sharedPrefs.getStringSet(commitmentsKey, mutableSetOf()) ?: mutableSetOf()
        val newEntry = "$question | Answer: $answer | Commitment: $commitment"
        existing.add(newEntry)
        sharedPrefs.edit().putStringSet(commitmentsKey, existing).apply()
    }

    private fun showCommitments() {
        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(32, 32, 32, 32)
        }

        val commitmentsSet = sharedPrefs.getStringSet(commitmentsKey, emptySet()) ?: emptySet()

        val title = TextView(this).apply {
            text = "Your Commitments"
            textSize = 20f
        }

        val scrollView = ScrollView(this)
        val commitmentsLayout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
        }

        if (commitmentsSet.isEmpty()) {
            val noCommitments = TextView(this).apply {
                text = "No commitments yet."
                textSize = 16f
            }
            commitmentsLayout.addView(noCommitments)
        } else {
            commitmentsSet.forEach { commitment ->
                val tv = TextView(this).apply {
                    text = commitment
                    setPadding(0, 10, 0, 10)
                }
                commitmentsLayout.addView(tv)
            }
        }

        scrollView.addView(commitmentsLayout)

        val restartButton = Button(this).apply {
            text = "Restart Quiz"
            setOnClickListener {
                currentQuestion = 0
                showQuestion()
            }
        }

        layout.addView(title)
        layout.addView(scrollView)
        layout.addView(restartButton)

        setContentView(layout)
    }
}
