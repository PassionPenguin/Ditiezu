package com.passionpenguin.ditiezu

import android.content.Context
import android.content.Intent
import android.graphics.Rect
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.children
import com.passionpenguin.ditiezu.helper.*
import kotlinx.android.synthetic.main.activity_forum_display.*
import kotlinx.android.synthetic.main.fragment_action_bar.*
import org.jsoup.Jsoup

class ForumDisplay : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forum_display)

        val extras = intent.extras
        var fid = 23
        if (extras != null) {
            fid = extras.getInt("fid")
        } else finish()

        val categoryContent =
            CategoryContent(applicationContext)
        val categoryList = categoryContent.categoryList
        val categoryId = categoryContent.categoryId

        val threadListView = ThreadList

        fun loadForumContent(page: Int, ext: String = "") {
            fun processResult(result: String) {
                val parser = Jsoup.parse(result)
                runOnUiThread {
                    threadListView.removeHeaderView(threadListView.findViewById(R.id.categoryHeader))
                    threadListView.removeFooterView(threadListView.findViewById(R.id.paginationNavigation))

                    if (threadListView.findViewById<HorizontalScrollView>(R.id.typeNavigationWrap) === null) {
                        val list = layoutInflater.inflate(R.layout.fragment_types_list, null)
                        val t = TextView(applicationContext)
                        t.text = resources.getString(R.string.all)
                        t.setTextColor(resources.getColor(R.color.black, null))
                        t.setOnClickListener { _ ->
                            loadForumContent(1)
                        }
                        t.setPadding(
                            resources.getDimension(R.dimen._8).toInt(),
                            resources.getDimension(R.dimen._16).toInt(),
                            resources.getDimension(R.dimen._8).toInt(),
                            resources.getDimension(R.dimen._16).toInt()
                        )
                        t.background = resources.getDrawable(R.drawable.border_bottom, null)
                        list.findViewById<LinearLayout>(R.id.typesNavigation).addView(t)
                        parser.select("#thread_types li:not(.fold):not(#ttp_all)").forEach {
                            with(it.select("a").attr("href")) {
                                val text = TextView(applicationContext)
                                text.text = it.select("a").text()
                                text.setTextColor(resources.getColor(R.color.black, null))
                                text.setOnClickListener { _ ->
                                    if (!it.className().contains("xw1"))
                                        loadForumContent(
                                            1, "?" + this.substring(this.indexOf("filter="))
                                        )
                                }
                                text.setPadding(
                                    resources.getDimension(R.dimen._8).toInt(),
                                    resources.getDimension(R.dimen._16).toInt(),
                                    resources.getDimension(R.dimen._8).toInt(),
                                    resources.getDimension(R.dimen._16).toInt()
                                )
                                try {
                                    if (it.className().contains("xw1"))
                                        text.background =
                                            resources.getDrawable(R.drawable.border_bottom, null)
                                } catch (exception: Exception) {
                                    Log.e("", this + exception.toString())
                                }
                                list.findViewById<LinearLayout>(R.id.typesNavigation).addView(text)
                            }
                        }
                        threadListView.addHeaderView(list)
                    } else {
                        threadListView.findViewById<LinearLayout>(R.id.typesNavigation).children.toList()
                            .forEach {
                                it.background = null
                            }
                        if (!ext.contains("typeid"))
                            threadListView.findViewById<LinearLayout>(R.id.typesNavigation).children.toList()[0].background =
                                resources.getDrawable(R.drawable.border_bottom, null)
                        else {
                            var i = -1
                            parser.select("#thread_types li:not(.fold)")
                                .forEachIndexed { index, e ->
                                    if (e.className().contains("xw1")) i = index
                                }
                            if (i == -1) i = 0
                            threadListView.findViewById<LinearLayout>(R.id.typesNavigation).children.toList()[i].background =
                                resources.getDrawable(R.drawable.border_bottom, null)
                        }
                    }

                    val bannerView =
                        layoutInflater.inflate(R.layout.item_category_info_header, null)
                    bannerView.findViewById<ImageView>(R.id.CategoryIcon)
                        .setImageDrawable(
                            resources.getDrawable(
                                categoryList[categoryId.indexOf(fid)].icon,
                                null
                            )
                        )
                    bannerView.findViewById<TextView>(R.id.CategoryTitle).text =
                        categoryList[categoryId.indexOf(fid)].title
                    bannerView.findViewById<TextView>(R.id.CategoryMeta).text =
                        parser.select(".xw0.xs1.i").text().replace("|", " | ")
                    bannerView.findViewById<TextView>(R.id.CategoryDescription).text =
                        categoryList[categoryId.indexOf(fid)].description
                    threadListView.addHeaderView(bannerView)

                    val footerPagination =
                        layoutInflater.inflate(R.layout.item_category_pagination_navigation, null)
                    val lastPage =
                        if (!parser.select(".last").isEmpty())
                            parser.select(".last")[0].text().substring(4).toInt()
                        else parser.select("#pgt .pg a:not(.nxt)").last().text().toInt()

                    footerPagination.findViewById<TextView>(R.id.curPage).text = page.toString()

                    val firstPageView = footerPagination.findViewById<ImageButton>(R.id.firstPage)
                    if (page == 1) firstPageView.visibility = View.GONE
                    else firstPageView.setOnClickListener {
                        loadForumContent(1)
                    }

                    val lastPageView = footerPagination.findViewById<ImageButton>(R.id.lastPage)
                    if (page == 1) lastPageView.visibility = View.GONE
                    else lastPageView.setOnClickListener {
                        loadForumContent(lastPage)
                    }

                    val prevPage = footerPagination.findViewById<ImageButton>(R.id.prevPage)
                    val prevPage1 = footerPagination.findViewById<TextView>(R.id.prevPage1)
                    if (page - 1 < 1) {
                        prevPage.visibility = View.GONE
                        prevPage1.visibility = View.GONE
                    } else {
                        prevPage.setOnClickListener {
                            loadForumContent(page - 1)
                        }
                        prevPage1.setOnClickListener {
                            loadForumContent(page - 1)
                        }
                        prevPage1.text = (page - 1).toString()
                    }

                    val prevPage2 = footerPagination.findViewById<TextView>(R.id.prevPage2)
                    if (page - 2 < 1) prevPage2.visibility = View.GONE
                    else prevPage2.setOnClickListener {
                        loadForumContent(page - 2)
                    }
                    prevPage2.text = (page - 2).toString()

                    val nextPage = footerPagination.findViewById<ImageButton>(R.id.nextPage)
                    val nextPage1 = footerPagination.findViewById<TextView>(R.id.nextPage1)
                    if (page + 1 > lastPage) {
                        nextPage.visibility = View.GONE
                        nextPage1.visibility = View.GONE
                    } else {
                        nextPage.setOnClickListener {
                            loadForumContent(page + 1)
                        }
                        nextPage1.setOnClickListener {
                            loadForumContent(page + 1)
                        }
                        nextPage1.text = (page + 1).toString()
                    }

                    val nextPage2 = footerPagination.findViewById<TextView>(R.id.nextPage2)
                    if (page + 2 > lastPage) nextPage2.visibility = View.GONE
                    else nextPage2.setOnClickListener {
                        loadForumContent(page + 2)
                    }
                    nextPage2.text = (page + 2).toString()
                    threadListView.addFooterView(footerPagination)

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
                            targetId = if (this.contains(".html"))
                                this.substring(30, this.lastIndexOf(".html") - 4).toInt()
                            else
                                this.substring(52, this.indexOf("&", 52)).toInt()
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
                        threadListView.adapter =
                            ThreadItemListAdapter(
                                this,
                                0,
                                threadListContent
                            )

                        threadListView.setOnItemClickListener { _, _, position, _ ->
                            if (position != 0 || position != 1) {
                                val i = Intent(this@ForumDisplay, ViewThread::class.java)
                                i.putExtra(
                                    "tid",
                                    threadListContent[position - 2].target
                                )
                                startActivity(i)
                            }
                        }
                    }
                }
            }
            HttpExt().retrievePage("http://www.ditiezu.com/forum-$fid-$page.html$ext") {
                if (it == "Failed Retrieved") {
                    // Failed Retrieved
                    Log.i("HTTPEXT", "FAILED RETRIEVED")
                }
                processResult(it)
            }
        }
        loadForumContent(1)

        val input = app_search_input

        input.setOnKeyListener(object : View.OnKeyListener {
            override fun onKey(v: View?, keyCode: Int, event: KeyEvent): Boolean {
                if (event.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER && input.text.toString()
                        .trim().isNotEmpty()
                ) {
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