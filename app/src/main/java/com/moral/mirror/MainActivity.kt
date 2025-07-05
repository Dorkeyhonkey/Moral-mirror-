package com.moral.mirror

import android.app.*
import android.content.*
import android.graphics.Color
import android.os.*
import android.view.Gravity
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import java.text.SimpleDateFormat
import java.util.*

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
        createNotificationChannel()
        scheduleDailyNotification()
        showQuestion()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "moral_mirror_channel",
                "Moral Mirror Notifications",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Reminders to review your commitments"
            }
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }
    }

    private fun scheduleDailyNotification() {
        val intent = Intent(this, NotificationReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            this,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val alarmManager = getSystemService(ALARM_SERVICE) as AlarmManager

        val calendar = Calendar.getInstance().apply {
            timeInMillis = System.currentTimeMillis()
            set(Calendar.HOUR_OF_DAY, 9)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
        }

        alarmManager.setInexactRepeating(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            AlarmManager.INTERVAL_DAY,
            pendingIntent
        )
    }

    private fun showQuestion() {
        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setBackgroundColor(Color.parseColor("#F5F5F5"))
            setPadding(40, 60, 40, 60)
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        }

        if (currentQuestion < questions.size) {
            val question = questions[currentQuestion]

            val logo = ImageView(this).apply {
                setImageResource(R.drawable.ic_launcher_foreground)
                setPadding(0, 0, 0, 20)
            }

            val questionText = TextView(this).apply {
                textSize = 20f
                text = question
                setTextColor(Color.BLACK)
                setPadding(0, 10, 0, 30)
                gravity = Gravity.CENTER
            }

            val answerGroup = RadioGroup(this).apply {
                setPadding(0, 0, 0, 30)
            }

            val yesButton = RadioButton(this).apply { text = "Yes" }
            val maybeButton = RadioButton(this).apply { text = "Maybe" }
            val noButton = RadioButton(this).apply { text = "No" }

            answerGroup.addView(yesButton)
            answerGroup.addView(maybeButton)
            answerGroup.addView(noButton)

            val commitPrompt = TextView(this).apply {
                text = "Make a personal commitment based on your answer:"
                textSize = 16f
                setPadding(0, 20, 0, 10)
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

            layout.addView(logo)
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
        val timestamp = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(Date())
        val newEntry = "$timestamp | $question | Answer: $answer | Commitment: $commitment"
        existing.add(newEntry)
        sharedPrefs.edit().putStringSet(commitmentsKey, existing).apply()
    }

    private fun showCommitments() {
        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setBackgroundColor(Color.parseColor("#F5F5F5"))
            setPadding(40, 60, 40, 60)
        }

        val commitmentsSet = sharedPrefs.getStringSet(commitmentsKey, emptySet()) ?: emptySet()

        val title = TextView(this).apply {
            text = "Your Commitments"
            textSize = 22f
            setTextColor(Color.BLACK)
            gravity = Gravity.CENTER
            setPadding(0, 0, 0, 20)
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
            commitmentsSet.sortedDescending().forEach { commitment ->
                val tv = TextView(this).apply {
                    text = commitment
                    setPadding(0, 10, 0, 10)
                    setTextColor(Color.DKGRAY)
                }
                commitmentsLayout.addView(tv)
            }
        }

        scrollView.addView(commitmentsLayout)

        val viewProgressButton = Button(this).apply {
            text = "View Progress"
            setOnClickListener {
                val intent = Intent(this@MainActivity, ProgressActivity::class.java)
                startActivity(intent)
            }
        }

        val restartButton = Button(this).apply {
            text = "Restart Quiz"
            setOnClickListener {
                currentQuestion = 0
                showQuestion()
            }
        }

        layout.addView(title)
        layout.addView(scrollView)
        layout.addView(viewProgressButton)
        layout.addView(restartButton)

        setContentView(layout)
    }
}
