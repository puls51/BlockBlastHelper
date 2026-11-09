package com.example.blockblasthelper

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.media.projection.MediaProjectionManager
import android.os.Bundle
import android.provider.Settings
import android.net.Uri
import android.view.Gravity
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast

class MainActivity : Activity() {

    private lateinit var projectionManager: MediaProjectionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        projectionManager =
            getSystemService(Context.MEDIA_PROJECTION_SERVICE) as MediaProjectionManager

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
                    startService(Intent(this@MainActivity, FloatingButtonService::class.java))
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

        if (intent.getBooleanExtra("request_capture", false)) {
            val captureIntent = projectionManager.createScreenCaptureIntent()
            startActivityForResult(captureIntent, 1001)
        }
    }
}
