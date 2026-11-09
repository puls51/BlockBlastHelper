package com.example.blockblasthelper

import android.app.Activity
import android.os.Bundle
import android.graphics.Color
import android.view.Gravity
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView

class MainActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
            setPadding(32, 32, 32, 32)
            setBackgroundColor(Color.rgb(25, 25, 35))
        }

        val title = TextView(this).apply {
            text = "Block Blast Helper"
            textSize = 26f
            setTextColor(Color.WHITE)
            gravity = Gravity.CENTER
        }

        val button = Button(this).apply {
            text = "Анализ поля"
            textSize = 18f
            setOnClickListener {
                result.text = "Следующий этап: распознавание игрового поля"
            }
        }

        val result = TextView(this).apply {
            text = "Помощник готов к настройке"
            textSize = 16f
            setTextColor(Color.WHITE)
            gravity = Gravity.CENTER
        }

        layout.addView(title)
        layout.addView(button)
        layout.addView(result)

        setContentView(layout)
    }
}
