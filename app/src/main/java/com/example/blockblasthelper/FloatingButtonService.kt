
package com.example.blockblasthelper

import android.app.Activity
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.content.pm.ServiceInfo
import android.graphics.PixelFormat
import android.hardware.display.DisplayManager
import android.hardware.display.VirtualDisplay
import android.media.ImageReader
import android.media.projection.MediaProjection
import android.media.projection.MediaProjectionManager
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.view.Gravity
import android.view.WindowManager
import android.widget.Button
import android.widget.Toast
class FloatingButtonService : Service() {

    private var windowManager: WindowManager? = null
    private var floatingButton: Button? = null
    private var mediaProjection: MediaProjection? = null
    private var virtualDisplay: VirtualDisplay? = null
    private var imageReader: ImageReader? = null
    private var firstFrameShown = false
    override fun onStartCommand(
        intent: Intent?,
        flags: Int,
        startId: Int
    ): Int {

        if (floatingButton == null) {
            showFloatingButton()
        }

        if (intent?.hasExtra("resultCode") == true &&
            intent.hasExtra("data") &&
            mediaProjection == null
        ) {
            startScreenCapture(intent)
        }

        return START_STICKY
    }

    private fun showFloatingButton() {
        windowManager =
            getSystemService(WINDOW_SERVICE) as WindowManager

        val button = Button(this).apply {
            text = "Анализ"
            textSize = 14f

            setOnClickListener {
    if (mediaProjection != null) {
        Toast.makeText(
            this@FloatingButtonService,
            "Захват экрана уже активен",
            Toast.LENGTH_SHORT
        ).show()
    } else {
        val activityIntent = Intent(
            this@FloatingButtonService,
            MainActivity::class.java
        ).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            putExtra("request_capture", true)
        }

        startActivity(activityIntent)
    }
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
    }

    private fun startScreenCapture(intent: Intent) {
        val resultCode = intent.getIntExtra(
            "resultCode",
            Activity.RESULT_CANCELED
        )

        @Suppress("DEPRECATION")
        val data: Intent? =
            if (Build.VERSION.SDK_INT >= 33) {
                intent.getParcelableExtra("data", Intent::class.java)
            } else {
                intent.getParcelableExtra("data")
            }

        if (resultCode != Activity.RESULT_OK || data == null) {
            return
        }

        createNotificationChannel()

        val notification = if (Build.VERSION.SDK_INT >= 26) {
            Notification.Builder(this, "screen_capture")
                .setContentTitle("Block Blast Helper")
                .setContentText("Захват экрана активен")
                .setSmallIcon(android.R.drawable.ic_menu_camera)
                .build()
        } else {
            @Suppress("DEPRECATION")
            Notification.Builder(this)
                .setContentTitle("Block Blast Helper")
                .setContentText("Захват экрана активен")
                .setSmallIcon(android.R.drawable.ic_menu_camera)
                .build()
        }

        if (Build.VERSION.SDK_INT >= 29) {
            startForeground(
                1001,
                notification,
                ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PROJECTION
            )
        } else {
            startForeground(1001, notification)
        }

        val manager = getSystemService(
            MEDIA_PROJECTION_SERVICE
        ) as MediaProjectionManager

        mediaProjection = manager.getMediaProjection(resultCode, data)

        val metrics = resources.displayMetrics
        val width = metrics.widthPixels
        val height = metrics.heightPixels
        val density = metrics.densityDpi

        imageReader = ImageReader.newInstance(
            width,
            height,
            PixelFormat.RGBA_8888,
            2
        )

        imageReader?.setOnImageAvailableListener(
    { reader ->
        val image = reader.acquireLatestImage()

        if (image != null) {
            if (!firstFrameShown) {
                firstFrameShown = true

                android.os.Handler(Looper.getMainLooper()).post {
                    android.widget.Toast.makeText(
                        this,
                        "Первый кадр экрана получен!",
                        android.widget.Toast.LENGTH_LONG
                    ).show()
                }
            }

            image.close()
        }
    },
    Handler(Looper.getMainLooper())
)

        virtualDisplay = mediaProjection?.createVirtualDisplay(
            "BlockBlastCapture",
            width,
            height,
            density,
            DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR,
            imageReader?.surface,
            null,
            null
        )
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= 26) {
            val channel = NotificationChannel(
                "screen_capture",
                "Захват экрана",
                NotificationManager.IMPORTANCE_LOW
            )

            val manager = getSystemService(
                NotificationManager::class.java
            )

            manager.createNotificationChannel(channel)
        }
    }

    override fun onDestroy() {
        virtualDisplay?.release()
        imageReader?.close()
        mediaProjection?.stop()

        floatingButton?.let {
            windowManager?.removeView(it)
        }

        floatingButton = null
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? = null
}
