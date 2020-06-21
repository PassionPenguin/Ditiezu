package com.passionpenguin.ditiezu

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Typeface
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ImageSpan
import android.text.style.StyleSpan
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.scale
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.snackbar.Snackbar
import com.passionpenguin.ditiezu.helper.*
import kotlinx.android.synthetic.main.activity_view_thread.*
import org.jsoup.Jsoup
import org.jsoup.nodes.Element
import java.util.*
import java.util.regex.Pattern
import kotlin.properties.Delegates

class ViewThread : AppCompatActivity() {
    private var darkMode by Delegates.notNull<Boolean>()
    private var tid by Delegates.notNull<Int>()
    private var page by Delegates.notNull<Int>()
    private var loginState by Delegates.notNull<Boolean>()
    private var t: Long = 0
    private lateinit var adapter: ReplyItemAdapter

    private fun retrieveThreadContent() {
        t = Calendar.getInstance().time.time
        runOnUiThread {
            LoadingMaskContainer.visibility = View.VISIBLE
        }
        fun loadPage(threadId: Int = this.tid, pageId: Int = this.page) {
            HttpExt().retrievePage("http://www.ditiezu.com/thread-$threadId-$pageId-1.html") { result ->
                val parser = Jsoup.parse(result)
                runOnUiThread {
                    LoadingMaskContainer.visibility = View.GONE
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
                    if (parser.select("#messagetext").isNotEmpty()) {
                        Dialog().tip(
                            parser.select("#messagetext").text(),
                            R.drawable.ic_baseline_close_24,
                            R.color.danger,
                            this@ViewThread,
                            ViewThread,
                            Dialog.TIME_SHORT
                        )
                        LoadingMaskContainer.visibility = View.GONE
                    }
                }

                val list = mutableListOf<ReplyItem>()

                parser.select("table[id^='pid']").forEachIndexed { index: Int, it: Element ->
                    var withPopularity = false
                    var withMoney = false
                    var withPrestige = false
                    var participantsNum = ""
                    val rateList = mutableListOf<RateItem>()
//                    var rateContent: String = ""
                    it.select(".tip, a").forEach { tipEl ->
                        if (tipEl.tagName() != "a" || tipEl.attr("href").contains("redirect"))
                            tipEl.remove()
                    }
                    it.select(".t_fsz a").forEach { tipEl ->
                        tipEl.text("查看链接")
                        tipEl.attr("style", "color: #289c77")
                    }
                    it.select("ignore_js_op").forEach { el ->
                        el.tagName("img")
                        el.attr("src", el.select("[id^='a_img']").attr("file"))
                    }
                    it.select("img").forEach { img ->
                        if (img.attr("src").contains("none.gif"))
                            img.attr(
                                "src",
                                img.attr("file")
                            )
                    }
                    it.html(
                        Pattern.compile(
                            "<img src=\"static/image/(\\S+?)\" smilieid=\"\\S+?\" border=\"0\" alt=\"\">",
                            Pattern.MULTILINE
                        ).matcher(it.html()).replaceAll("[emotion]$1[/emotion]")
                    )
                    it.select("img[smilieid]").forEach { img ->
                        img.tagName("span")
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
                    with(it.select(".pstatus")) {
                        this.tagName("PSTATUS")
                    }

                    if (it.select("[id^='ratelog_']").isNotEmpty()) {
                        val log = it.select("[id^='ratelog_']")
                        with(log.select(".ratl tbody:first-child")) {
                            if (this.text().contains("人气")) withPopularity = true
                            if (this.text().contains("金钱")) withMoney = true
                            if (this.text().contains("威望")) withPrestige = true
                            participantsNum = this.select("th:first-child .xi1").text()
                        }
                        it.select(".ratl_l [id^='rate_']").forEach { el ->
                            val index = arrayOf(
                                if (withPopularity) {
                                    if (withMoney) {
                                        if (withPrestige) 2
                                        else 1
                                    } else if (withPrestige) 1
                                    else 0
                                } else -1,
                                if (withMoney) {
                                    if (withPrestige) 1
                                    else 0
                                } else -1,
                                if (withPrestige) 0 else -1
                            )
                            rateList.add(
                                RateItem(
                                    with(el.select("a:nth-child(2)").attr("href")) {
                                        if (this.indexOf("uid-") + 4 <= 0 || this.indexOf(".html") <= 0)
                                            0
                                        else this.substring(
                                            this.indexOf("uid-") + 4,
                                            this.indexOf(".html")
                                        ).toInt()
                                    },
                                    el.select("a:nth-child(2)").text(),
                                    if (index[0] != -1) el.select(".xg1, .xi1")[index[0]].text() else "",
                                    if (index[1] != -1) el.select(".xg1, .xi1")[index[1]].text() else "",
                                    if (index[2] != -1) el.select(".xg1, .xi1")[index[2]].text() else "",
                                    el.select(".xg1").text()
                                )
                            )
                        }
//                        rateContent =
//                            log.select(".ratc").html()
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
                            { v: LinearLayout, _i: Int ->
                                v.removeAllViews()
                                val p = parser.select("[id^='pid']")[_i]
                                p.select("[id^='postmessage_']")[0].html(
                                    p.select("[id^='postmessage_']")[0].html()
                                        .replace("&nbsp;", "\n").replace("<br>", "\n")
                                        .replace("[\r\n]+", "\n").trim()
                                )
                                p.select("[id^='postmessage_']")[0].children().forEach { n ->
                                    if (n.tagName() == "br" && n.nextElementSibling() != null && n.nextElementSibling()
                                            .tagName() == "br"
                                    ) n.remove()
                                }
                                p.select("[id^='postmessage_'], .pattl").forEach { data ->
                                    data.childNodes().forEach { n ->
                                        when (n.nodeName()) {
                                            "img" -> {
                                                when {
                                                    (n.attr("src").isNotEmpty()) -> {
                                                        val i = ImageView(this)
                                                        i.adjustViewBounds = true
                                                        i.setPadding(0, 0, 0, 0)
                                                        i.layoutParams = ViewGroup.LayoutParams(
                                                            ViewGroup.LayoutParams.WRAP_CONTENT,
                                                            ViewGroup.LayoutParams.WRAP_CONTENT
                                                        )
                                                        Glide.with(this)
                                                            .load(n.attr("src"))
                                                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                                                            .apply(
                                                                RequestOptions.bitmapTransform(
                                                                    RoundedCorners(8)
                                                                )
                                                            ).fitCenter()
                                                            .into(i)
                                                        v.addView(i)
                                                    }
                                                    ((n as Element).select("[id^='aimg_']")
                                                        .isNotEmpty()
                                                            ) -> {
                                                        val i = ImageView(this)
                                                        i.adjustViewBounds = true
                                                        i.setPadding(0, 0, 0, 0)
                                                        i.layoutParams = ViewGroup.LayoutParams(
                                                            ViewGroup.LayoutParams.WRAP_CONTENT,
                                                            ViewGroup.LayoutParams.WRAP_CONTENT
                                                        )
                                                        Glide.with(this)
                                                            .load(
                                                                n.select("[id^='aimg_']")[0].attr(
                                                                    "src"
                                                                )
                                                            )
                                                            .fitCenter()
                                                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                                                            .apply(
                                                                RequestOptions.bitmapTransform(
                                                                    RoundedCorners(8)
                                                                )
                                                            ).into(i)
                                                        v.addView(i)
                                                    }
                                                    (n.select("[id^='attach_']")
                                                        .isNotEmpty()
                                                            ) -> {
                                                        // TODO: ATTACH VIEW
                                                    }
                                                }
                                            }
                                            "#text" -> {
                                                val t = TextView(this)
                                                t.layoutParams = ViewGroup.LayoutParams(
                                                    ViewGroup.LayoutParams.WRAP_CONTENT,
                                                    ViewGroup.LayoutParams.WRAP_CONTENT
                                                )
                                                t.textSize = 16f
                                                t.setLineSpacing(0f, 1.5f)
                                                t.setPadding(0, 6, 0, 6)
                                                val s = SpannableString(n.outerHtml())
                                                val emotionMatcher =
                                                    Pattern.compile("\\[emotion](\\S+?)\\[/emotion]")
                                                        .matcher(n.outerHtml())

                                                fun bitmapFromUrl(url: String): Bitmap {
                                                    var bitmap = try {
                                                        BitmapFactory.decodeStream(assets.open(url))
                                                    } catch (e: Exception) {
                                                        println(e)
                                                        BitmapFactory.decodeStream(assets.open("smiley/xiaobai/1.gif"))
                                                    }
                                                    val height = bitmap.height
                                                    val width = bitmap.width
                                                    val size =
                                                        resources.getDimension(R.dimen._32)
                                                    bitmap = if (height > width) bitmap.scale(
                                                        (size * width / height).toInt(),
                                                        size.toInt()
                                                    )
                                                    else bitmap.scale(
                                                        size.toInt(),
                                                        (size * height / width).toInt()
                                                    )
                                                    return bitmap
                                                }
                                                while (emotionMatcher.find()) {
                                                    s.setSpan(
                                                        ImageSpan(
                                                            this,
                                                            bitmapFromUrl(emotionMatcher.group(1))
                                                        ),
                                                        emotionMatcher.start(),
                                                        emotionMatcher.end(),
                                                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                                                    )
                                                }
                                                s.setSpan(
                                                    StyleSpan(Typeface.BOLD),
                                                    0,
                                                    n.outerHtml().length,
                                                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                                                )
                                                t.text = s
                                                v.addView(t)
                                            }
                                        }
                                    }
                                }
                            },
                            it.select(".authi .xw1").text(),
                            hashMapOf(
                                Pair(
                                    "posttime",
                                    it.select("[id^='authorposton']").text().substring(4)
                                ),
                                Pair("floor", "${(page - 1) * 15 + index + 1} 层")
                            ),
                            it.select(".editp").isNotEmpty() && loginState,
                            it.select(".fastre").isNotEmpty() && loginState,
                            it.select("[onclick^=\"showWindow('rate'\"]")
                                .isNotEmpty() && loginState,
                            it.attr("id").substring(3).toInt(),
                            tid,
                            rateList,
                            withPopularity,
                            withMoney,
                            withPrestige,
                            participantsNum
//                            , rateContent
                        )
                    )
                }

                this@ViewThread.runOnUiThread {
                    title = parser.select("#thread_subject").text()
                    threadTitle.text = parser.select("#thread_subject").text()
                    adapter = ReplyItemAdapter(
                        this,
                        list,
                        parser.select("[name=\"formhash\"]").attr("value"),
                        curPage = page,
                        lastPage = if (!parser.select(".last").isEmpty())
                            parser.select(".last")[0].text().substring(4).toInt()
                        else if (!parser.select("#pgt .pg a:not(.nxt)")
                                .isEmpty()
                        ) parser.select("#pgt .pg a:not(.nxt)")
                            .last().text().toInt() else 1
                    ) { p ->
                        this.page = p
                        loadPage(tid, page)
                    }
                    viewThread.adapter = adapter
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

        val extras = intent.extras
        if (extras != null) {
            this.tid = (extras.get("tid") ?: 1) as Int
            this.page = (extras.get("page") ?: 1) as Int
        } else finish()

        loginState = HttpExt().checkLogin()
        adapter = ReplyItemAdapter(this, listOf(), "", 0, 0) {}

        this.darkMode =
            this.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES

        viewThread.layoutManager = LinearLayoutManager(this)

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