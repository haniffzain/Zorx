package com.raven.launcher.startmenu

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.view.Gravity
import android.widget.LinearLayout
import android.widget.TextView

class StartMenuView(context: Context) : LinearLayout(context) {

    init {
        orientation = VERTICAL
        gravity = Gravity.TOP

        val menuBackground = GradientDrawable().apply {
            setColor(Color.rgb(25, 25, 30))
            cornerRadius = 18f
        }

        background = menuBackground

        setPadding(
            30,
            30,
            30,
            30
        )

        addMenuItem("🦅  Raven OS")
        addMenuItem("📱  Applications")
        addMenuItem("📁  Files")
        addMenuItem("⚙  Settings")
        addMenuItem("🔍  Search")
        addMenuItem("⏻  Power")
    }

    private fun addMenuItem(title: String) {

        val item = TextView(context).apply {
            text = title
            textSize = 18f
            setTextColor(Color.WHITE)
            gravity = Gravity.CENTER_VERTICAL
            setPadding(
                20,
                20,
                20,
                20
            )
        }

        addView(
            item,
            LayoutParams(
                LayoutParams.MATCH_PARENT,
                LayoutParams.WRAP_CONTENT
            )
        )
    }
}
