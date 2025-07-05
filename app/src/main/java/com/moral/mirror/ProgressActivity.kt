package com.moral.mirror

import android.os.Bundle
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import android.graphics.Color

class ProgressActivity : AppCompatActivity() {

    private val commitmentsKey = "commitments"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setBackgroundColor(Color.parseColor("#F5F5F5"))
            setPadding(40, 60, 40, 60)
        }

        val title = TextView(this).apply {
            text = "Your Progress"
            textSize = 22f
            setTextColor(Color.BLACK)
            setPadding(0, 0, 0, 20)
        }

        val scrollView = ScrollView(this)
        val contentLayout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
        }

        val sharedPrefs = getSharedPreferences("MoralMirrorPrefs", MODE_PRIVATE)
        val commitments = sharedPrefs.getStringSet(commitmentsKey, emptySet()) ?: emptySet()

        if (commitments.isEmpty()) {
            val noData = TextView(this).apply {
                text = "No commitments saved yet."
                textSize = 16f
            }
            contentLayout.addView(noData)
        } else {
            val sortedList = commitments.sortedDescending()
            sortedList.forEach { entry ->
                val tv = TextView(this).apply {
                    text = entry
                    setPadding(0, 10, 0, 10)
                    setTextColor(Color.DKGRAY)
                }
                contentLayout.addView(tv)
            }
        }

        scrollView.addView(contentLayout)

        val backButton = Button(this).apply {
            text = "Back"
            setOnClickListener { finish() }
        }

        layout.addView(title)
        layout.addView(scrollView)
        layout.addView(backButton)

        setContentView(layout)
    }
}
