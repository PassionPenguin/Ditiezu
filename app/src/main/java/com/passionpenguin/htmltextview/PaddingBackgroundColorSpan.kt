/*
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

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.text.style.LineBackgroundSpan
import kotlin.math.roundToInt

class PaddingBackgroundColorSpan(private val mBackgroundColor: Int, private val mPadding: Int) :
    LineBackgroundSpan {
    private val mBgRect: Rect = Rect()
    override fun drawBackground(
        c: Canvas,
        p: Paint,
        left: Int,
        right: Int,
        top: Int,
        baseline: Int,
        bottom: Int,
        text: CharSequence,
        start: Int,
        end: Int,
        lnum: Int
    ) {
        val textWidth = p.measureText(text, start, end).roundToInt()
        val paintColor = p.color
        // Draw the background
        mBgRect[left - mPadding, top - (if (lnum == 0) mPadding / 2 else -(mPadding / 2)), left + textWidth + mPadding] =
            bottom + mPadding / 2
        p.color = mBackgroundColor
        c.drawRect(mBgRect, p)
        p.color = paintColor
    }
}