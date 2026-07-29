package com.raven.launcher.recents

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class RecentsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Temporary Raven Recents entry point.
        // Full Overview/Quickstep implementation will come later.
        finish()
    }
}
