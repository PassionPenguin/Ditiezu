package com.passionpenguin.ditiezu

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import com.passionpenguin.ditiezu.helper.HttpExt
import java.util.*

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val webView: WebView = findViewById(R.id.LoginWebView)
        WebView.setWebContentsDebuggingEnabled(true)
        webView.settings.javaScriptEnabled = true
        webView.webViewClient = (object : WebViewClient() {
            override fun shouldOverrideUrlLoading(
                view: WebView?,
                request: WebResourceRequest?
            ): Boolean {
                val url = request?.url.toString()
                return if (url.indexOf("mod=logging") == -1 || url.indexOf("ditiezu.com") == -1) {
                    Log.i(url, url.indexOf("ditiezu.com").toString())
                    view?.evaluateJavascript(
                        "window.open('http://www.ditiezu.com/member.php?mod=logging&action=login&mobile=yes');",
                        null
                    )
                    true
                } else false
            }
        })
        webView.loadUrl("http://www.ditiezu.com/forum.php?mod=forum")
        Timer().scheduleAtFixedRate(object : TimerTask() {
            override fun run() {
                if (HttpExt().checkLogin())
                    startActivity(Intent(this@LoginActivity, AccountActivity::class.java))
            }
        }, 0, 5000)
    }
}