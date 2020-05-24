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
import org.jsoup.Jsoup


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val homeButton: LinearLayout = findViewById(R.id.HomeButton)
        val categoryButton: LinearLayout = findViewById(R.id.CategoryButton)
        val notificationButton: LinearLayout = findViewById(R.id.NotificationButton)
        val accountButton: LinearLayout = findViewById(R.id.AccountButton)
        val categoryListContainer: LinearLayout = findViewById(R.id.CategoryListContainer)
        val categoryListMaskContainer: LinearLayout = findViewById(R.id.CategoryListMaskContainer)
        val categoryListView: ListView = findViewById(R.id.CategoryList)
        val threadListView: ListView = findViewById(R.id.ThreadList)
        val mask: LinearLayout = findViewById(R.id.LoadingMaskContainer)
        fun string(int: Int): String {
            return resources.getString(int)
        }

        threadListView.addHeaderView(layoutInflater.inflate(R.layout.banner, null))

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

        val categoryList = mutableListOf(
            CategoryItem(
                string(R.string.category_Beijing),
                R.drawable.beijing
            ),
            CategoryItem(
                string(R.string.category_Tianjin),
                R.drawable.tianjin
            ),
            CategoryItem(
                string(R.string.category_Shanghai),
                R.drawable.shanghai
            ),
            CategoryItem(
                string(R.string.category_Guangzhou),
                R.drawable.guangzhou
            ),
            CategoryItem(
                string(R.string.category_Changchun),
                R.drawable.changchun
            ),
            CategoryItem(
                string(R.string.category_Dalian),
                R.drawable.dalian
            ),
            CategoryItem(
                string(R.string.category_Wuhan),
                R.drawable.wuhan
            ),
            CategoryItem(
                string(R.string.category_Chongqing),
                R.drawable.chongqing
            ),
            CategoryItem(
                string(R.string.category_Shenzhen),
                R.drawable.shenzhen
            ),
            CategoryItem(
                string(R.string.category_Nanjing),
                R.drawable.nanjing
            ),
            CategoryItem(
                string(R.string.category_Chengdu),
                R.drawable.chengdu
            ),
            CategoryItem(
                string(R.string.category_Shenyang),
                R.drawable.shenyang
            ),
            CategoryItem(
                string(R.string.category_Foshan),
                R.drawable.foshan
            ),
            CategoryItem(
                string(R.string.category_Xian),
                R.drawable.xian
            ),
            CategoryItem(
                string(R.string.category_Suzhou),
                R.drawable.suzhou
            ),
            CategoryItem(
                string(R.string.category_Kunming),
                R.drawable.kunming
            ),
            CategoryItem(
                string(R.string.category_Hangzhou),
                R.drawable.hangzhou
            ),
            CategoryItem(
                string(R.string.category_Harbin),
                R.drawable.harbin
            ),
            CategoryItem(
                string(R.string.category_Zhengzhou),
                R.drawable.zhengzhou
            ),
            CategoryItem(
                string(R.string.category_Changsha),
                R.drawable.changsha
            ),
            CategoryItem(
                string(R.string.category_Ningbo),
                R.drawable.ningbo
            ),
            CategoryItem(
                string(R.string.category_Wuxi),
                R.drawable.wuxi
            ),
            CategoryItem(
                string(R.string.category_Qingdao),
                R.drawable.qingdao
            ),
            CategoryItem(
                string(R.string.category_Nanchang),
                R.drawable.nanchang
            ),
            CategoryItem(
                string(R.string.category_Fuzhou),
                R.drawable.fuzhou
            ),
            CategoryItem(
                string(R.string.category_Dongguan),
                R.drawable.dongguan
            ),
            CategoryItem(
                string(R.string.category_Nanning),
                R.drawable.nanning
            ),
            CategoryItem(
                string(R.string.category_Hefei),
                R.drawable.hefei
            ),
            CategoryItem(
                string(R.string.category_Shijiazhuang),
                R.drawable.shijiazhuang
            ),
            CategoryItem(
                string(R.string.category_Guiyang),
                R.drawable.guiyang
            ),
            CategoryItem(
                string(R.string.category_Xiamen),
                R.drawable.xiamen
            ),
            CategoryItem(
                string(R.string.category_Urumqi),
                R.drawable.urumqi
            ),
            CategoryItem(
                string(R.string.category_Wenzhou),
                R.drawable.wenzhou
            ),
            CategoryItem(
                string(R.string.category_Jinan),
                R.drawable.jinan
            ),
            CategoryItem(
                string(R.string.category_Lanzhou),
                R.drawable.lanzhou
            ),
            CategoryItem(
                string(R.string.category_Changzhou),
                R.drawable.changzhou
            ),
            CategoryItem(
                string(R.string.category_Xuzhou),
                R.drawable.xuzhou
            ),
            CategoryItem(
                string(R.string.category_Hongkong),
                R.drawable.hongkong
            ),
            CategoryItem(
                string(R.string.category_Macau),
                R.drawable.macau
            ),
            CategoryItem(
                string(R.string.category_Taiwan),
                R.drawable.taiwan
            )
        )
        val categoryId = arrayOf(
            7,
            6,
            8,
            23,
            40,
            41,
            39,
            38,
            24,
            22,
            53,
            50,
            56,
            54,
            51,
            70,
            52,
            55,
            64,
            67,
            65,
            68,
            66,
            71,
            72,
            75,
            73,
            74,
            140,
            76,
            77,
            143,
            142,
            148,
            78,
            48,
            144,
            151,
            28,
            79,
            36,
            47,
            37
        )
        categoryListView.adapter =
            CategoryAdapter(
                this,
                R.layout.category_popup,
                categoryList
            )
        categoryListView.setOnItemClickListener { _, _, position, _ ->
            val i = Intent(this@MainActivity, ForumDisplay::class.java)
            i.putExtra(
                "fid",
                categoryId[position - 1]
            )
            startActivity(i)
            categoryDialogController(true)
            Toast.makeText(this@MainActivity, position.toString(), Toast.LENGTH_LONG).show()
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
        }


        fun processResult(result: String) {
            val parser = Jsoup.parse(result)
            runOnUiThread {
                val threadListContent = mutableListOf<ThreadListItem>()
                parser.select("ul.comiis_onemiddleulone li").forEach {
                    val author = it.select("code a")
                    val authorName = author.text()
                    val category = it.select(".orgen").text()
                    val title = it.select(".blackvs")
                    threadListContent.add(
                        ThreadListItem(
                            author.attr("href").substring(
                                author.attr("href").indexOf("uid-") + 4,
                                author.attr("href").indexOf(".html")
                            ).toInt(),
                            title.text(),
                            authorName,
                            "来自头条推荐 · $category",
                            title.attr("href")
                                .substring(30, title.attr("href").lastIndexOf("-1-1")).toInt()
                        )
                    )
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
                        val i = Intent(this@MainActivity, ViewThread::class.java)
                        i.putExtra(
                            "tid",
                            threadListContent[position - 1].target
                        )
                        startActivity(i)
                    }
                }
            }
        }

        HttpExt().retrievePage("http://www.ditiezu.com/") {
            val ctrlAnimation = TranslateAnimation(
                TranslateAnimation.RELATIVE_TO_SELF, 0F,
                TranslateAnimation.RELATIVE_TO_SELF, 0F,
                TranslateAnimation.RELATIVE_TO_SELF, 0F,
                TranslateAnimation.RELATIVE_TO_SELF, 1F
            )
            ctrlAnimation.duration = 400L
            findViewById<LinearLayout>(R.id.LoadingMaskContainer).visibility = View.VISIBLE
            findViewById<LinearLayout>(R.id.LoadingMaskContainer).startAnimation(ctrlAnimation)
            findViewById<LinearLayout>(R.id.LoadingMaskContainer).postDelayed(400) {
                findViewById<LinearLayout>(R.id.LoadingMaskContainer).visibility = View.GONE
            }

            if (it == "Failed Retrieved") {
                // Failed Retrieved
                Log.i("HTTPEXT", "FAILED RETRIEVED")
            }
            processResult(it)
        }
    }
}