package com.passionpenguin.ditiezu

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.View
import androidx.appcompat.app.AppCompatActivity

class Splash : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        window.decorView.apply {
            systemUiVisibility = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
        }

        Handler().postDelayed(
            {
                val myIntent = Intent(this@Splash, MainActivity::class.java)
                this@Splash.startActivity(myIntent)
            },
            500
        )
    }
}