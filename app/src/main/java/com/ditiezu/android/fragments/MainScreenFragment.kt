/*
 * ==================================================
 * =  PROJECT     地下铁的故事
 * =  MODULE      地下铁的故事.app
 * =  FILE NAME   MainScreenFragment.kt
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

package com.ditiezu.android.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ditiezu.android.R
import com.ditiezu.android.adapters.ThreadItem
import com.ditiezu.android.adapters.ThreadItemAdapter
import com.passionpenguin.NetUtils
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.jsoup.Jsoup
import org.jsoup.parser.Parser
import java.util.*

class MainScreenFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_item_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        activity?.let {
            val spinner = it.findViewById<ImageView>(R.id.spinner)
            val spinnerAnim = with(AnimationUtils.loadAnimation(it, R.anim.rotate)) {
                this.duration = 1000
                this.fillAfter = true
                this
            }
            val list = view.findViewById<RecyclerView>(R.id.list)

            /*
            if (activity?.let { HttpExt.checkLogin(it) }!!)
            activity?.let { activity ->
                val s = NetUtils(activity).retrievePage("http://www.ditiezu.com/search.php?mod=forum&srchfrom=31536000&searchsubmit=yes") // 有点离谱哦～一年分量～
                activity.runOnUiThread {
                    when {
                        s == "Failed Retrieved" -> {
                            Dialog.tip(
                                resources.getString(R.string.failed_retrieved),
                                R.drawable.ic_baseline_close_24,
                                R.color.danger,
                                activity,
                                threadItemList,
                                Dialog.TIME_SHORT
                            )
                        }
                        s.contains("用户登录") -> {
                            Dialog.tip(
                                resources.getString(R.string.login_tips),
                                R.drawable.ic_baseline_close_24,
                                R.color.danger,
                                activity,
                                threadItemList,
                                Dialog.TIME_SHORT
                            )
                        }
                        s.contains("只能进行一次搜索") -> {
                            Dialog.tip(
                                resources.getString(R.string.search_15s),
                                R.drawable.ic_baseline_close_24,
                                R.color.danger,
                                activity,
                                threadItemList,
                                Dialog.TIME_SHORT
                            )
                        }
                        s.contains("站点设置每分钟系统最多") -> {
                            Dialog.tip(
                                resources.getString(R.string.search_system_restriction),
                                R.drawable.ic_baseline_close_24,
                                R.color.danger,
                                activity,
                                threadItemList,
                                Dialog.TIME_SHORT
                            )
                        }
                        else -> {
                            val parser = Jsoup.parse(s)
                            val threadListContent = mutableListOf<ThreadItem>()
                            parser.select("#threadlist .pbw").forEach {
                                val title = it.select("h3 a")
                                val author = it.select("p")[2].select("span a")[0]
                                val meta = it.select(".xg1").text()
                                var targetTid = ""
                                with(title.attr("href")) {
                                    try {
                                        targetTid = if (this.contains("highlight")) this.substring(this.indexOf("tid=") + 4, this.indexOf("&", this.indexOf("tid=") + 4))
                                        else this.substring(this.indexOf("tid=") + 4)
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
                                        it.select("span:first-child").text(),
                                        it.select(".xi1").text(),
                                        meta.substring(meta.indexOf(" - ") + 3, meta.length - 3),
                                        meta.substring(0, meta.indexOf("个回复")),
                                        targetTid.toInt()
                                    )
                                )
                            }

                            activity.runOnUiThread {
                                val list = activity.findViewById<RecyclerView>(R.id.list)
                                list.adapter = ThreadItemAdapter(
                                    activity,
                                    threadListContent,
                                )
                                list.layoutManager = LinearLayoutManager(activity)
                                var mDistance = 0
//                                    list.addOnScrollListener(object : RecyclerView.OnScrollListener() {
//                                        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
//                                            mDistance += dy
//                                            try {
//                                                when {
//                                                    mDistance == 0 -> {
//                                                        actionBar.setBackgroundColor(Color.argb(0, 255, 255, 255))
//                                                        actionBar.findViewById<TextView>(R.id.appName).setTextColor(Color.rgb(255, 255, 255))
//                                                    }
//                                                    mDistance <= 204 -> {
//                                                        actionBar.setBackgroundColor(Color.argb((mDistance * 1f / 204 * 255).toInt(), 255, 255, 255))
//                                                        actionBar.findViewById<TextView>(R.id.appName)
//                                                            .setTextColor(Color.rgb(((204 - mDistance * 1f) / 204 * 255).toInt(), ((204 - mDistance * 1f) / 204 * 255).toInt(), ((204 - mDistance * 1f) / 204 * 255).toInt()))
//                                                    }
//                                                    else -> {
//                                                        actionBar.setBackgroundColor(Color.rgb(255, 255, 255))
//                                                        actionBar.findViewById<TextView>(R.id.appName).setTextColor(Color.rgb(0, 0, 0))
//                                                    }
//                                                }
//                                            } catch (exp: Exception) {
//                                                AppCenterLog.error("[TIF]", exp.toString())
//                                            }
//                                        }
//                                    })
//                                    activity.findViewById<LinearLayout>(R.id.LoadingAnimation)?.visibility = View.GONE
                            }
                        }
                    }
                }
            }*/
            fun processResult(result: String) {
                val startMill = System.currentTimeMillis()
                spinner.startAnimation(spinnerAnim)
                val parser = Jsoup.parse(result, "", Parser.xmlParser())
                val threadListContent = mutableListOf<ThreadItem>()
                parser.select("item").forEach { item ->
                    if (item.select("link").html().isNotEmpty()) {
                        threadListContent.add(
                            ThreadItem(
                                -1,
                                item.select("title").text(),
                                item.select("description").text().trim(),
                                item.select("author").text(),
                                item.select("pubDate").text(),
                                item.select("category").text(),
                                null,
                                null,
                                with(item.select("link").html()) {
                                    (this.substring(this.indexOf("tid=") + 4)).toInt()
                                }
                            )
                        )
                    }
                }
                list.layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
                val adapter = ThreadItemAdapter(it, threadListContent)
                list.adapter = adapter
                Timer().schedule(object : TimerTask() {
                    override fun run() {
                        it.runOnUiThread {
                            spinner.animate().alpha(0F).setDuration(1000).start()
                        }
                    }
                }, if (System.currentTimeMillis() > startMill + 1000) 0 else 1000 - System.currentTimeMillis() + startMill)
                /*var mDistance = 0
                    activity.findViewById<RecyclerView>(R.id.list).addOnScrollListener(object : RecyclerView.OnScrollListener() {
                        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                            mDistance += dy
                            try {
                                when {
                                    mDistance == 0 -> {
                                        actionBar.setBackgroundColor(Color.argb(0, 255, 255, 255))
                                        actionBar.findViewById<TextView>(R.id.appName).setTextColor(Color.rgb(255, 255, 255))
                                    }
                                    mDistance <= 204 -> {
                                        actionBar.setBackgroundColor(Color.argb((mDistance * 1f / 204 * 255).toInt(), 255, 255, 255))
                                        actionBar.findViewById<TextView>(R.id.appName)
                                            .setTextColor(Color.rgb(((204 - mDistance * 1f) / 204 * 255).toInt(), ((204 - mDistance * 1f) / 204 * 255).toInt(), ((204 - mDistance * 1f) / 204 * 255).toInt()))
                                    }
                                    else -> {
                                        actionBar.setBackgroundColor(Color.rgb(255, 255, 255))
                                        actionBar.findViewById<TextView>(R.id.appName).setTextColor(Color.rgb(0, 0, 0))
                                    }
                                }
                            } catch (exp: Exception) {
                                AppCenterLog.error("[TIF]", exp.toString())
                            }
                        }
                    })
                    activity.findViewById<LinearLayout>(R.id.LoadingAnimation)?.visibility = View.GONE*/
            }

            GlobalScope.launch {
                val data = NetUtils(it).retrievePage("http://www.ditiezu.com/?mod=rss")
                it.runOnUiThread {
                    it.findViewById<ViewGroup>(R.id.MainActivity).postDelayed({
                        when (data) {
                            /*                            "Failed Retrieved" -> {
                                Dialog.tip(
                                    resources.getString(R.string.login_tips),
                                    R.drawable.ic_baseline_close_24,
                                    R.color.danger,
                                    activity,
                                    activity.findViewById(R.id.MainActivity),
                                    Dialog.TIME_SHORT
                                )
                            } */
                            else -> {
                                processResult(data)
                            }
                        }
                    }, 0)
                }
            }
        }
    }
}