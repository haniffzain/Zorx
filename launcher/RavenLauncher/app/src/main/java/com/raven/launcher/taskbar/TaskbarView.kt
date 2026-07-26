package com.raven.launcher.taskbar

import android.content.Context
import android.graphics.Color
import android.view.Gravity
import android.widget.LinearLayout
import android.widget.TextView

class TaskbarView(context: Context) : LinearLayout(context) {

    init {

        orientation = HORIZONTAL

        gravity = Gravity.CENTER_VERTICAL

        setBackgroundColor(Color.BLACK)

        val startButton = TextView(context)

        startButton.text = "🦅 Raven"

        startButton.textSize = 18f

        startButton.setTextColor(Color.WHITE)

        startButton.setPadding(
            30,
            15,
            30,
            15
        )

        addView(startButton)

    }
}
