/*
 * ==================================================
 * =  PROJECT     地下铁的故事
 * =  MODULE      地下铁的故事.app
 * =  FILE NAME   UserProfile.kt
 * =  LAST MODIFIED BY PASSIONPENGUIN [1/5/21, 9:25 PM]
 * ==================================================
 * Copyright 2021 PassionPenguin. All rights reserved.
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

package com.ditiezu.android

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.passionpenguin.*
import kotlinx.android.synthetic.main.activity_account.*
import kotlinx.android.synthetic.main.fragment_add_friend.*
import kotlinx.android.synthetic.main.fragment_add_friend.view.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.jsoup.Jsoup
import java.net.URLEncoder

class UserProfile : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_account)

        val uid = intent.extras?.getInt("uid") ?: -1
        if (uid == -1) onBackPressed()

        this.let {
            notLoginTips.visibility = View.GONE
            userCenter.visibility = View.GONE
            spinner.startAnimation(with(AnimationUtils.loadAnimation(it, R.anim.rotate)) {
                this.fillAfter = true
                this
            })
            spinner.fadeIn()
            notLoginTips.visibility = View.GONE
            userCenter.visibility = View.VISIBLE
            if (personalPrefList.childCount == 0)
                GlobalScope.launch {
                    val ct = NetUtils(it).retrievePage("http://www.ditiezu.com/space-uid-$uid.html")
                    val parser = Jsoup.parse(ct)
                    val formhash = parser.select("[name='formhash']").attr("value")

                    var metaList = parser.select(".pbm.mbm.bbda.cl:first-child ul > li:last-child")[0]
                    parser.select(".pbm.mbm.bbda.cl:first-child ul > li").forEach { el ->
                        if (el.html().contains("统计信息")) metaList = el
                    }
                    val prefItem = mutableListOf<PrefListItem>()
                    prefItem.add(PrefListItem(resources.getString(R.string.user_id), "", uid.toString(), false) {})
                    prefItem.add(PrefListItem(resources.getString(R.string.online_hour), resources.getString(R.string.online_hour_description), parser.select("#pbbs>:first-child").textNodes()[0].text(), false) {})

                    it.runOnUiThread {
                        if (parser.select("#lsform").isEmpty()) {
                            friendCtrl.visibility = View.VISIBLE
                            if (parser.select(".ul_add").isEmpty())
                                runOnUiThread {
                                    friendCtrl.text = resources.getString(R.string.remove_friend_title)
                                }
                            friendCtrl.setOnClickListener {
                                if (parser.select(".ul_add").isNotEmpty()) {
                                    object : PopupWindow(this@UserProfile, resources.getString(R.string.add_friend), resources.getString(R.string.add_friend_description, parser.select("h2.mbn")[0].childNodes()[0].outerHtml().trim())) {
                                        override fun initContent(window: android.widget.PopupWindow, root: ViewGroup) {
                                            val i = LayoutInflater.from(this@UserProfile).inflate(R.layout.fragment_add_friend, root, false)
                                            root.addView(i)

                                            ArrayAdapter.createFromResource(this@UserProfile, R.array.friend_group, R.layout.spinner_dropdown_item).also { adapter ->
                                                // Specify the layout to use when the list of choices appears
                                                adapter.setDropDownViewResource(R.layout.spinner_dropdown_item)
                                                // Apply the adapter to the spinner
                                                userGroupList.adapter = adapter
                                            }
                                        }

                                        override fun onSubmit(window: android.widget.PopupWindow, root: ViewGroup) {
                                            GlobalScope.launch {
                                                with(NetUtils(this@UserProfile).postPage("http://www.ditiezu.com/home.php?mod=spacecp&ac=friend&op=add&uid=$uid&inajax=1", "referer=http%3A%2F%2Fwww.ditiezu.com%2Fspace-uid-$uid.html&addsubmit=true&handlekey=a_friend_li_$uid&formhash=$formhash&note=${URLEncoder.encode(root.reason.text.toString(), "GBK")}&gid=${root.userGroupList.selectedItemPosition}")) {
                                                    if (this != "")
                                                        Alert(this@UserProfile, resources.getString(R.string.request_sent)).success()
                                                }
                                            }
                                        }
                                    }
                                } else {
                                    object : PopupWindow(this@UserProfile, resources.getString(R.string.remove_friend), resources.getString(R.string.remove_friend_description, parser.select("h2.mbn")[0].childNodes()[0].outerHtml().trim())) {
                                        override fun initContent(window: android.widget.PopupWindow, root: ViewGroup) {
                                        }

                                        override fun onSubmit(window: android.widget.PopupWindow, root: ViewGroup) {
                                            GlobalScope.launch {
                                                with(NetUtils(this@UserProfile).postPage("http://www.ditiezu.com/home.php?mod=spacecp&ac=friend&op=ignore&uid=$uid&confirm=1&inajax=1", "referer=http%3A%2F%2Fwww.ditiezu.com%2Fspace-uid-$uid.html&friendsubmit=true&formhash=$formhash&from=&handlekey=a_ignore_$uid")) {
                                                    if (this != "")
                                                        when {
                                                            this.contains("成功") -> {
                                                                friendCtrl.text = resources.getString(R.string.add_friend_title)
                                                                Alert(this@UserProfile, this.substring(53, this.indexOf("<", 53)).trim()).success()
                                                            }
                                                            this.contains("失败") -> Alert(this@UserProfile, this.substring(53, this.indexOf("<", 53)).trim()).error()
                                                        }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        userName.text = parser.select("h2.mbn")[0].childNodes()[0].outerHtml().trim()
                        Glide.with(it)
                            .load("http://www.ditiezu.com/uc_server/avatar.php?mod=avatar&uid=$uid")
                            .placeholder(R.mipmap.noavatar_middle)
                            .error(R.mipmap.noavatar_middle)
                            .apply(RequestOptions.bitmapTransform(RoundedCorners(8)))
                            .into(avatar)
                        valueLevel.text = parser.select(".pbm span a")[0].text()

                        try {
                            valueFriends.text = metaList.select("a")[0].text().trim().substring(4)
                        } catch (e: Exception) {
                            valueFriends.text = "N/A"
                        }
                        try {
                            valueReplies.text = metaList.select("a")[1].text().trim().substring(4)
                        } catch (e: Exception) {
                            valueReplies.text = "N/A"
                        }
                        try {
                            valueThreads.text = metaList.select("a")[2].text().trim().substring(4)
                        } catch (e: Exception) {
                            valueThreads.text = "N/A"
                        }


                        val pts = parser.select("#psts li")[0].textNodes()[0].text().trim().toInt()
                        val max = when (pts) {
                            0 -> 0
                            in 1..49 -> 50
                            in 50..199 -> 200
                            in 200..499 -> 500
                            in 500..999 -> 1000
                            in 1000..2999 -> 3000
                            in 3000..4999 -> 5000
                            in 5000..9999 -> 10000
                            in 10000..19999 -> 20000
                            in 20000..50000 -> 50000
                            else -> 0
                        }
                        val min = when (pts) {
                            0 -> 0
                            in 1..49 -> 0
                            in 50..199 -> 50
                            in 200..499 -> 200
                            in 500..999 -> 500
                            in 1000..2999 -> 1000
                            in 3000..4999 -> 3000
                            in 5000..9999 -> 5000
                            in 10000..19999 -> 10000
                            in 20000..50000 -> 20000
                            else -> 0
                        }
                        progressBar.max = max - min
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
                            progressBar.setProgress(pts - min, true)
                        else progressBar.progress = pts - min
                        level.text = parser.select(".pbm span a")[0].text()
                        points.text = "$pts / $max"

                        valuePoints.text = pts.toString()
                        valuePrestige.text = parser.select("#psts li")[1].textNodes()[0].text().trim()
                        valueMoney.text = parser.select("#psts li")[2].textNodes()[0].text().trim()
                        valueMScore.text = parser.select("#psts li")[3].textNodes()[0].text().trim()
                        valuePopularity.text = parser.select("#psts li")[4].textNodes()[0].text().trim()

                        prefItem.forEach { item -> personalPrefList.addView(prefView(it, item.name, item.description, item.value, item.toggle, item.execFunc)) }
                    }
                }
            spinner.fadeOut()
        }
    }
}