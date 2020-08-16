/*
 * ==================================================
 * =  PROJECT     地下铁的故事
 * =  MODULE      地下铁的故事.app
 * =  FILE NAME   Helper.kt
 * =  LAST MODIFIED BY PASSIONPENGUIN [8/14/20 1:41 AM]
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

package com.passionpenguin

import android.app.Activity
import android.content.res.ColorStateList
import android.graphics.drawable.Drawable
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.core.graphics.drawable.DrawableCompat
import com.ditiezu.android.R
import java.util.*

class LoadingButton(parent: LinearLayout, private val activity: Activity) {
    private val spinner = parent.findViewById<ImageView>(R.id.spinner)!!
    fun onLoading() {
        spinner.visibility = View.VISIBLE
        spinner.animate().alpha(1F).setDuration(250).start()
        spinner.startAnimation(with(AnimationUtils.loadAnimation(activity, R.anim.rotate)) {
            this.duration = 1000
            this.fillAfter = true
            this
        })
    }

    fun onLoaded() {
        spinner.visibility = View.GONE
        spinner.animate().alpha(0F).setDuration(475).start()
        Timer().schedule(object : TimerTask() {
            override fun run() {
                spinner.visibility = View.GONE
                spinner.clearAnimation()
            }
        }, 500)
    }
}

fun tintDrawable(
    drawable: Drawable?,
    colors: ColorStateList?
): Drawable? {
    val wrappedDrawable = DrawableCompat.wrap(drawable!!)
    DrawableCompat.setTintList(wrappedDrawable, colors)
    return wrappedDrawable
}