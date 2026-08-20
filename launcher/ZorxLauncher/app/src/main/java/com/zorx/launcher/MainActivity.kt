package com.zorx.launcher

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.zorx.launcher.desktop.DesktopActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        startActivity(
            Intent(this, DesktopActivity::class.java)
        )

        finish()
    }
}
