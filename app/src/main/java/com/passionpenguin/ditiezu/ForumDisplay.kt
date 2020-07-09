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
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.children
import androidx.core.view.isEmpty
import androidx.recyclerview.widget.LinearLayoutManager
import com.passionpenguin.ditiezu.helper.*
import kotlinx.android.synthetic.main.activity_forum_display.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.jsoup.Jsoup

class ForumDisplay : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forum_display)

        val extras = intent.extras
        val id = extras?.getInt("id") ?: return

        val categoryContent = CategoryContent(applicationContext)
        val categoryList = categoryContent.categoryList[id]

        fab.setOnClickListener {
            val i = Intent(this@ForumDisplay, Editor::class.java)
            i.putExtra("type", "newthread")
            i.putExtra("fid", categoryList.id)
            startActivity(i)
        }

        var adapter = ThreadItemAdapter(this@ForumDisplay, listOf(), isHome = false, withHeader = true, withNavigation = true, curCategoryItem = categoryList, curPage = 1, lastPage = 1, disabledCurPage = false, enabledPrev = false, enabledNext = false) {}

        fun loadForumContent(page: Int, ext: String = "") {
            fun processResult(result: String) {
                val parser = Jsoup.parse(result)
                runOnUiThread {
                    if (typesNavigation.isEmpty()) {
                        val t = TextView(applicationContext)
                        t.text = resources.getString(R.string.all)
                        t.setTextColor(resources.getColor(R.color.black, null))
                        t.setOnClickListener { loadForumContent(1) }
                        t.setPadding(resources.getDimension(R.dimen._8).toInt(), resources.getDimension(R.dimen._16).toInt(), resources.getDimension(R.dimen._8).toInt(), resources.getDimension(R.dimen._16).toInt())
                        t.background = resources.getDrawable(R.drawable.border_bottom, null)
                        typesNavigation.addView(t)
                        parser.select("#thread_types li:not(.fold):not(#ttp_all)").forEach {
                            with(it.select("a").attr("href")) {
                                val text = TextView(applicationContext)
                                text.text = it.select("a").text()
                                text.setTextColor(resources.getColor(R.color.black, null))
                                text.setOnClickListener { _ ->
                                    if (!it.className().contains("xw1"))
                                        loadForumContent(1, "?" + this.substring(this.indexOf("filter=")))
                                }
                                text.setPadding(resources.getDimension(R.dimen._8).toInt(), resources.getDimension(R.dimen._16).toInt(), resources.getDimension(R.dimen._8).toInt(), resources.getDimension(R.dimen._16).toInt())
                                try {
                                    if (it.className().contains("xw1")) text.background = resources.getDrawable(R.drawable.border_bottom, null)
                                } catch (ignored: Exception) {
                                }
                                typesNavigation.addView(text)
                            }
                        }
                    } else {
                        typesNavigation.children.toList().forEach { it.background = null }
                        if (!ext.contains("typeid"))
                            typesNavigation.children.toList()[0].background = resources.getDrawable(R.drawable.border_bottom, null)
                        else {
                            var i = -1
                            parser.select("#thread_types li:not(.fold)").forEachIndexed { index, e -> if (e.className().contains("xw1")) i = index }
                            if (i == -1) i = 0
                            typesNavigation.children.toList()[i].background = resources.getDrawable(R.drawable.border_bottom, null)
                        }
                    }

                    val threadListContent = mutableListOf<ThreadItem>()
                    parser.select("[id^='normalthread_']").forEach {
                        val type = if (it.select(".new em").text() !== "") {
                            it.select(".new em").text()
                        } else {
                            ""
                        }
                        val author = it.select(".by cite a")[0]
                        val authorName = author.text()
                        val time = it.select(".by:not(.kmhf) em span").text()
                        val views = it.select(".num em").text()
                        val replies = it.select(".num a").text()
                        val lastTime = it.select(".by.kmhf em span").attr("title")
                        val title = it.select(".xst")
                        var targetId: Int
                        with(title.attr("href")) {
                            targetId = if (this.contains(".html")) this.substring(30, this.lastIndexOf(".html") - 4).toInt()
                            else this.substring(52, this.indexOf("&", 52)).toInt()
                        }
                        threadListContent.add(
                            ThreadItem(
                                author.attr("href").substring(
                                    author.attr("href").indexOf("uid-") + 4,
                                    author.attr("href").indexOf(".html")
                                ).toInt(),
                                title.text(),
                                "",
                                authorName,
                                time + " " + views + resources.getString(R.string.views) + " " + replies + " " + resources.getString(
                                    R.string.replies
                                ),
                                "$type $lastTime",
                                targetId
                            )
                        )
                        adapter.mItems.forEachIndexed { i, _ -> adapter.changeData(i) }
                        adapter = ThreadItemAdapter(
                            this@ForumDisplay,
                            threadListContent,
                            isHome = false,
                            withHeader = true,
                            withNavigation = true,
                            curCategoryItem = categoryList,
                            curPage = page,
                            lastPage = if (!parser.select(".last").isEmpty()) parser.select(".last")[0].text().substring(4).toInt()
                            else if (!parser.select("#pgt .pg a:not(.nxt)").isEmpty()) parser.select("#pgt .pg a:not(.nxt)").last().text().toInt() else 1,
                            disabledCurPage = false,
                            enabledPrev = false,
                            enabledNext = false
                        ) { p ->
                            loadForumContent(p)
                        }
                        ThreadList.adapter = adapter
                        ThreadList.layoutManager = LinearLayoutManager(this@ForumDisplay)
                    }
                }
            }
            GlobalScope.launch {
                with(HttpExt.retrievePage("http://www.ditiezu.com/forum-${categoryList.id}-$page.html$ext")) {
                    if (this == "Failed Retrieved")
                        Dialog.tip(resources.getString(R.string.failed_retrieved), R.drawable.ic_baseline_close_24, R.color.danger, this@ForumDisplay, ForumDisplay, Dialog.TIME_SHORT)
                    processResult(this)
                }
            }
        }
        loadForumContent(1)

        val input = findViewById<EditText>(R.id.app_search_input)

        input.setOnKeyListener(object : View.OnKeyListener {
            override fun onKey(v: View?, keyCode: Int, event: KeyEvent): Boolean {
                if (event.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER && input.text.toString().trim().isNotEmpty()) {
                    val i = Intent(this@ForumDisplay, SearchResultActivity::class.java)
                    i.putExtra("kw", input.text.toString())
                    startActivity(i)
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