package com.passionpenguin.ditiezu

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.microsoft.appcenter.AppCenter
import com.microsoft.appcenter.analytics.Analytics
import com.microsoft.appcenter.crashes.Crashes
import com.tencent.bugly.crashreport.CrashReport
import java.util.*

class Splash : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        CrashReport.initCrashReport(applicationContext, "8555ad868a", false)
        AppCenter.start(
            application, "84a9bc99-3f3b-4a6c-a8f5-2007ce79a16c",
            Analytics::class.java, Crashes::class.java
        )

        Timer().schedule(object : TimerTask() {
            override fun run() {
                startActivity(Intent(this@Splash, MainActivity::class.java))
            }
        }, 1500)
    }
}