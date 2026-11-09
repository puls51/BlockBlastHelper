package com.example.blockblasthelper

import android.app.Service
import android.content.Intent
import android.graphics.PixelFormat
import android.os.IBinder
import android.view.Gravity
import android.view.WindowManager
import android.widget.Button
import android.widget.Toast

class FloatingButtonService : Service() {

    private var windowManager: WindowManager? = null
    private var floatingButton: Button? = null

    override fun onStartCommand(
        intent: Intent?,
        flags: Int,
        startId: Int
    ): Int {

        if (floatingButton != null) return START_STICKY

        windowManager = getSystemService(WINDOW_SERVICE) as WindowManager

        val button = Button(this).apply {
            text = "Анализ"
            textSize = 14f

            setOnClickListener {
    val intent = Intent(
        this@FloatingButtonService,
        MainActivity::class.java
    ).apply {
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        putExtra("request_capture", true)
    }
    startActivity(intent)
}
        }

        val params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSLUCENT
        ).apply {
            gravity = Gravity.TOP or Gravity.END
            x = 30
            y = 250
        }

        floatingButton = button
        windowManager?.addView(button, params)

        return START_STICKY
    }

    override fun onDestroy() {
        floatingButton?.let {
            windowManager?.removeView(it)
        }
        floatingButton = null
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? = null
}
