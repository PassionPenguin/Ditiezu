/*
 * ==================================================
 * =  PROJECT     地下铁的故事
 * =  MODULE      地下铁的故事.app
 * =  FILE NAME   AccountActivity.kt
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

package com.ditiezu.android.views.main

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.animation.AnimationUtils
import android.webkit.CookieManager
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.ditiezu.android.Editor
import com.ditiezu.android.Editor.Companion.TYPE_SIGHTML
import com.ditiezu.android.LoginActivity
import com.ditiezu.android.R
import com.passionpenguin.*
import kotlinx.android.synthetic.main.activity_account.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.jsoup.Jsoup

@SuppressLint("SetTextI18n")
class AccountActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_account)
    }

    override fun onResume() {
        super.onResume()
        onLoad()
    }

    private fun onLoad() {
        notLoginTips.visibility = View.GONE
        userCenter.visibility = View.GONE
        val spinnerAnim = with(AnimationUtils.loadAnimation(this, R.anim.rotate)) {
            this.fillAfter = true
            this
        }
        spinner.fadeIn()
        spinner.startAnimation(spinnerAnim)

        GlobalScope.launch {
            val loginState = NetUtils(this@AccountActivity).checkLogin()

            runOnUiThread {
                if (loginState) {
                    notLoginTips.visibility = View.GONE
                    userCenter.visibility = View.VISIBLE
                    if (personalPrefList.childCount == 0)
                        GlobalScope.launch {
                            val ct = NetUtils(this@AccountActivity).retrievePage("http://www.ditiezu.com/home.php?mod=space")
                            val parser = Jsoup.parse(ct)
                            val formhash = parser.select("[name='formhash']")[0].attr("value")

                            val absUrl = parser.select("strong a").attr("href")
                            val id = absUrl.substring(33, absUrl.lastIndexOf(".html"))

                            var metaList = parser.select(".pbm.mbm.bbda.cl:first-child ul > li:last-child")[0]
                            parser.select(".pbm.mbm.bbda.cl:first-child ul > li").forEach { el ->
                                if (el.html().contains("统计信息")) metaList = el
                            }
                            val prefItem = mutableListOf<PrefListItem>()
                            prefItem.add(PrefListItem(resources.getString(R.string.user_id), "", id, false) {})
                            prefItem.add(PrefListItem(resources.getString(R.string.online_hour), resources.getString(R.string.online_hour_description), parser.select("#pbbs>:first-child").textNodes()[0].text(), false) {})
                            prefItem.add(PrefListItem(resources.getString(R.string.signature), "", "", true) {
                                val i = Intent(this@AccountActivity, Editor::class.java)
                                i.putExtra("type", TYPE_SIGHTML)
                                startActivity(i)
                            })
                            prefItem.add(PrefListItem(
                                resources.getString(R.string.logout), resources.getString(
                                    R.string.logout_description
                                ), "", true
                            ) {
                                SPHelper(this@AccountActivity).edit("login", "false")
                                GlobalScope.launch {
                                    NetUtils(this@AccountActivity).retrievePage("http://www.ditiezu.com/member.php?mod=logging&action=logout&formhash=$formhash")
                                    CookieManager.getInstance().removeAllCookies(null)
                                    runOnUiThread {
                                        recreate()
                                    }
                                }
                                val i = Intent(this@AccountActivity, LoginActivity::class.java)
                                i.putExtra("redirected", true)
                                startActivity(i)
                            })

                            runOnUiThread {
                                userName.text = parser.select("h2.mbn")[0].childNodes()[0].outerHtml().trim()
                                Glide.with(this@AccountActivity)
                                    .load("http://www.ditiezu.com/uc_server/avatar.php?mod=avatar&uid=$id")
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

                                prefItem.forEach { item -> personalPrefList.addView(prefView(this@AccountActivity, item.name, item.description, item.value, item.toggle, item.execFunc)) }
                            }
                        }
                } else {
                    notLoginTips.visibility = View.VISIBLE
                    button.setOnClickListener { _ ->
                        startActivity(Intent(this@AccountActivity, LoginActivity::class.java))
                    }
                }
                spinner.fadeOut()
            }
        }
    }
}