package com.passionpenguin.ditiezu

import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import android.view.*
import android.webkit.JavascriptInterface
import android.webkit.WebView
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isGone
import com.google.android.material.snackbar.Snackbar
import org.jsoup.Jsoup

class ViewThread : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_thread)
        actionBar?.setDisplayHomeAsUpEnabled(true)

        val darkMode =
            this.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK === Configuration.UI_MODE_NIGHT_YES

        val extras = intent.extras
        var tid = 1
        if (extras != null) {
            tid = extras.getInt("tid")
        } else finish()

        findViewById<com.google.android.material.floatingactionbutton.FloatingActionButton>(R.id.fab).setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }

        val container: ViewGroup = findViewById(R.id.viewThreadContainer)

        fun retrieveThreadContent(page: Int = 1) {
            fun processResult(result: String) {
                val parser = Jsoup.parse(result)
                this@ViewThread.runOnUiThread {
                    title = parser.select("#thread_subject").text()
                    val webView = WebView(applicationContext)
                    webView.settings.javaScriptEnabled = true
                    class WebViewInterface() {
                        @JavascriptInterface
                        fun getLoadedPage(): String {
                            return result;
                        }

                        @JavascriptInterface
                        fun isDarkMode(): Boolean {
                            return darkMode;
                        }
                    }
                    webView.addJavascriptInterface(WebViewInterface(), "android")
                    webView.loadUrl("file:///android_asset/threadDisplay.html")
                    WebView.setWebContentsDebuggingEnabled(true);
                    container.addView(webView)
                    val loading: LinearLayout? = findViewById(R.id.LoadingAnimation)
                    loading?.isGone = true
                }
            }

            HttpExt().retrievePage("http://www.ditiezu.com/thread-$tid-$page-1.html") {
                if (it == "Failed Retrieved") {
                    // Failed Retrieved
                    Log.i("HTTPEXT", "FAILED RETRIEVED")
                }
                processResult(it)
            }
        }

        retrieveThreadContent(1)
    }
}