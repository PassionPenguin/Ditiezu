/*
 * Copyright (C) 2013-2014 Dominik Schürmann <dominik@schuermann.eu>
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

import android.content.Context
import android.text.Html.ImageGetter
import android.util.AttributeSet
import androidx.annotation.RawRes
import com.passionpenguin.htmltextview.HtmlFormatter.formatHtml
import com.passionpenguin.htmltextview.LocalLinkMovementMethod.Companion.instance
import java.io.InputStream
import java.util.*

class HtmlTextView : JellyBeanSpanFixTextView {
    private var clickableTableSpan: ClickableTableSpan? = null
    private var drawTableLinkSpan: DrawTableLinkSpan? = null
    private var onClickATagListener: OnClickATagListener? = null
    private var indent = 24.0f // Default to 24px.
    private var removeTrailingWhiteSpace = true

    constructor(
        context: Context?,
        attrs: AttributeSet?,
        defStyle: Int
    ) : super(context, attrs, defStyle)

    constructor(context: Context?, attrs: AttributeSet?) : super(
        context,
        attrs
    )

    constructor(context: Context?) : super(context)

    /**
     * @see com.passionpenguin.htmltextview.HtmlTextView.setHtml
     */
    fun setHtml(@RawRes resId: Int) {
        setHtml(resId, null)
    }

    /**
     * @see com.passionpenguin.htmltextview.HtmlTextView.setHtml
     */
    fun setHtml(html: String) {
        setHtml(html, null)
    }

    /**
     * Loads HTML from a raw resource, i.e., a HTML file in res/raw/.
     * This allows translatable resource (e.g., res/raw-de/ for german).
     * The containing HTML is parsed to Android's Spannable format and then displayed.
     *
     * @param resId       for example: R.raw.help
     * @param imageGetter for fetching images. Possible ImageGetter provided by this library:
     * HtmlLocalImageGetter and HtmlRemoteImageGetter
     */
    fun setHtml(@RawRes resId: Int, imageGetter: ImageGetter?) {
        val inputStreamText =
            context.resources.openRawResource(resId)
        setHtml(convertStreamToString(inputStreamText), imageGetter)
    }

    /**
     * Parses String containing HTML to Android's Spannable format and displays it in this TextView.
     * Using the implementation of Html.ImageGetter provided.
     *
     * @param html        String containing HTML, for example: "**Hello world!**"
     * @param imageGetter for fetching images. Possible ImageGetter provided by this library:
     * HtmlLocalImageGetter and HtmlRemoteImageGetter
     */
    fun setHtml(html: String, imageGetter: ImageGetter?) {
        text = formatHtml(
            context,
            html,
            imageGetter,
            clickableTableSpan,
            drawTableLinkSpan,
            onClickATagListener,
            indent,
            removeTrailingWhiteSpace
        )

        // make links work
        movementMethod = instance
    }

//    /**
//     * The Html.fromHtml method has the behavior of adding extra whitespace at the bottom
//     * of the parsed HTML displayed in for example a TextView. In order to remove this
//     * whitespace call this method before setting the text with setHtml on this TextView.
//     *
//     * @param removeTrailingWhiteSpace true if the whitespace rendered at the bottom of a TextView
//     * after setting HTML should be removed.
//     */
//    fun setRemoveTrailingWhiteSpace(removeTrailingWhiteSpace: Boolean) {
//        this.removeTrailingWhiteSpace = removeTrailingWhiteSpace
//    }

    /**
     * The Html.fromHtml method has the behavior of adding extra whitespace at the bottom
     * of the parsed HTML displayed in for example a TextView. In order to remove this
     * whitespace call this method before setting the text with setHtml on this TextView.
     *
     *
     * This method is deprecated, use setRemoveTrailingWhiteSpace instead.
     *
     * @param removeFromHtmlSpace true if the whitespace rendered at the bottom of a TextView
     * after setting HTML should be removed.
     */
    @Deprecated("")
    fun setRemoveFromHtmlSpace(removeFromHtmlSpace: Boolean) {
        removeTrailingWhiteSpace = removeFromHtmlSpace
    }

//    fun setClickableTableSpan(clickableTableSpan: ClickableTableSpan?) {
//        this.clickableTableSpan = clickableTableSpan
//    }
//
//    fun setDrawTableLinkSpan(drawTableLinkSpan: DrawTableLinkSpan?) {
//        this.drawTableLinkSpan = drawTableLinkSpan
//    }
//
//    fun setOnClickATagListener(onClickATagListener: OnClickATagListener?) {
//        this.onClickATagListener = onClickATagListener
//    }
//
//    /**
//     * Add ability to increase list item spacing. Useful for configuring spacing based on device
//     * screen size. This applies to ordered and unordered lists.
//     *
//     * @param px pixels to indent.
//     */
//    fun setListIndentPx(px: Float) {
//        indent = px
//    }

    companion object {
        const val TAG = "HtmlTextView"
        const val DEBUG = false

        /**
         * http://stackoverflow.com/questions/309424/read-convert-an-inputstream-to-a-string
         */
        private fun convertStreamToString(`is`: InputStream): String {
            val s = Scanner(`is`).useDelimiter("\\A")
            return if (s.hasNext()) s.next() else ""
        }
    }
}