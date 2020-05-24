package com.passionpenguin.ditiezu

import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import android.view.*
import android.webkit.JavascriptInterface
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
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

        fun retrieveThreadContent(page: Int = 1) {

            fun loadPage(threadId: Int = tid, pageId: Int = page) {
                HttpExt().retrievePage("http://www.ditiezu.com/thread-$threadId-$pageId-1.html") { result ->
                    if (result == "Failed Retrieved") {
                        // Failed Retrieved
                        Log.i("HTTPEXT", "FAILED RETRIEVED")
                    }
                    fun processResult(result: String) {
                        val parser = Jsoup.parse(result)
                        this@ViewThread.runOnUiThread {
                            title = parser.select("#thread_subject").text()
                            val webView: WebView = findViewById(R.id.viewThread)
                            webView.settings.javaScriptEnabled = true
                            class WebViewInterface() {
                                @JavascriptInterface
                                fun loadPageWithPage(page: Int): String {
                                    return HttpExt().asyncRetrievePage("http://www.ditiezu.com/thread-$tid-$page-1.html")
                                }

                                @JavascriptInterface
                                fun displayWebView() {
                                    runOnUiThread {
                                        webView.visibility = View.VISIBLE
                                    }
                                }

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
                            webView.webViewClient = (object : WebViewClient() {
                                override fun shouldOverrideUrlLoading(
                                    view: WebView?,
                                    request: WebResourceRequest?
                                ): Boolean {
                                    val url = request?.url.toString()
                                    if (url.indexOf("ditiezu.com/") != -1) {
                                        if (url.indexOf("mod=viewthread") != -1 || url.indexOf("thread-") != -1) {
                                            // ViewThread
                                            loadPage(
                                                if (url.indexOf("viewthread") == -1) {
                                                    url.substring(
                                                        url.indexOf("thread-") + 7,
                                                        url.indexOf("-1-1")
                                                    ).toInt()
                                                } else {
                                                    url.substring(
                                                        url.indexOf("tid=") + 4,
                                                        url.indexOf("&", url.indexOf("tid="))
                                                    ).toInt()
                                                }, if (url.indexOf("viewthread") == -1) {
                                                    url.substring(
                                                        url.indexOf("-") + 1,
                                                        url.indexOf("-1")
                                                    ).toInt()
                                                } else {
                                                    url.substring(
                                                        url.indexOf("page=") + 5,
                                                        url.indexOf("&", url.indexOf("page="))
                                                    ).toInt()
                                                }
                                            )
                                        }
                                    }
                                    view?.evaluateJavascript(
                                        "window.open('$url')",
                                        null
                                    )
                                    return true
                                }
                            })
                            val loading: LinearLayout? = findViewById(R.id.LoadingAnimation)
                            loading?.isGone = true
                        }
                    }
                    processResult(result)
                }
            }
            loadPage(tid, page)
        }

        retrieveThreadContent(1)
    }
}