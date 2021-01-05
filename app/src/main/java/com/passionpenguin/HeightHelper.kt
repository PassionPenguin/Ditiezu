/*
 * ==================================================
 * =  PROJECT     地下铁的故事
 * =  MODULE      地下铁的故事.app
 * =  FILE NAME   HeightHelper.kt
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
import android.graphics.Rect
import android.graphics.drawable.ColorDrawable
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver.OnGlobalLayoutListener
import android.view.WindowManager
import android.widget.PopupWindow

class HeightHelper(private val mActivity: Activity) : PopupWindow(mActivity), OnGlobalLayoutListener {
    private val rootView: View = View(mActivity)
    private var listener: HeightListener? = null
    private var heightMax = 0 // Record the maximum height of the pop content area

    fun init(): HeightHelper {
        if (!isShowing) {
            val view = mActivity.window.decorView
            // Delay loading popupwindow, if not, error will be reported
            view.post { showAtLocation(view, Gravity.NO_GRAVITY, 0, 0) }
        }
        return this
    }

    fun setHeightListener(listener: HeightListener?): HeightHelper {
        this.listener = listener
        return this
    }

    override fun onGlobalLayout() {
        val rect = Rect()
        rootView.getWindowVisibleDisplayFrame(rect)
        if (rect.bottom > heightMax) {
            heightMax = rect.bottom
        }

        // The difference between the two is the height of the keyboard
        val keyboardHeight = heightMax - rect.bottom
        if (listener != null) {
            listener!!.onHeightChanged(keyboardHeight)
        }
    }

    interface HeightListener {
        fun onHeightChanged(height: Int)
    }

    init {
        // Basic configuration
        contentView = rootView

        // Monitor global Layout changes
        rootView.viewTreeObserver.addOnGlobalLayoutListener(this)
        setBackgroundDrawable(ColorDrawable(0))

        // Set width to 0 and height to full screen
        width = 0
        height = ViewGroup.LayoutParams.MATCH_PARENT

        // Set keyboard pop-up mode
        softInputMode = WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE
        inputMethodMode = INPUT_METHOD_NEEDED
    }
}