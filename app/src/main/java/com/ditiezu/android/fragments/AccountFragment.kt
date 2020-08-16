/*
 * ==================================================
 * =  PROJECT     地下铁的故事
 * =  MODULE      地下铁的故事.app
 * =  FILE NAME   AccountFragment.kt
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

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.CookieManager
import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.ditiezu.android.R
import com.ditiezu.android.LoginActivity
import com.passionpenguin.NetUtils
import com.passionpenguin.PrefListItem
import com.passionpenguin.SPHelper
import com.ditiezu.android.Editor
import com.ditiezu.android.Editor.Companion.TYPE_SIGHTML
import com.passionpenguin.prefView
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.jsoup.Jsoup

@SuppressLint("SetTextI18n")
class AccountFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_account, container, false)
    }

    private fun onLoad() {
        activity?.let {
            it.findViewById<ConstraintLayout>(R.id.not_login_tips).visibility = View.GONE
            it.findViewById<ScrollView>(R.id.userCenter).visibility = View.GONE
            it.findViewById<ImageView>(R.id.spinner).visibility = View.VISIBLE
            it.findViewById<ImageView>(R.id.spinner).alpha = 1F

            GlobalScope.launch {
                val loginState = NetUtils(it).checkLogin()

                it.runOnUiThread {
                    if (loginState) {
                        it.findViewById<ConstraintLayout>(R.id.not_login_tips).visibility = View.GONE
                        it.findViewById<ScrollView>(R.id.userCenter).visibility = View.VISIBLE
                        if (it.findViewById<LinearLayout>(R.id.personal_pref_list).childCount == 0)
                            GlobalScope.launch {
                                val ct = NetUtils(it).retrievePage("http://www.ditiezu.com/home.php?mod=space")
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
                                    val i = Intent(context, Editor::class.java)
                                    i.putExtra("type", TYPE_SIGHTML)
                                    it.startActivity(i)
                                })
                                prefItem.add(PrefListItem(
                                    resources.getString(R.string.logout), resources.getString(
                                        R.string.logout_description
                                    ), "", true
                                ) {
                                    SPHelper(it).edit("login", "false")
                                    GlobalScope.launch {
                                        NetUtils(it).retrievePage("http://www.ditiezu.com/member.php?mod=logging&action=logout&formhash=$formhash")
                                        CookieManager.getInstance().removeAllCookies(null)
                                        it.runOnUiThread {
                                            it.recreate()
                                        }
                                    }
                                    val i = Intent(activity, LoginActivity::class.java)
                                    i.putExtra("redirected", true)
                                    startActivity(i)
                                })

                                it.runOnUiThread {
                                    it.findViewById<TextView>(R.id.userName).text = parser.select("h2.mbn")[0].childNodes()[0].outerHtml().trim()
                                    context?.let { mCtx ->
                                        Glide.with(mCtx)
                                            .load("http://www.ditiezu.com/uc_server/avatar.php?mod=avatar&uid=$id")
                                            .placeholder(R.mipmap.noavatar_middle)
                                            .error(R.mipmap.noavatar_middle)
                                            .apply(RequestOptions.bitmapTransform(RoundedCorners(8)))
                                            .into(it.findViewById(R.id.avatar))
                                    }
                                    it.findViewById<TextView>(R.id.value_level).text = parser.select(".pbm span a")[0].text()

                                    try {
                                        it.findViewById<TextView>(R.id.value_friends).text = metaList.select("a")[0].text().trim().substring(4)
                                    } catch (e: Exception) {
                                        it.findViewById<TextView>(R.id.value_friends).text = "N/A"
                                    }
                                    try {
                                        it.findViewById<TextView>(R.id.value_replies).text = metaList.select("a")[1].text().trim().substring(4)
                                    } catch (e: Exception) {
                                        it.findViewById<TextView>(R.id.value_replies).text = "N/A"
                                    }
                                    try {
                                        it.findViewById<TextView>(R.id.value_threads).text = metaList.select("a")[2].text().trim().substring(4)
                                    } catch (e: Exception) {
                                        it.findViewById<TextView>(R.id.value_threads).text = "N/A"
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
                                    val progressBar = it.findViewById<ProgressBar>(R.id.progressBar)
                                    progressBar.max = max - min
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
                                        progressBar.setProgress(pts - min, true)
                                    else progressBar.progress = pts - min
                                    it.findViewById<TextView>(R.id.level).text = parser.select(".pbm span a")[0].text()
                                    it.findViewById<TextView>(R.id.points).text = "$pts / $max"

                                    it.findViewById<TextView>(R.id.value_points).text = pts.toString()
                                    it.findViewById<TextView>(R.id.value_prestige).text = parser.select("#psts li")[1].textNodes()[0].text().trim()
                                    it.findViewById<TextView>(R.id.value_money).text = parser.select("#psts li")[2].textNodes()[0].text().trim()
                                    it.findViewById<TextView>(R.id.value_m_score).text = parser.select("#psts li")[3].textNodes()[0].text().trim()
                                    it.findViewById<TextView>(R.id.value_popularity).text = parser.select("#psts li")[4].textNodes()[0].text().trim()

                                    prefItem.forEach { item -> it.findViewById<LinearLayout>(R.id.personal_pref_list).addView(context?.let { ctx -> prefView(ctx, item.name, item.description, item.value, item.toggle, item.execFunc) }) }
                                }
                            }
                    } else {
                        it.findViewById<ConstraintLayout>(R.id.not_login_tips).visibility = View.VISIBLE
                        it.findViewById<Button>(R.id.button)?.setOnClickListener { _ ->
                            it.startActivity(Intent(activity, LoginActivity::class.java))
                        }
                    }
                    it.findViewById<ImageView>(R.id.spinner).animate().alpha(0F).setDuration(1000).start()
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        onLoad()
    }
}