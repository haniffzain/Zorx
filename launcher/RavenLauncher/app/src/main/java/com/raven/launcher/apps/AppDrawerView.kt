package com.raven.launcher.apps

import android.content.Context
import android.graphics.Color
import android.view.Gravity
import android.widget.GridLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView

class AppDrawerView(context: Context) : ScrollView(context) {

    private val appManager = AppManager(context)

    init {

        setBackgroundColor(Color.rgb(20, 22, 28))

        val grid = GridLayout(context).apply {
            columnCount = 5

            setPadding(
                30,
                30,
                30,
                30
            )
        }

        val apps = appManager.getInstalledApps()

        apps.forEach { app ->

            val appContainer = LinearLayout(context).apply {

                orientation = LinearLayout.VERTICAL
                gravity = Gravity.CENTER

                setPadding(
                    20,
                    20,
                    20,
                    20
                )

                isClickable = true
                isFocusable = true

                setOnClickListener {
                    appManager.launchApp(app)
                }
            }

            val icon = ImageView(context).apply {

                setImageDrawable(
                    app.loadIcon(context.packageManager)
                )

                layoutParams = LinearLayout.LayoutParams(
                    72,
                    72
                )
            }

            val label = TextView(context).apply {

                text = app.loadLabel(
                    context.packageManager
                )

                setTextColor(Color.WHITE)

                textSize = 14f

                gravity = Gravity.CENTER

                maxLines = 2

                setPadding(
                    5,
                    8,
                    5,
                    5
                )
            }

            appContainer.addView(icon)
            appContainer.addView(label)

            val itemParams = GridLayout.LayoutParams().apply {

                width = 180
                height = 150

                setMargins(
                    10,
                    10,
                    10,
                    10
                )
            }

            grid.addView(
                appContainer,
                itemParams
            )
        }

        addView(grid)
    }
}
