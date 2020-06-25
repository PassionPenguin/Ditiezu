package com.passionpenguin.ditiezu

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.MobileAds
import com.microsoft.appcenter.AppCenter
import com.microsoft.appcenter.analytics.Analytics
import com.microsoft.appcenter.crashes.Crashes
import com.tencent.bugly.crashreport.CrashReport
import kotlinx.android.synthetic.main.activity_splash.*

class Splash : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        CrashReport.initCrashReport(applicationContext, "8555ad868a", false)
        AppCenter.start(
            application, "84a9bc99-3f3b-4a6c-a8f5-2007ce79a16c",
            Analytics::class.java, Crashes::class.java
        )

        MobileAds.initialize(
            this
        ) { }

        adView.loadAd(AdRequest.Builder().build())
        adView.adListener = object : AdListener() {
            override fun onAdLoaded() {
                super.onAdLoaded()
                adView.loadAd(AdRequest.Builder().build())
            }

            override fun onAdFailedToLoad(p0: Int) {
                super.onAdFailedToLoad(p0)
                startActivity(Intent(this@Splash, MainActivity::class.java))
            }

            override fun onAdClosed() {
                super.onAdClosed()
                startActivity(Intent(this@Splash, MainActivity::class.java))
            }
        }
    }
}