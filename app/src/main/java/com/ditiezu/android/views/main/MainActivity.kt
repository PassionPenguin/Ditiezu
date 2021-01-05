/*
 * ==================================================
 * =  PROJECT     地下铁的故事
 * =  MODULE      地下铁的故事.app
 * =  FILE NAME   MainActivity.kt
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

import android.os.Bundle
import android.util.Log
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LifecycleObserver
import com.ditiezu.android.R
import com.ditiezu.android.adapters.ThreadItem
import com.ditiezu.android.adapters.ThreadItemAdapter
import com.passionpenguin.NetUtils
import com.passionpenguin.fadeIn
import com.passionpenguin.fadeOut
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.jsoup.Jsoup
import org.jsoup.parser.Parser
import java.util.*

class MainActivity : AppCompatActivity(), LifecycleObserver {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    override fun onPostResume() {
        super.onPostResume()
        GlobalScope.launch {
            val data = NetUtils(this@MainActivity).retrievePage("http://www.ditiezu.com/?mod=rss")
            runOnUiThread {
                processResult(data)
            }
        }
    }

    fun processResult(result: String) {
        val spinnerAnim = with(AnimationUtils.loadAnimation(this, R.anim.rotate)) {
            this.fillAfter = true
            this
        }
        val startMill = System.currentTimeMillis()
        spinner.fadeIn()
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
        Log.i("SIZE", threadListContent.size.toString())
        val adapter = ThreadItemAdapter(this, threadListContent, true)
        list.adapter = adapter
        spinner.postDelayed({
            spinner.fadeOut()
        }, if (System.currentTimeMillis() > startMill + 1000) 0 else 1000 - System.currentTimeMillis() + startMill)
    }
}