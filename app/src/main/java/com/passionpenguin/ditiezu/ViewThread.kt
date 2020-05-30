package com.passionpenguin.ditiezu

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.webkit.JavascriptInterface
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.postDelayed
import com.google.android.material.snackbar.Snackbar
import com.passionpenguin.ditiezu.helper.Animation
import com.passionpenguin.ditiezu.helper.HttpExt
import kotlinx.android.synthetic.main.activity_view_thread.*
import org.jsoup.Jsoup
import kotlin.properties.Delegates

class ViewThread : AppCompatActivity() {
    private var darkMode by Delegates.notNull<Boolean>()
    private var tid by Delegates.notNull<Int>()
    private var page by Delegates.notNull<Int>()
    private var loginState by Delegates.notNull<Boolean>()

    private fun retrieveThreadContent() {
        fun loadPage(threadId: Int = this.tid, pageId: Int = this.page) {
            HttpExt().retrievePage("http://www.ditiezu.com/thread-$threadId-$pageId-1.html") { result ->
                if (result == "Failed Retrieved") {
                    // Failed Retrieved
                    Log.i("HTTPEXT", "FAILED RETRIEVED")
                }
                fun processResult(result: String) {
                    val parser = Jsoup.parse(result)
                    this@ViewThread.runOnUiThread {
                        title = parser.select("#thread_subject").text()
                        viewThread.settings.javaScriptEnabled = true
                        class WebViewInterface {
                            @JavascriptInterface
                            fun loadPageWithPage(page: Int): String {
                                return HttpExt()
                                    .asyncRetrievePage("http://www.ditiezu.com/thread-$tid-$page-1.html")
                            }

                            @JavascriptInterface
                            fun displayWebView() {
                                runOnUiThread {
                                    viewThread.visibility = View.VISIBLE
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

                            @JavascriptInterface
                            fun toggleLogin() {
                                startActivity(
                                    Intent(
                                        this@ViewThread,
                                        LoginActivity::class.java
                                    )
                                )
                            }

                            @JavascriptInterface
                            fun checkLogin(): Boolean {
                                return HttpExt()
                                    .checkLogin()
                            }
                        }
                        viewThread.addJavascriptInterface(WebViewInterface(), "android")
                        viewThread.loadUrl("file:///android_asset/threadDisplay.html")
                        WebView.setWebContentsDebuggingEnabled(true)
                        viewThread.webViewClient = (object : WebViewClient() {
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
                        LoadingMaskContainer.visibility = View.VISIBLE
                        LoadingAnimation.startAnimation(
                            Animation().fadeOutAnimation()
                        )
                        LoadingMaskContainer.postDelayed(400) {
                            LoadingMaskContainer.visibility = View.GONE
                        }
                        threadTitle.text = parser.select("#thread_subject").text()
                    }
                }
                processResult(result)
            }
        }
        loadPage(tid, page)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.thread, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle item selection
        return when (item.itemId) {
            R.id.share -> {
                val shareIntent = Intent()
                shareIntent.action = Intent.ACTION_SEND
                shareIntent.type = "text/plain"
                shareIntent.putExtra(
                    Intent.EXTRA_TEXT,
                    "http://www.ditiezu.com/forum.php?mod=viewthread&tid=$tid&page=$page"
                );
                startActivity(Intent.createChooser(shareIntent, "Choose an app"))
                true
            }
            R.id.link -> {
                val clipboard =
                    applicationContext.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                val clip = ClipData.newPlainText(
                    "Thread URL",
                    "http://www.ditiezu.com/forum.php?mod=viewthread&tid=$tid&page=$page"
                )
                clipboard.setPrimaryClip(clip)
                Snackbar.make(
                    viewThread,
                    resources.getString(R.string.copied),
                    Snackbar.LENGTH_SHORT
                ).show()
                true
            }
            R.id.reload -> {
                retrieveThreadContent()
                true
            }
            R.id.reply -> {
                if (loginState) {/* TODO: Updated Post Thread */
                } else {
                    Snackbar.make(
                        viewThread,
                        resources.getString(R.string.login_description),
                        Snackbar.LENGTH_LONG
                    ).setAction(resources.getString(R.string.login)) {
                        startActivity(
                            Intent(this@ViewThread, LoginActivity::class.java)
                        )
                    }.show()
                }
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_thread)
        setSupportActionBar(action_bar_in_thread)

        this.darkMode =
            this.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES

        val extras = intent.extras
        if (extras != null) {
            this.tid = extras.getInt("tid", 1)
            this.page = extras.getInt("page", 1)
        } else finish()

        loginState = HttpExt().checkLogin()

        BackButton.setOnClickListener {
            onBackPressed()
        }
        retrieveThreadContent()
    }
}