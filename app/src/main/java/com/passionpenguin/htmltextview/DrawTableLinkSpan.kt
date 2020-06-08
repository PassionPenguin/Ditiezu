/*
 * Copyright (C) 2016 Richard Thai
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

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Paint.FontMetricsInt
import android.text.style.ReplacementSpan

/**
 * This span defines how a table should be rendered in the HtmlTextView. The default implementation
 * is a cop-out which replaces the HTML table with some text ("[tap for table]" is the default).
 *
 *
 * This is to be used in conjunction with the ClickableTableSpan which will redirect a click to the
 * text some application-defined action (i.e. render the raw HTML in a WebView).
 */
class DrawTableLinkSpan : ReplacementSpan() {
    private var tableLinkText =
        DEFAULT_TABLE_LINK_TEXT
    var textSize = DEFAULT_TEXT_SIZE
    var textColor = DEFAULT_TEXT_COLOR

    // This sucks, but we need this so that each table can get drawn.
    // Otherwise, we end up with the default table link text (nothing) for earlier tables.
    fun newInstance(): DrawTableLinkSpan {
        val drawTableLinkSpan = DrawTableLinkSpan()
        drawTableLinkSpan.tableLinkText = tableLinkText
        drawTableLinkSpan.textSize = textSize
        drawTableLinkSpan.textColor = textColor
        return drawTableLinkSpan
    }

    override fun getSize(
        paint: Paint,
        text: CharSequence,
        start: Int,
        end: Int,
        fm: FontMetricsInt?
    ): Int {
        val width = paint.measureText(tableLinkText, 0, tableLinkText.length).toInt()
        textSize = paint.textSize
        return width
    }

    override fun draw(
        canvas: Canvas,
        text: CharSequence,
        start: Int,
        end: Int,
        x: Float,
        top: Int,
        y: Int,
        bottom: Int,
        paint: Paint
    ) {
        val paint2 = Paint()
        paint2.style = Paint.Style.STROKE
        paint2.color = textColor
        paint2.isAntiAlias = true
        paint2.textSize = textSize
        canvas.drawText(tableLinkText, x, bottom.toFloat(), paint2)
    }

    companion object {
        private const val DEFAULT_TABLE_LINK_TEXT = ""
        private const val DEFAULT_TEXT_SIZE = 80f
        private const val DEFAULT_TEXT_COLOR = Color.BLUE
    }
}