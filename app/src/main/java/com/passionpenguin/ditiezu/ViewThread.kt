@file:Suppress("BlockingMethodInNonBlockingContext")

package com.passionpenguin.ditiezu

import android.annotation.SuppressLint
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.webkit.JavascriptInterface
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.*
import androidx.annotation.Nullable
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.passionpenguin.ditiezu.helper.Dialog
import com.passionpenguin.ditiezu.helper.HttpExt
import com.passionpenguin.ditiezu.helper.InviteUserAdapter
import com.passionpenguin.ditiezu.helper.User
import kotlinx.android.synthetic.main.activity_view_thread.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.jsoup.Jsoup
import java.net.URLEncoder
import java.util.regex.Pattern
import kotlin.properties.Delegates

class ViewThread : AppCompatActivity() {
    private var darkMode by Delegates.notNull<Boolean>()
    private var tid by Delegates.notNull<Int>()
    private var page by Delegates.notNull<Int>()
    private var loginState by Delegates.notNull<Boolean>()
    private var formhash by Delegates.notNull<String>()

    private fun evaluate(code: String, @Nullable resultCallback: ((res: String) -> Unit)?) {
        runOnUiThread {
            webView.evaluateJavascript(code, resultCallback)
        }
    }

    inner class WebViewInterface {
        @JavascriptInterface
        fun load(page: Int) {
            GlobalScope.launch {
                evaluate(
                    "onPageLoaded(`${HttpExt.retrievePage("http://www.ditiezu.com/thread-$tid-$page-1.html")}`, $page)",
                    null
                )
            }
        }

        @JavascriptInterface
        fun rate(pid: Int, index: Int) {
            evaluate("onLoading()", null)
            GlobalScope.launch {
                var s = HttpExt.retrievePage("http://www.ditiezu.com/forum.php?mod=misc&action=rate&tid=$tid&pid=$pid&infloat=yes&handlekey=rate&t=&inajax=1&ajaxtarget=fwin_content_rate")

                runOnUiThread {
                    evaluate("onLoaded()", null)
                    when (s) {
                        "Failed Retrieved" -> {
                            Dialog.tip(
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
                                    Dialog.tip(
                                        p.select(".alert_error").text(),
                                        R.drawable.ic_baseline_close_24,
                                        R.color.danger,
                                        this@ViewThread,
                                        ViewThread,
                                        Dialog.TIME_SHORT
                                    )
                                }
                                else -> {
                                    Dialog.create(
                                        this@ViewThread,
                                        ViewThread,
                                        resources.getString(R.string.rate),
                                        resources.getString(R.string.rate_title),
                                        resources.getString(R.string.rate_description),
                                        { v, _ ->
                                            if (v.findViewById<TextView>(R.id.reason).text == "") {
                                                Dialog.tip(
                                                    resources.getString(R.string.require_reason),
                                                    R.drawable.ic_baseline_close_24,
                                                    R.color.danger,
                                                    this@ViewThread,
                                                    ViewThread,
                                                    Dialog.TIME_SHORT
                                                )
                                            } else {
                                                evaluate("onLoading()", null)
                                                GlobalScope.launch {
                                                    val str = HttpExt.postPage(
                                                        "http://www.ditiezu.com/forum.php?mod=misc&action=rate&ratesubmit=yes&infloat=yes&inajax=1",
                                                        "formhash=$formhash&tid=${tid}&pid=${pid}&handlekey=rate" +
                                                                "&reason=${URLEncoder.encode(v.findViewById<EditText>(R.id.reason).text.toString(), "GBK")}" +
                                                                "&score4=${v.findViewById<Spinner>(R.id.score).selectedItem}"
                                                    )
                                                    runOnUiThread {
                                                        evaluate("onLoaded()", null)

                                                        val response = str.substring(
                                                            str.indexOf("_rate('") + 33,
                                                            str.indexOf("'", str.indexOf("_rate('") + 34)
                                                        )
                                                        when {
                                                            str == "Failed Retrieved" -> {
                                                                Dialog.tip(
                                                                    resources.getString(R.string.failed_retrieved),
                                                                    R.drawable.ic_baseline_close_24,
                                                                    R.color.danger,
                                                                    this@ViewThread,
                                                                    ViewThread,
                                                                    Dialog.TIME_SHORT
                                                                )
                                                            }
                                                            str.contains("succeed") -> {
                                                                Dialog.tip(
                                                                    response,
                                                                    R.drawable.ic_baseline_check_24,
                                                                    R.color.success,
                                                                    this@ViewThread,
                                                                    ViewThread,
                                                                    Dialog.TIME_SHORT
                                                                )
                                                                evaluate("onLoading()", null)
                                                                GlobalScope.launch {
                                                                    val data = HttpExt.retrievePage("http://www.ditiezu.com/forum.php?mod=viewthread&tid=$tid&viewpid=$pid&inajax=1&ajaxtarget=post_$pid")
                                                                    evaluate("onReloadIndex(`$data`, $index)", null)
                                                                }
                                                                evaluate("onLoaded()", null)
                                                            }
                                                            str.contains("error") -> {
                                                                Dialog.tip(
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
                                                R.layout.spinner_dropdown_item
                                            ).also { adapter ->
                                                // Specify the layout to use when the list of choices appears
                                                adapter.setDropDownViewResource(R.layout.spinner_dropdown_item)
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
                                                R.layout.spinner_dropdown_item
                                            ).also { adapter ->
                                                // Specify the layout to use when the list of choices appears
                                                adapter.setDropDownViewResource(R.layout.spinner_dropdown_item)
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
            }
        }

        @JavascriptInterface
        fun reply(pid: Int) {
            val i = Intent(this@ViewThread, Editor::class.java)
            i.putExtra("type", "reply")
            i.putExtra("tid", tid)
            i.putExtra("pid", pid)
            i.putExtra("reppid", pid)
            i.putExtra("reppost", pid)
            startActivity(i)
        }

        @JavascriptInterface
        fun edit(pid: Int) {
            val i = Intent(this@ViewThread, Editor::class.java)
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

        @JavascriptInterface
        fun invite() {
            evaluate("onLoading()") {}
            GlobalScope.launch {
                val users = mutableListOf<User>()
                var page = 0
                val p = Pattern.compile("\"avatar\":\"(.+?)\",\"uid\":(.+?),\"username\":\"(.+?)\"")
                while (true) {
                    page++
                    val str = HttpExt.retrievePage("http://www.ditiezu.com/home.php?mod=spacecp&ac=friend&op=getinviteuser&inajax=1&page=$page")
                    evaluate("var data = ${str.substring(53, str.length - 10)}; Object.keys(data.userdata).map(i=>data.userdata[i])") {
                        var result = it
                        var m = p.matcher(result)
                        while (m.find()) {
                            users.add(User(m.group(2).toInt(), m.group(3).toString(), m.group(1).toString()))
                            result = result.substring(m.start() + 1) // ignore the just-matched and move on;
                            m = p.matcher(result)
                        }
                    }
                    if (str.contains("'singlenum':'0'")) {
                        if (users.isNotEmpty())
                            Dialog.create(
                                this@ViewThread,
                                ViewThread,
                                resources.getString(R.string.invite),
                                resources.getString(R.string.invite_title),
                                resources.getString(R.string.invite_description),
                                { _, _ ->
                                    val selectedUser = users.filter { i -> i.isChecked }
                                    val selectedUserGroup = arrayListOf<ArrayList<Int>>()
                                    var data = arrayListOf<Int>()
                                    for ((index, i) in selectedUser.withIndex()) {
                                        if (index % 20 == 0 && index != 0 && index != selectedUser.size - 1) {
                                            selectedUserGroup.add(data)
                                            data = arrayListOf()
                                        }
                                        data.add(i.uid)
                                    }
                                    selectedUserGroup.add(data)
                                    var successfullyTimes = 0
                                    selectedUserGroup.forEachIndexed { index, it ->
                                        GlobalScope.launch {
                                            val usersKeys = TextUtils.join("&uids%5B%5D=", it)
                                            val s = HttpExt.postPage("http://www.ditiezu.com/misc.php?mod=invite&action=thread&id=$tid&inajax=1", "formhash=$formhash&referer=http%3A%2F%2Fwww.ditiezu.com%2Fthread-$tid-$page-1.html&handlekey=invite&invitesubmit=yes&uids=$usersKeys")
                                            if (s.contains("succeedhandle")) successfullyTimes++
                                            if (index == selectedUserGroup.size - 1) {
                                                Dialog.tip(resources.getString(R.string.successful_and_fail_times, successfullyTimes, selectedUserGroup.size - successfullyTimes),
                                                    if (selectedUserGroup.size - successfullyTimes > 0) R.drawable.ic_baseline_close_24 else R.drawable.ic_baseline_check_24,
                                                    if (selectedUserGroup.size - successfullyTimes > 0) R.color.danger else R.color.success,
                                                    this@ViewThread, ViewThread, Dialog.TIME_LONG)
                                            }
                                        }
                                    }
                                    evaluate("onLoaded()") {}
                                }) { v, w ->
                                v.addView(LayoutInflater.from(this@ViewThread).inflate(R.layout.fragment_invite, v, false))
                                with(v.findViewById<RecyclerView>(R.id.selector)) {
                                    this.layoutManager = GridLayoutManager(this@ViewThread, 4)
                                    this.adapter = InviteUserAdapter(this@ViewThread, users)
                                }
                                w.update()
                            }
                        break
                    }
                }
                evaluate("delete data") {}
            }

            evaluate("onLoaded") {}
        }
    }

    private fun retrieveThreadContent() {
        fun loadPage(threadId: Int = this.tid, pageId: Int = this.page) {
            evaluate("onLoading()", null)
            GlobalScope.launch {
                with(HttpExt.retrievePage("http://www.ditiezu.com/thread-$threadId-$pageId-1.html")) {
                    runOnUiThread {
                        evaluate("onLoaded()", null)
                        if (this == "Failed Retrieved") {
                            Dialog.tip(
                                resources.getString(R.string.failed_retrieved),
                                R.drawable.ic_baseline_close_24,
                                R.color.danger,
                                this@ViewThread,
                                ViewThread,
                                Dialog.TIME_SHORT
                            )
                        }
                        val parser = Jsoup.parse(this)
                        formhash = parser.select("[name=\"formhash\"]").attr("value")
                        title = parser.select("#thread_subject").text()
                        threadTitle.text = title
                        if (parser.select("#messagetext").isNotEmpty()) {
                            Dialog.tip(
                                parser.select("#messagetext").text(),
                                R.drawable.ic_baseline_close_24,
                                R.color.danger,
                                this@ViewThread,
                                ViewThread,
                                Dialog.TIME_SHORT
                            )
                        }
                        println(pageId)
                        evaluate("onPageLoaded(`$this`, $pageId)", null)
                    }
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
                    val i = Intent(this@ViewThread, Editor::class.java)
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

    @SuppressLint("SetJavaScriptEnabled")
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
                evaluate(
                    "window.open('$url')",
                    null
                )
                return true
            }
        })
        GlobalScope.launch {
            loginState = HttpExt.checkLogin(this@ViewThread)
        }
        this.darkMode =
            this.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES

        BackButton.setOnClickListener {
            onBackPressed()
        }
        retrieveThreadContent()
    }
}