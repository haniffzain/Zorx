package com.raven.launcher.taskbar

import android.content.Context
import android.graphics.Color
import android.view.Gravity
import android.widget.LinearLayout
import android.widget.TextView

class TaskbarView(
    context: Context,
    private val onStartClick: () -> Unit
) : LinearLayout(context) {

    init {

        orientation = HORIZONTAL
        gravity = Gravity.CENTER_VERTICAL

        setBackgroundColor(Color.BLACK)

        val startButton = TextView(context)

        startButton.text = "🦅 Raven"
        startButton.textSize = 18f
        startButton.setTextColor(Color.WHITE)
        startButton.gravity = Gravity.CENTER_VERTICAL

        startButton.setPadding(
            30,
            15,
            30,
            15
        )

        startButton.setOnClickListener {
            onStartClick()
        }

        addView(startButton)
    }
}
