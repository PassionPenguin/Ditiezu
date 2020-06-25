package com.passionpenguin.ditiezu

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.webkit.JavascriptInterface
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import com.passionpenguin.ditiezu.helper.Dialog
import com.passionpenguin.ditiezu.helper.HttpExt
import kotlinx.android.synthetic.main.activity_view_thread.*
import org.jsoup.Jsoup
import java.net.URLEncoder
import kotlin.properties.Delegates

class ViewThread : AppCompatActivity() {
    private var darkMode by Delegates.notNull<Boolean>()
    private var tid by Delegates.notNull<Int>()
    private var page by Delegates.notNull<Int>()
    private var loginState by Delegates.notNull<Boolean>()
    private var formhash by Delegates.notNull<String>()

    inner class WebViewInterface {
        @JavascriptInterface
        fun load(page: Int) {
            HttpExt().retrievePage("http://www.ditiezu.com/thread-$tid-$page-1.html") {
                runOnUiThread {
                    webView.evaluateJavascript("onPageLoaded(`$it`, $page)", null)
                }
            }
        }

        @JavascriptInterface
        fun rate(pid: Int, index: Int) {
            println("$pid - $tid")
            runOnUiThread {
                webView.evaluateJavascript("onLoading()", null)
            }
            var s =
                HttpExt().asyncRetrievePage("http://www.ditiezu.com/forum.php?mod=misc&action=rate&tid=$tid&pid=$pid&infloat=yes&handlekey=rate&t=&inajax=1&ajaxtarget=fwin_content_rate")
            runOnUiThread {
                webView.evaluateJavascript("onLoaded()", null)
            }

            when (s) {
                "Failed Retrieved" -> {
                    Dialog().tip(
                        resources.getString(R.string.failed_retrieved),
                        R.drawable.ic_baseline_close_24,
                        R.color.danger,
                        this@ViewThread,
                        ViewThread,
                        Dialog.TIME_SHORT
                    )
                }
                else -> {
                    s = s.substring(53, s.length - 10)
                    val p = Jsoup.parse(s)
                    when {
                        p.select(".alert_error").isNotEmpty() -> {
                            Dialog().tip(
                                p.select(".alert_error").text(),
                                R.drawable.ic_baseline_close_24,
                                R.color.danger,
                                this@ViewThread,
                                ViewThread,
                                Dialog.TIME_SHORT
                            )
                        }
                        else -> {
                            Dialog().create(
                                this@ViewThread,
                                ViewThread,
                                resources.getString(R.string.rate),
                                resources.getString(R.string.rate_title),
                                resources.getString(R.string.rate_description),
                                { v, _ ->
                                    if (v.findViewById<TextView>(R.id.reason).text == "") {
                                        Dialog().tip(
                                            resources.getString(R.string.require_reason),
                                            R.drawable.ic_baseline_close_24,
                                            R.color.danger,
                                            this@ViewThread,
                                            ViewThread,
                                            Dialog.TIME_SHORT
                                        )
                                    } else {
                                        webView.evaluateJavascript("onLoading()", null)
                                        val str = HttpExt().asyncPostPage(
                                            "http://www.ditiezu.com/forum.php?mod=misc&action=rate&ratesubmit=yes&infloat=yes&inajax=1",
                                            "formhash=$formhash&tid=${tid}&pid=${pid}&handlekey=rate&reason=${URLEncoder.encode(
                                                v.findViewById<EditText>(R.id.reason).text.toString(),
                                                "GBK"
                                            )}&score4=${v.findViewById<Spinner>(R.id.score).selectedItem}"
                                        )
                                        webView.evaluateJavascript("onLoaded()", null)

                                        val response = str.substring(
                                            str.indexOf("_rate('") + 33,
                                            str.indexOf(
                                                "'", str.indexOf("_rate('") + 34
                                            )
                                        )
                                        when {
                                            str == "Failed Retrieved" -> {
                                                Dialog().tip(
                                                    resources.getString(R.string.failed_retrieved),
                                                    R.drawable.ic_baseline_close_24,
                                                    R.color.danger,
                                                    this@ViewThread,
                                                    ViewThread,
                                                    Dialog.TIME_SHORT
                                                )
                                            }
                                            str.contains("succeed") -> {
                                                Dialog().tip(
                                                    response,
                                                    R.drawable.ic_baseline_check_24,
                                                    R.color.primary500,
                                                    this@ViewThread,
                                                    ViewThread,
                                                    Dialog.TIME_SHORT
                                                )
                                                webView.evaluateJavascript("onLoading()", null)
                                                webView.evaluateJavascript(
                                                    "onReloadIndex(`${HttpExt().asyncRetrievePage(
                                                        "http://www.ditiezu.com/forum.php?mod=viewthread&tid=$tid&viewpid=$pid&inajax=1&ajaxtarget=post_$pid"
                                                    )}`, $index)", null
                                                )
                                                webView.evaluateJavascript("onLoaded()", null)
                                            }
                                            str.contains("error") -> {
                                                Dialog().tip(
                                                    response,
                                                    R.drawable.ic_baseline_close_24,
                                                    R.color.danger,
                                                    this@ViewThread,
                                                    ViewThread,
                                                    Dialog.TIME_SHORT
                                                )
                                            }
                                        }
                                    }
                                }) { v, w ->
                                v.addView(
                                    LayoutInflater.from(this@ViewThread)
                                        .inflate(
                                            R.layout.fragment_rate,
                                            v,
                                            false
                                        )
                                )
                                with(v.findViewById<Spinner>(R.id.reasonList)) {
                                    ArrayAdapter.createFromResource(
                                        this@ViewThread,
                                        R.array.reason_list,
                                        android.R.layout.simple_spinner_dropdown_item
                                    ).also { adapter ->
                                        // Specify the layout to use when the list of choices appears
                                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                                        // Apply the adapter to the spinner
                                        this.adapter = adapter
                                    }
                                    this.onItemSelectedListener =
                                        object : AdapterView.OnItemSelectedListener {
                                            override fun onItemSelected(
                                                parent: AdapterView<*>?,
                                                view: View,
                                                position: Int,
                                                id: Long
                                            ) {
                                                v.findViewById<EditText>(R.id.reason)
                                                    .setText(resources.getStringArray(R.array.reason_list)[position])
                                            }

                                            override fun onNothingSelected(parent: AdapterView<*>?) {}
                                        }
                                }
                                with(v.findViewById<Spinner>(R.id.score)) {
                                    ArrayAdapter.createFromResource(
                                        this@ViewThread,
                                        R.array.popularity_score,
                                        android.R.layout.simple_spinner_dropdown_item
                                    ).also { adapter ->
                                        // Specify the layout to use when the list of choices appears
                                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                                        // Apply the adapter to the spinner
                                        this.adapter = adapter
                                    }
                                }
                                with(p.select("td:last-child")[0].text().toInt()) {
                                    val restScore =
                                        v.findViewById<TextView>(R.id.rest)
                                    restScore.text =
                                        resources.getString(
                                            R.string.rest_score,
                                            this
                                        )
                                    if (this < 3)
                                        restScore.setTextColor(
                                            resources.getColor(
                                                R.color.danger,
                                                null
                                            )
                                        )
                                }
                                w.update()
                            }
                        }
                    }
                }
            }
        }

        @JavascriptInterface
        fun reply(pid: Int) {
            val i = Intent(this@ViewThread, PostActivity::class.java)
            i.putExtra("type", "reply")
            i.putExtra("tid", tid)
            i.putExtra("pid", pid)
            i.putExtra("reppid", pid)
            i.putExtra("reppost", pid)
            startActivity(i)
        }

        @JavascriptInterface
        fun edit(pid: Int) {
            val i = Intent(this@ViewThread, PostActivity::class.java)
            i.putExtra("type", "edit")
            i.putExtra("pid", pid)
            i.putExtra("tid", tid)
            startActivity(i)
        }

        @JavascriptInterface
        fun isDarkMode(): Boolean {
            return darkMode
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
    }

    private fun retrieveThreadContent() {
        fun loadPage(threadId: Int = this.tid, pageId: Int = this.page) {
            runOnUiThread {
                webView.evaluateJavascript("onLoading()", null)
            }
            HttpExt().retrievePage("http://www.ditiezu.com/thread-$threadId-$pageId-1.html") { result ->
                runOnUiThread {
                    webView.evaluateJavascript("onLoaded()", null)
                    if (result == "Failed Retrieved") {
                        Dialog().tip(
                            resources.getString(R.string.failed_retrieved),
                            R.drawable.ic_baseline_close_24,
                            R.color.danger,
                            this@ViewThread,
                            ViewThread,
                            Dialog.TIME_SHORT
                        )
                    }
                    val parser = Jsoup.parse(result)
                    formhash = parser.select("[name=\"formhash\"]").attr("value")
                    title = parser.select("#thread_subject").text()
                    threadTitle.text = title
                    if (parser.select("#messagetext").isNotEmpty()) {
                        Dialog().tip(
                            parser.select("#messagetext").text(),
                            R.drawable.ic_baseline_close_24,
                            R.color.danger,
                            this@ViewThread,
                            ViewThread,
                            Dialog.TIME_SHORT
                        )
                    }
                    webView.evaluateJavascript("onPageLoaded(`$result`, $pageId)", null)
                }
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
                )
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
                    ViewThread,
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
                if (loginState) {
                    val i = Intent(this@ViewThread, PostActivity::class.java)
                    i.putExtra("tid", tid)
                    startActivity(i)
                } else {
                    Snackbar.make(
                        ViewThread,
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

        val extras = intent.extras
        if (extras != null) {
            this.tid = (extras.get("tid") ?: 1) as Int
            this.page = (extras.get("page") ?: 1) as Int
        } else finish()

        webView.loadUrl("file:///android_asset/webHelper/viewthread.html")
        webView.addJavascriptInterface(WebViewInterface(), "android")
        webView.settings.javaScriptEnabled = true
        WebView.setWebContentsDebuggingEnabled(true)

        webView.webViewClient = (object : WebViewClient() {
            override fun shouldOverrideUrlLoading(
                view: WebView?,
                request: WebResourceRequest?
            ): Boolean {
                val url = request?.url.toString()
                if (url.indexOf("ditiezu.com/") != -1) {
                    if (url.indexOf("mod=viewthread") != -1 || url.indexOf("thread-") != -1) {
                        // ViewThread
                        val tid = if (url.indexOf("viewthread") == -1) {
                            url.substring(
                                url.indexOf("thread-") + 7,
                                url.indexOf("-1-1")
                            ).toInt()
                        } else {
                            url.substring(
                                url.indexOf("tid=") + 4,
                                url.indexOf("&", url.indexOf("tid="))
                            ).toInt()
                        }
                        val page = if (url.indexOf("viewthread") == -1) {
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
                        val i = Intent(this@ViewThread, ViewThread::class.java)
                        i.putExtra("tid", tid)
                        i.putExtra("page", page)
                        startActivity(i)
                    }
                }
                view?.evaluateJavascript(
                    "window.open('$url')",
                    null
                )
                return true
            }
        })
        loginState = HttpExt().checkLogin()
        this.darkMode =
            this.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES

        BackButton.setOnClickListener {
            onBackPressed()
        }
        retrieveThreadContent()
    }
}