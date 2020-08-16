/*
 * ==================================================
 * =  PROJECT     地下铁的故事
 * =  MODULE      地下铁的故事.app
 * =  FILE NAME   ViewThread.kt
 * =  LAST MODIFIED BY PASSIONPENGUIN [8/14/20 1:40 AM]
 * ==================================================
 * Copyright 2020 PassionPenguin. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

@file:Suppress("BlockingMethodInNonBlockingContext")

package com.ditiezu.android

import android.annotation.SuppressLint
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.text.TextUtils
import android.view.*
import android.webkit.JavascriptInterface
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.*
import androidx.annotation.Nullable
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ditiezu.android.adapters.InviteUserAdapter
import com.ditiezu.android.adapters.User
import com.google.android.material.snackbar.Snackbar
import com.passionpenguin.Alert
import com.passionpenguin.NetUtils
import com.passionpenguin.PopupWindow
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
    private var webView by Delegates.notNull<WebView>()
    private var tips by Delegates.notNull<TextView>()
    private var tipsImage by Delegates.notNull<ImageView>()

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
                    "onPageLoaded(`${NetUtils(this@ViewThread).retrievePage("http://www.ditiezu.com/thread-$tid-$page-1.html").replace("`", "\\`")}`, $page)",
                    null
                )
            }
        }

        @JavascriptInterface
        fun rate(pid: Int, index: Int) {
            evaluate("onLoading()", null)
            GlobalScope.launch {
                var s = NetUtils(this@ViewThread).retrievePage("http://www.ditiezu.com/forum.php?mod=misc&action=rate&tid=$tid&pid=$pid&infloat=yes&handlekey=rate&t=&inajax=1&ajaxtarget=fwin_content_rate")

                runOnUiThread {
                    evaluate("onLoaded()", null)
                    if (s != "") {
                        s = s.substring(53, s.length - 10)
                        val p = Jsoup.parse(s)
                        when {
                            p.select(".alert_error").isNotEmpty() -> {
                                Alert(this@ViewThread, p.select(".alert_error").text()).error()
                            }
                            else -> {
                                object : PopupWindow(
                                    this@ViewThread,
                                    resources.getString(R.string.rate_title),
                                    resources.getString(R.string.rate_description)
                                ) {
                                    override fun onSubmit(window: android.widget.PopupWindow, root: ViewGroup) {
                                        if (root.findViewById<TextView>(R.id.reason).text == "") {
                                            Alert(this@ViewThread, resources.getString(R.string.require_reason)).error()
                                        } else {
                                            evaluate("onLoading()", null)
                                            GlobalScope.launch {
                                                val str = NetUtils(this@ViewThread).postPage(
                                                    "http://www.ditiezu.com/forum.php?mod=misc&action=rate&ratesubmit=yes&infloat=yes&inajax=1",
                                                    "formhash=$formhash&tid=${tid}&pid=${pid}&handlekey=rate" +
                                                            "&reason=${URLEncoder.encode(root.findViewById<EditText>(R.id.reason).text.toString(), "GBK")}" +
                                                            "&score4=${root.findViewById<Spinner>(R.id.score).selectedItem}"
                                                )
                                                runOnUiThread {
                                                    evaluate("onLoaded()", null)

                                                    val response = str.substring(
                                                        str.indexOf("_rate('") + 33,
                                                        str.indexOf("'", str.indexOf("_rate('") + 34)
                                                    )
                                                    when {
                                                        str == "" -> {
                                                        }
                                                        str.contains("succeed") -> {
                                                            Alert(this@ViewThread, response).success()
                                                            evaluate("onLoading()", null)
                                                            GlobalScope.launch {
                                                                val data = NetUtils(this@ViewThread).retrievePage("http://www.ditiezu.com/forum.php?mod=viewthread&tid=$tid&viewpid=$pid&inajax=1&ajaxtarget=post_$pid").replace("`", "\\`")
                                                                evaluate("onReloadIndex(`$data`, $index)", null)
                                                            }
                                                            evaluate("onLoaded()", null)
                                                        }
                                                        str.contains("error") -> {
                                                            Alert(this@ViewThread, response).error()
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }

                                    override fun initContent(window: android.widget.PopupWindow, root: ViewGroup) {
                                        root.addView(LayoutInflater.from(this@ViewThread).inflate(R.layout.fragment_rate, root, false))
                                        with(root.findViewById<Spinner>(R.id.reasonList)) {
                                            ArrayAdapter.createFromResource(this@ViewThread, R.array.reason_list, R.layout.spinner_dropdown_item).also { adapter ->
                                                // Specify the layout to use when the list of choices appears
                                                adapter.setDropDownViewResource(R.layout.spinner_dropdown_item)
                                                // Apply the adapter to the spinner
                                                this.adapter = adapter
                                            }
                                            this.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                                                override fun onItemSelected(parent: AdapterView<*>?, view: View, position: Int, id: Long) {
                                                    root.findViewById<EditText>(R.id.reason).setText(resources.getStringArray(R.array.reason_list)[position])
                                                }

                                                override fun onNothingSelected(parent: AdapterView<*>?) {}
                                            }
                                        }
                                        with(root.findViewById<Spinner>(R.id.score)) {
                                            ArrayAdapter.createFromResource(this@ViewThread, R.array.popularity_score, R.layout.spinner_dropdown_item).also { adapter ->
                                                // Specify the layout to use when the list of choices appears
                                                adapter.setDropDownViewResource(R.layout.spinner_dropdown_item)
                                                // Apply the adapter to the spinner
                                                this.adapter = adapter
                                            }
                                        }
                                        with(p.select("td:last-child")[0].text().toInt()) {
                                            val restScore = root.findViewById<TextView>(R.id.rest)
                                            restScore.text = resources.getString(R.string.rest_score, this)
                                            if (this < 3) restScore.setTextColor(resources.getColor(R.color.danger_500, null))
                                        }
                                        window.update()
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

        /*
        @JavascriptInterface
        fun toggleLogin() {
            startActivity(
                Intent(
                    this@ViewThread,
                    LoginActivity::class.java
                )
            )
        }*/

        @JavascriptInterface
        fun openUserProfile(uid: Int) {
            val i = Intent(this@ViewThread, UserProfile::class.java)
            i.putExtra("uid", uid)
            startActivity(i)
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
                    val str = NetUtils(this@ViewThread).retrievePage("http://www.ditiezu.com/home.php?mod=spacecp&ac=friend&op=getinviteuser&inajax=1&page=$page")
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
                        evaluate("onLoaded()") {}
                        if (users.isNotEmpty())
                            object : PopupWindow(
                                this@ViewThread,
                                resources.getString(R.string.invite_title),
                                resources.getString(R.string.invite_description)
                            ) {
                                override fun onSubmit(window: android.widget.PopupWindow, root: ViewGroup) {
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
                                            val s = NetUtils(this@ViewThread).postPage("http://www.ditiezu.com/misc.php?mod=invite&action=thread&id=$tid&inajax=1", "formhash=$formhash&referer=http%3A%2F%2Fwww.ditiezu.com%2Fthread-$tid-$page-1.html&handlekey=invite&invitesubmit=yes&uids=$usersKeys")
                                            if (s.contains("succeedhandle")) successfullyTimes++
                                            if (index == selectedUserGroup.size - 1) {
                                                with(Alert(this@ViewThread, resources.getString(R.string.successful_and_fail_times, successfullyTimes, selectedUserGroup.size - successfullyTimes))) {
                                                    when {
                                                        selectedUserGroup.size - successfullyTimes > 0 -> this.error()
                                                        else -> this.success()
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }

                                override fun initContent(window: android.widget.PopupWindow, root: ViewGroup) {
                                    root.addView(LayoutInflater.from(this@ViewThread).inflate(R.layout.fragment_invite, root, false))
                                    with(root.findViewById<RecyclerView>(R.id.selector)) {
                                        this.layoutManager = GridLayoutManager(this@ViewThread, 4)
                                        this.adapter = InviteUserAdapter(this@ViewThread, users)
                                    }
                                    window.update()
                                }
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
                with(NetUtils(this@ViewThread).retrievePage("http://www.ditiezu.com/thread-$threadId-$pageId-1.html").replace("`", "\\`")) {
                    runOnUiThread {
                        evaluate("onLoaded()", null)
                        val parser = Jsoup.parse(this)
                        formhash = parser.select("[name=\"formhash\"]").attr("value")
                        title = parser.select("#thread_subject").text()
                        findViewById<TextView>(R.id.threadTitle).text = title
                        if (parser.select("#messagetext").isNotEmpty()) {
                            tips.visibility = View.VISIBLE
                            tipsImage.visibility = View.VISIBLE
                            tips.text = parser.select("#messagetext").text()
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
                    findViewById<ConstraintLayout>(R.id.ViewThread),
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
//                if (loginState) {
//                    val i = Intent(this@ViewThread, Editor::class.java)
//                    i.putExtra("tid", tid)
//                    startActivity(i)
//                } else {
//                    Snackbar.make(
//                        ViewThread,
//                        resources.getString(R.string.login_description),
//                        Snackbar.LENGTH_LONG
//                    ).setAction(resources.getString(R.string.login)) {
//                        startActivity(
//                            Intent(this@ViewThread, LoginActivity::class.java)
//                        )
//                    }.show()
//                }
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_thread)
        setSupportActionBar(findViewById(R.id.action_bar_in_thread))

        webView = findViewById(R.id.webView)
        tips = findViewById(R.id.tips)
        tipsImage = findViewById(R.id.tips_image)

        val extras = intent.extras
        if (extras != null) {
            if (extras.get("tid") == null) onBackPressed() else this.tid = extras.get("tid") as Int
            this.page = (extras.get("page") ?: 1) as Int
        } else onBackPressed()

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
                            url.substring(url.indexOf("thread-") + 7, url.indexOf("-1-1")).toInt()
                        } else {
                            url.substring(url.indexOf("tid=") + 4, url.indexOf("&", url.indexOf("tid="))).toInt()
                        }
                        val page = if (url.indexOf("viewthread") == -1) {
                            url.substring(url.indexOf("-") + 1, url.indexOf("-1")).toInt()
                        } else {
                            url.substring(url.indexOf("page=") + 5, url.indexOf("&", url.indexOf("page="))).toInt()
                        }
                        this@ViewThread.tid = tid
                        this@ViewThread.page = page
                        retrieveThreadContent()
                    }
                }
                evaluate("window.open('$url')", null)
                return true
            }
        })
        GlobalScope.launch {
//            loginState = NetUtils(this@ViewThread).checkLogin(this@ViewThread)
        }
        loginState = false
        this.darkMode = this.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES

        findViewById<ImageButton>(R.id.BackButton).setOnClickListener {
            onBackPressed()
        }
        retrieveThreadContent()
    }
}