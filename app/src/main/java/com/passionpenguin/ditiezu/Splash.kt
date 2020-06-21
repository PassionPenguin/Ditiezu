package com.passionpenguin.ditiezu

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.MobileAds
import kotlinx.android.synthetic.main.activity_splash.*


class Splash : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

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