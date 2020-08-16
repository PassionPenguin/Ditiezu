/*
 * ==================================================
 * =  PROJECT     地下铁的故事
 * =  MODULE      地下铁的故事.app
 * =  FILE NAME   Helper.kt
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

package com.ditiezu.android.fragments.notifications

import android.app.Activity
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ditiezu.android.R
import com.ditiezu.android.adapters.NotificationItem
import com.ditiezu.android.adapters.NotificationItemAdapter
import com.passionpenguin.NetUtils
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.jsoup.Jsoup

fun helper(activity: Activity, url: String, listEl: RecyclerView, tips: TextView, tipsImage: ImageView) {

    tips.visibility = View.GONE
    tipsImage.visibility = View.GONE
    GlobalScope.launch {
        val s = NetUtils(activity).retrievePage(url)
        val parser = Jsoup.parse(s)
        when {
            s.contains("用户登录") -> {
                activity.runOnUiThread {
                    tips.text = activity.resources.getString(R.string.not_login)
                    tips.visibility = View.VISIBLE
                    tipsImage.visibility = View.VISIBLE
                }
            }
            parser.select(".emp").text().contains("暂时没有新提醒") -> {
                activity.runOnUiThread {
                    tips.text = activity.resources.getString(R.string.no_notification)
                    tips.visibility = View.VISIBLE
                    tipsImage.visibility = View.VISIBLE
                }
            }
            else -> {
                val list = mutableListOf<NotificationItem>()
                GlobalScope.launch {
                    parser.select("[notice]").forEach {
                        try {
                            var quote: String? = null
                            if (!it.select(".quote").isEmpty()) {
                                quote = it.select(".quote").text()
                                it.select(".quote").remove()
                            }
                            var page = "1"
                            var tid: String
                            with(it.select(".ntc_body a:last-child")) {
                                when {
                                    this.isEmpty() -> tid = "-1"
                                    this.attr("href").contains("findpost") -> {
                                        val result = NetUtils(activity).retrieveRedirect(this.attr("href"))
                                        tid = result?.get(0) ?: "1"
                                        page = result?.get(1) ?: "1"
                                    }
                                    this.attr("href").contains("thread-") -> {
                                        tid = this.attr("href").substring(
                                            this.attr("href").indexOf("thread-") + 7,
                                            this.attr("href").indexOf("-", this.attr("href").indexOf("thread-") + 7)
                                        )
                                        page = "1"
                                    }
                                    else -> tid = "-1"
                                }
                            }

                            list.add(
                                NotificationItem(
                                    if (it.select("img").attr("src").contains("systempm")) {
                                        "http://www.ditiezu.com/" + it.select("img").attr("src")
                                    } else it.select("img").attr("src"),
                                    it.select(".ntc_body").text(),
                                    quote,
                                    it.select("dt span")[0].text(),
                                    tid,
                                    page
                                )
                            )
                        } catch (ignored: Exception) {
                        }
                    }
                    activity.runOnUiThread {
                        listEl.adapter = NotificationItemAdapter(activity, list)
                        listEl.layoutManager = LinearLayoutManager(activity)
                    }
                }
            }
        }
    }
}