package com.passionpenguin.ditiezu

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.animation.TranslateAnimation
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.postDelayed
import com.passionpenguin.ditiezu.helper.*
import org.jsoup.Jsoup


class ForumDisplay : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val extras = intent.extras
        var fid = 23
        if (extras != null) {
            fid = extras.getInt("fid")
        } else finish()

        val homeButton: LinearLayout = findViewById(R.id.HomeButton)
        val categoryButton: LinearLayout = findViewById(R.id.CategoryButton)
        val notificationButton: LinearLayout = findViewById(R.id.NotificationButton)
        val accountButton: LinearLayout = findViewById(R.id.AccountButton)
        val categoryListContainer: LinearLayout = findViewById(R.id.CategoryListContainer)
        val categoryListMaskContainer: LinearLayout = findViewById(R.id.CategoryListMaskContainer)
        val categoryListView: ListView = findViewById(R.id.CategoryList)
        val threadListView: ListView = findViewById(R.id.ThreadList)
        val mask: LinearLayout = findViewById(R.id.LoadingMaskContainer)

        val categoryContent =
            CategoryContent(applicationContext)
        val categoryList = categoryContent.categoryList
        val categoryId = categoryContent.categoryId

        fun bottomButtonHighlight(clear: Array<LinearLayout>, add: LinearLayout) {
            clear.forEach {
                (it.getChildAt(0) as ImageView).setColorFilter(
                    ContextCompat.getColor(applicationContext, R.color.black),
                    android.graphics.PorterDuff.Mode.SRC_IN
                )
                (it.getChildAt(1) as TextView).setTextColor(
                    ContextCompat.getColor(
                        applicationContext,
                        R.color.black
                    )
                )
            }
            (add.getChildAt(0) as ImageView).setColorFilter(
                ContextCompat.getColor(applicationContext, R.color.primary700),
                android.graphics.PorterDuff.Mode.SRC_IN
            )
            (add.getChildAt(1) as TextView).setTextColor(
                ContextCompat.getColor(
                    applicationContext,
                    R.color.primary700
                )
            )
            mask.visibility = View.GONE
        }

        fun categoryDialogController(state: Boolean) {
            val ctrlAnimation = if (state) TranslateAnimation(
                TranslateAnimation.RELATIVE_TO_SELF, 0F, TranslateAnimation.RELATIVE_TO_SELF, 0F,
                TranslateAnimation.RELATIVE_TO_SELF, 0F, TranslateAnimation.RELATIVE_TO_SELF, 1F
            ) else TranslateAnimation(
                TranslateAnimation.RELATIVE_TO_SELF, 0F, TranslateAnimation.RELATIVE_TO_SELF, 0F,
                TranslateAnimation.RELATIVE_TO_SELF, 1F, TranslateAnimation.RELATIVE_TO_SELF, 0F
            )
            ctrlAnimation.duration = 400L //设置动画的过渡时间

            categoryListMaskContainer.visibility = View.VISIBLE
            categoryListContainer.startAnimation(ctrlAnimation)
            if (state) {
                categoryListMaskContainer.postDelayed(400) {
                    categoryListMaskContainer.visibility = View.GONE
                }
                bottomButtonHighlight(
                    arrayOf(categoryButton, notificationButton, accountButton),
                    homeButton
                )
            }
        }

        fun loadForumContent(page: Int) {
            fun processResult(result: String) {
                val parser = Jsoup.parse(result)
                runOnUiThread {
                    threadListView.removeHeaderView(threadListView.findViewById(R.id.categoryHeader))
                    threadListView.removeFooterView(threadListView.findViewById(R.id.paginationNavigation))
                    val bannerView = layoutInflater.inflate(R.layout.category_info_header, null)
                    bannerView.findViewById<ImageView>(R.id.CategoryIcon)
                        .setImageDrawable(
                            resources.getDrawable(
                                categoryList[categoryId.indexOf(fid)].icon,
                                null
                            )
                        )
                    bannerView.findViewById<TextView>(R.id.CategoryTitle).text =
                        categoryList[categoryId.indexOf(fid)].title
                    threadListView.addHeaderView(bannerView)

                    val footerPagination =
                        layoutInflater.inflate(R.layout.category_pagination_navigation, null)
                    val lastPage = parser.select(".last")[0].text().substring(4).toInt()

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

                    val threadListContent = mutableListOf<ThreadListItem>()
                    parser.select("[id^='normalthread_']").forEach {
                        try {
                            val type = if (it.select(".new em").text() !== "") {
                                it.select(".new em").text()
                            } else {
                                ""
                            }
                            val author = it.select(".by cite a")[0]
                            val authorName = author.text()
                            val time = it.select(".by em span").text()
                            val title = it.select(".new .xst")
                            threadListContent.add(
                                ThreadListItem(
                                    author.attr("href").substring(
                                        author.attr("href").indexOf("uid-") + 4,
                                        author.attr("href").indexOf(".html")
                                    ).toInt(),
                                    title.text(),
                                    authorName,
                                    "$type $time",
                                    title.attr("href")
                                        .substring(
                                            30,
                                            title.attr("href").lastIndexOf(".html") - 4
                                        )
                                        .toInt()
                                )
                            )
                        } catch (Exception: Exception) {
                            Log.i("Error", Exception.toString())
                        }
                    }
                    threadListView.adapter =
                        ThreadListAdapter(
                            this,
                            R.layout.thread_list_item,
                            threadListContent
                        )

                    threadListView.setOnItemClickListener { _, _, position, _ ->
                        if (position == 0) {
                            // Banner
                        } else {
                            val i = Intent(this@ForumDisplay, ViewThread::class.java)
                            i.putExtra(
                                "tid",
                                threadListContent[position - 1].target
                            )
                            startActivity(i)
                        }
                    }
                }
            }

            HttpExt().retrievePage("http://www.ditiezu.com/forum-$fid-$page.html") {
                runOnUiThread {
                    findViewById<LinearLayout>(R.id.LoadingMaskContainer).visibility = View.VISIBLE
                    findViewById<LinearLayout>(R.id.LoadingMaskContainer).startAnimation(
                        Animation().fadeOutAnimation())
                    findViewById<LinearLayout>(R.id.LoadingMaskContainer).postDelayed(400) {
                        findViewById<LinearLayout>(R.id.LoadingMaskContainer).visibility = View.GONE
                    }
                }

                if (it == "Failed Retrieved") {
                    // Failed Retrieved
                    Log.i("HTTPEXT", "FAILED RETRIEVED")
                }
                processResult(it)
            }
        }

        categoryListView.adapter =
            CategoryAdapter(
                this,
                R.layout.category_popup,
                categoryList
            )
        categoryListView.setOnItemClickListener { _, _, position, _ ->
            categoryDialogController(true)
            fid = categoryId[position]
            loadForumContent(1)
        }
        categoryListMaskContainer.setOnClickListener {
            categoryDialogController(true)
        }
        findViewById<LinearLayout>(R.id.LoadingMaskContainer).setOnClickListener {
            it.visibility = View.GONE
        }
        homeButton.setOnClickListener {
            bottomButtonHighlight(
                arrayOf(categoryButton, notificationButton, accountButton),
                homeButton
            )
        }
        categoryButton.setOnClickListener {
            bottomButtonHighlight(
                arrayOf(homeButton, notificationButton, accountButton),
                categoryButton
            )

            categoryDialogController(false)
        }
        notificationButton.setOnClickListener {
            bottomButtonHighlight(
                arrayOf(homeButton, categoryButton, accountButton),
                notificationButton
            )
        }
        accountButton.setOnClickListener {
            bottomButtonHighlight(
                arrayOf(homeButton, notificationButton, categoryButton),
                accountButton
            )
            startActivity(Intent(this@ForumDisplay, AccountActivity::class.java))
        }

        loadForumContent(1)
    }
}