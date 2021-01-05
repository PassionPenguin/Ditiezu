/*
 * ==================================================
 * =  PROJECT     地下铁的故事
 * =  MODULE      地下铁的故事.app
 * =  FILE NAME   Alert.kt
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

package com.passionpenguin

import android.app.Activity
import android.content.res.ColorStateList
import android.view.*
import android.widget.ImageView
import android.widget.PopupWindow
import android.widget.TextView
import com.ditiezu.android.R
import kotlin.properties.Delegates

class Alert(val activity: Activity, text: String) {

    var view by Delegates.notNull<View>()
    var popupWindow by Delegates.notNull<PopupWindow>()
    var window by Delegates.notNull<Window>()

    init {
        activity.runOnUiThread {
            window = activity.window
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            val target = window.decorView.findViewById<ViewGroup>(android.R.id.content)
            view = LayoutInflater.from(activity).inflate(R.layout.fragment_tips, target, false)
            popupWindow = PopupWindow(view, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, true)
            popupWindow.showAtLocation(target, Gravity.TOP, 0, 0)
            view.alpha = 0F
            view.animate().alpha(1F).setDuration(250).start()
            view.postDelayed({
                view.animate().alpha(0F).setDuration(250).start()
                view.postDelayed({ popupWindow.dismiss() }, 250)
            }, 1000)
            view.findViewById<TextView>(R.id.text).text = text
        }
    }

    fun success() {
        view.findViewById<ImageView>(R.id.icon).setImageResource(R.drawable.ic_baseline_check_24)
        view.findViewById<ImageView>(R.id.icon).backgroundTintList = ColorStateList.valueOf(activity.resources.getColor(R.color.success, null))
    }

    fun error() {
        view.findViewById<ImageView>(R.id.icon).setImageResource(R.drawable.ic_baseline_close_24)
        view.findViewById<ImageView>(R.id.icon).backgroundTintList = ColorStateList.valueOf(activity.resources.getColor(R.color.danger, null))
    }
}