package com.passionpenguin.ditiezu

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log

class AccountActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_account)

        if (!HttpExt().checkLogin())
            startActivity(Intent(this@AccountActivity, LoginActivity::class.java))
    }
}