package com.passionpenguin.ditiezu

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import com.passionpenguin.ditiezu.helper.HttpExt
import com.passionpenguin.ditiezu.helper.ReplyItem
import com.passionpenguin.ditiezu.helper.ReplyItemAdapter
import kotlinx.android.synthetic.main.activity_view_thread.*
import org.jsoup.Jsoup
import java.util.*
import kotlin.properties.Delegates

class ViewThread : AppCompatActivity() {
    private var darkMode by Delegates.notNull<Boolean>()
    private var tid by Delegates.notNull<Int>()
    private var page by Delegates.notNull<Int>()
    private var loginState by Delegates.notNull<Boolean>()
    private var t: Long = 0

    private fun retrieveThreadContent() {
        t = Calendar.getInstance().time.time
        runOnUiThread {
            LoadingMaskContainer.visibility = View.VISIBLE
        }
        fun loadPage(threadId: Int = this.tid, pageId: Int = this.page) {
            HttpExt().retrievePage("http://www.ditiezu.com/thread-$threadId-$pageId-1.html") { result ->
                val parser = Jsoup.parse(result)
                runOnUiThread {
                    if (result == "Failed Retrieved") {
                        tips.removeAllViews()
                        val v = LayoutInflater.from(applicationContext)
                            .inflate(R.layout.tip_access_denied, tips, false)
                        v.findViewById<TextView>(R.id.text).text =
                            resources.getString(R.string.failed_retrieved)
                        tips.addView(v)
                        LoadingMaskContainer.visibility = View.GONE
                    }
                    if (parser.select("#messagetext").isNotEmpty()) {
                        tips.removeAllViews()
                        val v = LayoutInflater.from(applicationContext)
                            .inflate(R.layout.tip_access_denied, tips, false)
                        v.findViewById<TextView>(R.id.text).text =
                            parser.select("#messagetext").text()
                        Log.i("", parser.select("#messagetext").text())
                        tips.addView(v)
                        LoadingMaskContainer.visibility = View.GONE
                    }
                }
                val list = mutableListOf<ReplyItem>()
                parser.select("table[id^='pid']").forEach {
                    it.select(".tip,a").forEach { tipEl ->
                        if (tipEl.tagName() != "a" || tipEl.attr("href").contains("redirect"))
                            tipEl.remove()
                    }
                    it.select("[id^='postmessage_'] a").forEach { tipEl ->
                        tipEl.text("查看链接")
                        tipEl.attr("style", "color: #289c77")
                    }
                    it.select("img[id^='aimg_']").forEach { img ->
                        img.attr("src", img.attr("file"))
                    }
                    it.select("img[smilieid]").forEach { img ->
                        img.attr("src", "http://www.ditiezu.com/" + img.attr("src"))
                    }
                    it.select("font[size]").forEach { size ->
                        when (size.attr("size").toInt()) {
                            1 -> size.html("<small><small>" + size.html() + "</small></small>")
                            2 -> size.html("<small>" + size.html() + "</small>")
                            4 -> size.html("<big>" + size.html() + "</big>")
                            5 -> size.html("<big><big>" + size.html() + "</big></big>")
                            6 -> size.html("<big><big><big>" + size.html() + "</big></big></big>")
                            7 -> size.html("<big><big><big><big>" + size.html() + "</big></big></big></big>")
                        }
                    }
                    it.select("blockquote").forEach { blockQuote ->
                        blockQuote.tagName("font")
                        blockQuote.attr("color", "#88888822")
                        blockQuote.attr("face", "monospaced")
                    }
                    list.add(
                        ReplyItem(
                            with(it.select(".avatar a").attr("href")) {
                                if (this.indexOf("uid-") + 4 <= 0 || this.indexOf(".html") <= 0)
                                    0
                                else this.substring(
                                    this.indexOf("uid-") + 4,
                                    this.indexOf(".html")
                                ).toInt()
                            },
                            it.select("[id^='postmessage_']").html(),
                            it.select(".authi .xw1").text(),
                            it.select("[id^='authorposton']").text(),
                            it.select(".fastre").isEmpty(),
                            it.select(".editp").isEmpty(),
                            it.attr("id").substring(3).toInt(),
                            tid
                        )
                    )
                }
                val footerPagination =
                    layoutInflater.inflate(R.layout.item_category_pagination_navigation, null)
                val lastPage =
                    if (!parser.select(".last").isEmpty())
                        parser.select(".last")[0].text().substring(4).toInt()
                    else if (!parser.select("#pgt .pg a:not(.nxt)").isEmpty()) parser.select("#pgt .pg a:not(.nxt)")
                        .last().text().toInt() else 1

                footerPagination.findViewById<TextView>(R.id.curPage).text = page.toString()

                val firstPageView = footerPagination.findViewById<ImageButton>(R.id.firstPage)
                if (page == 1) firstPageView.visibility = View.GONE
                else firstPageView.setOnClickListener {
                    this.page = 1
                    loadPage(tid, page)
                }

                val lastPageView = footerPagination.findViewById<ImageButton>(R.id.lastPage)
                if (page >= lastPage) lastPageView.visibility = View.GONE
                else lastPageView.setOnClickListener {
                    this.page = lastPage
                    loadPage(tid, page)
                }

                val prevPage = footerPagination.findViewById<ImageButton>(R.id.prevPage)
                val prevPage1 = footerPagination.findViewById<TextView>(R.id.prevPage1)
                if (page - 1 < 1) {
                    prevPage.visibility = View.GONE
                    prevPage1.visibility = View.GONE
                } else {
                    prevPage.setOnClickListener {
                        loadPage(tid, --page)
                    }
                    prevPage1.setOnClickListener {
                        loadPage(tid, --page)
                    }
                    prevPage1.text = (page - 1).toString()
                }

                val prevPage2 = footerPagination.findViewById<TextView>(R.id.prevPage2)
                if (page - 2 < 1) prevPage2.visibility = View.GONE
                else prevPage2.setOnClickListener {
                    this.page = page - 2
                    loadPage(tid, page)
                }
                prevPage2.text = (page - 2).toString()

                val nextPage = footerPagination.findViewById<ImageButton>(R.id.nextPage)
                val nextPage1 = footerPagination.findViewById<TextView>(R.id.nextPage1)
                if (page + 1 > lastPage) {
                    nextPage.visibility = View.GONE
                    nextPage1.visibility = View.GONE
                } else {
                    nextPage.setOnClickListener {
                        loadPage(tid, ++page)
                    }
                    nextPage1.setOnClickListener {
                        loadPage(tid, ++page)
                    }
                    nextPage1.text = (page + 1).toString()
                }

                val nextPage2 = footerPagination.findViewById<TextView>(R.id.nextPage2)
                if (page + 2 > lastPage) nextPage2.visibility = View.GONE
                else nextPage2.setOnClickListener {
                    this.page = page + 2
                    loadPage(tid, page)
                }
                nextPage2.text = (page + 2).toString()
                this@ViewThread.runOnUiThread {
                    title = parser.select("#thread_subject").text()
                    threadTitle.text = parser.select("#thread_subject").text()
                    viewThread.removeFooterView(viewThread.findViewById(R.id.paginationNavigation))
                    viewThread.adapter = ReplyItemAdapter(applicationContext, 0, list)
                    viewThread.addFooterView(footerPagination)
                    LoadingMaskContainer.visibility = View.GONE
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
                if (loginState) {
                    val i = Intent(this@ViewThread, PostActivity::class.java)
                    i.putExtra("tid", tid)
                    startActivity(i)
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
            this.tid = (extras.get("tid") ?: 1) as Int
            this.page = (extras.get("page") ?: 1) as Int
        } else finish()

        loginState = HttpExt().checkLogin()

        BackButton.setOnClickListener {
            onBackPressed()
        }
        retrieveThreadContent()
    }

    override fun onResume() {
        super.onResume()
        if (intent.extras?.get("reload") == true)
            retrieveThreadContent()
    }
}