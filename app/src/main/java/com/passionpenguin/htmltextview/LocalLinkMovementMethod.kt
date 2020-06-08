/*
 * Copyright (C) 2015 Heliangwei
 *
 * Converted from Java to Kotlin
 * Fixed some mistakes.
 * Copyright (C) 2020 @PassionPenguin
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.passionpenguin.htmltextview

import android.text.Selection
import android.text.Spannable
import android.text.method.LinkMovementMethod
import android.text.method.Touch
import android.text.style.ClickableSpan
import android.view.MotionEvent
import android.widget.TextView

/**
 * Copied from http://stackoverflow.com/questions/8558732
 */
class LocalLinkMovementMethod : LinkMovementMethod() {
    override fun onTouchEvent(
        widget: TextView,
        buffer: Spannable,
        event: MotionEvent
    ): Boolean {
        val action = event.action
        if (action == MotionEvent.ACTION_UP ||
            action == MotionEvent.ACTION_DOWN
        ) {
            var x = event.x.toInt()
            var y = event.y.toInt()
            x -= widget.totalPaddingLeft
            y -= widget.totalPaddingTop
            x += widget.scrollX
            y += widget.scrollY
            val layout = widget.layout
            val line = layout.getLineForVertical(y)
            val off = layout.getOffsetForHorizontal(line, x.toFloat())
            val link =
                buffer.getSpans(off, off, ClickableSpan::class.java)
            return if (link.isNotEmpty()) {
                if (action == MotionEvent.ACTION_UP) {
                    link[0].onClick(widget)
                } else if (action == MotionEvent.ACTION_DOWN) {
                    Selection.setSelection(
                        buffer,
                        buffer.getSpanStart(link[0]),
                        buffer.getSpanEnd(link[0])
                    )
                }
                true
            } else {
                Selection.removeSelection(buffer)
                Touch.onTouchEvent(widget, buffer, event)
                false
            }
        }
        return Touch.onTouchEvent(widget, buffer, event)
    }

    companion object {
        private var sInstance: LocalLinkMovementMethod? = null
        @JvmStatic
        val instance: LocalLinkMovementMethod?
            get() {
                if (sInstance == null) sInstance =
                    LocalLinkMovementMethod()
                return sInstance
            }
    }
}