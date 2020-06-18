package com.passionpenguin.ditiezu

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.passionpenguin.ditiezu.helper.Dialog
import com.passionpenguin.ditiezu.helper.HttpExt
import com.passionpenguin.ditiezu.helper.ThreadItem
import com.passionpenguin.ditiezu.helper.ThreadItemAdapter
import kotlinx.android.synthetic.main.activity_forum_display.ForumDisplay
import kotlinx.android.synthetic.main.activity_personal_history.*
import org.jsoup.Jsoup

//
class PersonalHistory : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_personal_history)
        var curPage = 1
        var type = "thread"

        val extras = intent.extras
        val uid = extras?.getInt("uid") ?: return

        fun loadContent() {
            fun processResult(result: String) {
                val parser = Jsoup.parse(result)
                runOnUiThread {
                    val threadListContent = mutableListOf<ThreadItem>()
                    parser.select(".tl table tr:not(.th)").forEach {
                        val typ = it.select("td:nth-child(3)").text()
                        val views = it.select(".num em").text()
                        val replies = it.select(".num a").text()
                        val title = it.select("th>a")
                        var targetId: Int
                        with(title.attr("href")) {
                            targetId = if (this.contains(".html"))
                                this.substring(30, this.lastIndexOf(".html") - 4).toInt()
                            else
                                this.substring(52, this.indexOf("&", 52)).toInt()
                        }
                        threadListContent.add(
                            ThreadItem(
                                uid,
                                title.text(),
                                "",
                                "",
                                views + resources.getString(R.string.views) + " " + replies + " " + resources.getString(
                                    R.string.replies
                                ),
                                typ,
                                targetId
                            )
                        )
                    }
                    Log.i(
                        parser.select(".pgb").isEmpty().toString(),
                        parser.select(".nxt").isEmpty().toString()
                    )
                    history.adapter = ThreadItemAdapter(
                        this@PersonalHistory,
                        threadListContent,
                        isHome = false,
                        withHeader = false,
                        withNavigation = true,
                        curCategoryItem = null,
                        curPage = curPage,
                        lastPage = curPage,
                        disabledCurPage = true,
                        enabledPrev = parser.select(".pgb").isNotEmpty(),
                        enabledNext = parser.select(".nxt").isNotEmpty()
                    ) { page ->
                        curPage = page
                        loadContent()
                    }
                    history.layoutManager = LinearLayoutManager(this@PersonalHistory)
                }
            }

            HttpExt().retrievePage("http://www.ditiezu.com/home.php?mod=space&uid=$uid&do=thread&view=me&type=$type&order=dateline&page=$curPage") {
                if (it == "Failed Retrieved")
                    Dialog().tip(
                        resources.getString(R.string.failed_retrieved),
                        R.drawable.ic_baseline_close_24,
                        R.color.danger,
                        this@PersonalHistory,
                        ForumDisplay,
                        Dialog.TIME_SHORT
                    )
                processResult(it)
            }
        }
        loadContent()
    }

}