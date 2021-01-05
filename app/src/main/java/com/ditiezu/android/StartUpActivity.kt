/*
 * ==================================================
 * =  PROJECT     地下铁的故事
 * =  MODULE      地下铁的故事.app
 * =  FILE NAME   StartUpActivity.kt
 * =  LAST MODIFIED BY PASSIONPENGUIN [1/5/21, 6:39 PM]
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
import android.view.animation.AnimationUtils
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.ditiezu.android.views.main.MainActivity
import com.passionpenguin.SPHelper
import java.util.*

class StartUpActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start_up)
        SPHelper(this).edit("followed_category", "")
        println(SPHelper(this).getString("followed_category"))

        findViewById<ImageView>(R.id.spinner).startAnimation(with(AnimationUtils.loadAnimation(this, R.anim.rotate)) {
            this.duration = 1000
            this.fillAfter = true
            this
        })

        Timer().schedule(object : TimerTask() {
            override fun run() {
                startActivity(Intent(this@StartUpActivity, MainActivity::class.java))
            }
        }, 2500)
    }
}