/*
 * ==================================================
 * =  PROJECT     地下铁的故事
 * =  MODULE      地下铁的故事.app
 * =  FILE NAME   PopupWindow.kt
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
import android.graphics.Color
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.PopupWindow
import android.widget.TextView
import com.ditiezu.android.R

abstract class PopupWindow(val activity: Activity, title: String, description: String) {
    init {
        activity.runOnUiThread {
            val window = activity.window
            val target = window.decorView.findViewById<ViewGroup>(android.R.id.content)
            val popupContentView: View = LayoutInflater.from(activity).inflate(R.layout.fragment_dialog, target, false)
            val popupWindow = PopupWindow(
                popupContentView,
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT,
                true
            )

            popupWindow.showAtLocation(target, Gravity.NO_GRAVITY, 0, 0)
            popupWindow.animationStyle = android.R.style.Animation_Dialog
            val icon = popupContentView.findViewById<ImageView>(R.id.app_icon)
//            Glide.with(icon).load(R.drawable.startup_icon).apply(RequestOptions.bitmapTransform(RoundedCorners(4))).into(icon)
            popupContentView.findViewById<TextView>(R.id.dialog_name).text = title
            popupContentView.findViewById<TextView>(R.id.CancelButton).setTextColor(activity.resources.getColor(R.color.black, null))
            popupContentView.findViewById<TextView>(R.id.dialogDescription).text = description
            popupContentView.findViewById<TextView>(R.id.CancelButton).setOnClickListener {
                popupWindow.dismiss()
                window.statusBarColor = Color.TRANSPARENT
            }
            popupContentView.findViewById<TextView>(R.id.ConfirmButton).setOnClickListener {
                onSubmit(popupWindow, popupContentView.findViewById(R.id.dialog_content))
                popupWindow.dismiss()
                window.statusBarColor = Color.TRANSPARENT
            }
            popupWindow.update()
            initContent(popupWindow, popupContentView.findViewById(R.id.dialog_content))
        }
    }

    abstract fun initContent(window: PopupWindow, root: ViewGroup)
    abstract fun onSubmit(window: PopupWindow, root: ViewGroup)
}