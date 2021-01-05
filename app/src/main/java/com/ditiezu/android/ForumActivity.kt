/*
 * ==================================================
 * =  PROJECT     地下铁的故事
 * =  MODULE      地下铁的故事.app
 * =  FILE NAME   ForumActivity.kt
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

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentPagerAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import com.bumptech.glide.Glide
import com.ditiezu.android.Editor.Companion.TYPE_NEW
import com.ditiezu.android.adapters.ThreadItem
import com.ditiezu.android.adapters.ThreadItemAdapter
import com.ditiezu.android.data.categoryList
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.tabs.TabLayout
import com.passionpenguin.NetUtils
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.jsoup.Jsoup
import java.util.*

class ForumActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forum)

        val id: Int = intent.extras?.getInt("ID") ?: -1
        if (id == -1) onBackPressed()
        val data = categoryList[id]

        findViewById<FloatingActionButton>(R.id.fab).setOnClickListener {
            val i = Intent(this, Editor::class.java)
            i.putExtra("type", TYPE_NEW)
            i.putExtra("fid", data.ID)
            startActivity(i)
        }

        Glide.with(this).load(data.icon).into(findViewById(R.id.icon))
        findViewById<TextView>(R.id.name).text = data.name
        findViewById<TextView>(R.id.description).text = data.description
        val spinner = findViewById<ImageView>(R.id.spinner)
        val spinnerAnim = with(AnimationUtils.loadAnimation(this, R.anim.rotate)) {
            this.duration = 1000
            this.fillAfter = true
            this
        }
        spinner.startAnimation(spinnerAnim)

        GlobalScope.launch {
            try {
                val html = NetUtils(this@ForumActivity).retrievePage("http://www.ditiezu.com/forum.php?mod=forumdisplay&fid=${data.ID}")
                val parser = Jsoup.parse(html)

                class PostTag(val id: Int, val name: String)

                val tags = mutableListOf<PostTag>()
                parser.select("#thread_types li:not(.fold):not(#ttp_all) a").forEach {
                    tags.add(PostTag(with(it.attr("href")) {
                        this.substring(this.indexOf("typeid=") + 7).toInt()
                    }, it.text()))
                }

                runOnUiThread {
                    class Adapter : FragmentPagerAdapter(supportFragmentManager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
                        override fun getCount(): Int {
                            return tags.size + 1
                        }

                        override fun getPageTitle(position: Int): CharSequence? {
                            return if (position == 0) "全部" else tags[position - 1].name
                        }

                        override fun getItem(position: Int): Fragment {
                            class DataFragment : Fragment() {
                                override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
                                    return inflater.inflate(R.layout.forum_content, container, false)
                                }

                                override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
                                    val list = view.findViewById<RecyclerView>(R.id.post_list)
                                    try {
                                        super.onViewCreated(view, savedInstanceState)

                                        list.layoutManager = LinearLayoutManager(activity)

                                        fun resultProcessor(html: String) {
                                            val startMill = System.currentTimeMillis()
                                            val threadListContent = mutableListOf<ThreadItem>()
                                            Jsoup.parse(html).select("[id^='normalthread_']").forEach {
                                                val author = it.select(".by cite a")[0]
                                                val authorName = author.text()
                                                val time = it.select(".by:not(.kmhf) em span").text()
                                                val views = it.select(".num em").text()
                                                val replies = it.select(".num a").text()
                                                val tagName = with(it.select("em:first-child a")) { if (isNotEmpty()) this.text() else null }
                                                val title = it.select(".xst")
                                                var targetId: Int
                                                with(title.attr("href")) {
                                                    targetId = if (this.contains(".html")) this.substring(30, this.lastIndexOf(".html") - 4).toInt()
                                                    else this.substring(52, this.indexOf("&", 52)).toInt()
                                                }
                                                threadListContent.add(
                                                    ThreadItem(
                                                        author.attr("href").substring(author.attr("href").indexOf("uid-") + 4, author.attr("href").indexOf(".html")).toInt(),
                                                        title.text(), "", authorName, time, tagName, views, replies, targetId
                                                    )
                                                )
                                                val adapter = ThreadItemAdapter(
                                                    this@ForumActivity,
                                                    threadListContent,
                                                )
                                                runOnUiThread {
                                                    list.adapter = adapter
                                                    Timer().schedule(object : TimerTask() {
                                                        override fun run() {
                                                            runOnUiThread {
                                                                spinner.animate().alpha(0F).setDuration(1000).start()
                                                            }
                                                        }
                                                    }, if (System.currentTimeMillis() > startMill + 1000) 0 else 1000 - System.currentTimeMillis() + startMill)
                                                }
                                            }
                                        }
                                        if (position == 0)
                                            resultProcessor(html)
                                        else GlobalScope.launch {
                                            resultProcessor(NetUtils(this@ForumActivity).retrievePage("http://www.ditiezu.com/forum.php?mod=forumdisplay&fid=${data.ID}&filter=typeid&typeid=${tags[position - 1].id}"))
                                        }
                                    } catch (e: Exception) {
                                        println(e)
                                    }
                                }
                            }
                            return DataFragment()
                        }
                    }

                    val sectionsPagerAdapter = Adapter()
                    val viewPager: ViewPager = findViewById(R.id.view_pager)
                    viewPager.adapter = sectionsPagerAdapter
                    val tabs: TabLayout = findViewById(R.id.tabs)
                    tabs.setupWithViewPager(viewPager)
                }
            } catch (e: Exception) {
                println(e)
            }
        }
    }
}