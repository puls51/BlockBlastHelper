package com.example.blockblasthelper

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.Gravity
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast

class MainActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
            setPadding(32, 32, 32, 32)
        }

        val title = TextView(this).apply {
            text = "Block Blast Helper"
            textSize = 25f
            gravity = Gravity.CENTER
        }

        val button = Button(this).apply {
            text = "Включить плавающую кнопку"
            setOnClickListener {
                if (Settings.canDrawOverlays(this@MainActivity)) {
                    startFloatingService()
                } else {
                    val intent = Intent(
                        Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                        Uri.parse("package:$packageName")
                    )
                    startActivity(intent)
                }
            }
        }

        layout.addView(title)
        layout.addView(button)
        setContentView(layout)
    }

    private fun startFloatingService() {
        startService(
            Intent(this, FloatingButtonService::class.java)
        )
        Toast.makeText(
            this,
            "Плавающая кнопка включена",
            Toast.LENGTH_SHORT
        ).show()
    }
}
