package com.passionpenguin.ditiezu

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_donate.*

class Donate : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_donate)

        wechatpay.setOnClickListener {
            wechatPayImage.visibility = View.VISIBLE
            wechatPayImage.animate().alpha(1F).setDuration(500).start()
            alipayImage.animate().alpha(0F).setDuration(500).start()
            alipayImage.postDelayed({
                alipayImage.visibility = View.INVISIBLE
            }, 500)
        }
        alipay.setOnClickListener {
            alipayImage.visibility = View.VISIBLE
            alipayImage.animate().alpha(1F).setDuration(500).start()
            wechatPayImage.animate().alpha(0F).setDuration(500).start()
            wechatPayImage.postDelayed({
                wechatPayImage.visibility = View.INVISIBLE
            }, 500)
        }
    }
}