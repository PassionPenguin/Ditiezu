package com.passionpenguin.ditiezu

import android.content.Context
import android.content.Intent
import android.graphics.Rect
import android.os.Bundle
import android.view.KeyEvent
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ListView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.passionpenguin.ditiezu.helper.Dialog
import com.passionpenguin.ditiezu.helper.HttpExt
import com.passionpenguin.ditiezu.helper.ThreadItem
import com.passionpenguin.ditiezu.helper.ThreadItemListAdapter
import kotlinx.android.synthetic.main.activity_search_result.*
import org.jsoup.Jsoup
import java.net.URLEncoder

class SearchResultActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_result)
        findViewById<TextView>(R.id.title).text = resources.getString(R.string.search)

        val i = intent.extras
        if (i?.getString("kw", null) == null) finish()
        var kw = i?.getString("kw", "霜羽")

        val formhash =
            Jsoup.parse(HttpExt().asyncRetrievePage("http://www.ditiezu.com/search.php?mod=forum"))
                .select("[name=\"formhash\"]").attr("value")

        fun processResult(result: String) {
            val parser = Jsoup.parse(result)
            val threadListContent = mutableListOf<ThreadItem>()
            parser.select("#threadlist .pbw").forEach {
                val title = it.select("h3 a")
                val author = it.select("p")[2].select("span a")[0]
                var targetTid = ""
                with(title.attr("href")) {
                    try {
                        targetTid = if (this.contains("highlight"))
                            this.substring(
                                this.indexOf("tid=") + 4,
                                this.indexOf("&", this.indexOf("tid=") + 4)
                            )
                        else
                            this.substring(this.indexOf("tid=") + 4)
                    } catch (ignored: Exception) {
                    }
                }
                threadListContent.add(
                    ThreadItem(
                        author.attr("href").substring(
                            author.attr("href").indexOf("uid-") + 4,
                            author.attr("href").indexOf(".html")
                        ).toInt(),
                        title.text().trim(),
                        it.select("p:nth-child(3)").text().trim(),
                        author.text(),
                        it.select("p:nth-child(4) span:first-child").text(),
                        "[" + it.select("p:nth-child(4) span:last-child")
                            .text() + "]" + it.select("p.xg1").text(),
                        targetTid.toInt()
                    )
                )
            }

            runOnUiThread {
                val list = findViewById<ListView>(R.id.threadItemList)
                list?.adapter =
                    ThreadItemListAdapter(
                        applicationContext,
                        0,
                        threadListContent
                    )

                findViewById<ListView>(R.id.threadItemList)
                    ?.setOnItemClickListener { _, _, position, _ ->
                        val intent = Intent(this@SearchResultActivity, ViewThread::class.java)
                        intent.putExtra("tid", threadListContent[position].target)
                        startActivity(intent)
                    }
            }
        }

        fun search() {
            LoadingMaskContainer.visibility = View.VISIBLE
            val s = HttpExt().asyncPostPage(
                "http://ditiezu.com/search.php?mod=forum",
                "formhash=$formhash&srchtxt=" + URLEncoder.encode(
                    kw.toString(),
                    "GBK"
                ) + "&searchsubmit=yes"
            )
            when {
                s == "Failed Retrieved" -> {
                    Dialog().tip(
                        resources.getString(R.string.failed_retrieved),
                        R.drawable.ic_baseline_close_24,
                        R.color.danger,
                        this@SearchResultActivity,
                        SearchResultActivity,
                        Dialog.TIME_SHORT
                    )
                }
                s.contains("用户登录") -> {
                    Dialog().tip(
                        resources.getString(R.string.login_tips),
                        R.drawable.ic_baseline_close_24,
                        R.color.danger,
                        this@SearchResultActivity,
                        SearchResultActivity,
                        Dialog.TIME_SHORT
                    )
                }
                s.contains("只能进行一次搜索") -> {
                    Dialog().tip(
                        resources.getString(R.string.search_15s),
                        R.drawable.ic_baseline_close_24,
                        R.color.danger,
                        this@SearchResultActivity,
                        SearchResultActivity,
                        Dialog.TIME_SHORT
                    )
                }
                s.contains("站点设置每分钟系统最多") -> {
                    Dialog().tip(
                        resources.getString(R.string.search_system_restriction),
                        R.drawable.ic_baseline_close_24,
                        R.color.danger,
                        this@SearchResultActivity,
                        SearchResultActivity,
                        Dialog.TIME_SHORT
                    )
                }
                s.contains("没有找到匹配结果") -> {
                    Dialog().tip(
                        resources.getString(R.string.keyword_not_match, kw),
                        R.drawable.ic_baseline_close_24,
                        R.color.danger,
                        this@SearchResultActivity,
                        SearchResultActivity,
                        Dialog.TIME_SHORT
                    )
                }
                else -> {
                    processResult(s)
                }
            }
            LoadingMaskContainer.visibility = View.GONE
        }
        search()

        val input = findViewById<EditText>(R.id.app_search_input)
        input.setText(kw)

        input.setOnKeyListener(object : View.OnKeyListener {
            override fun onKey(v: View?, keyCode: Int, event: KeyEvent): Boolean {
                if (event.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER && input.text.toString()
                        .trim().isNotEmpty()
                ) {
                    kw = input.text.toString()
                    search()
                    return true
                }
                return false
            }
        })
    }

    override fun dispatchTouchEvent(event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_DOWN) {
            val v = currentFocus
            if (v is EditText) {
                val outRect = Rect()
                v.getGlobalVisibleRect(outRect)
                if (!outRect.contains(event.rawX.toInt(), event.rawY.toInt())) {
                    v.clearFocus()
                    val imm: InputMethodManager =
                        getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0)
                }
            }
        }
        return super.dispatchTouchEvent(event)
    }
}