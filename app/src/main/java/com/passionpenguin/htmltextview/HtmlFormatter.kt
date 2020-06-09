/*
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
import android.text.Html
import android.text.Html.ImageGetter
import android.text.Spanned

object HtmlFormatter {
    fun formatHtml(builder: HtmlFormatterBuilder): Spanned? {
        return formatHtml(
            builder.context,
            builder.html,
            builder.imageGetter,
            builder.clickableTableSpan,
            builder.drawTableLinkSpan,
            builder.onClickATagListener,
            builder.indent,
            builder.isRemoveTrailingWhiteSpace
        )
    }

    @JvmStatic
    fun formatHtml(
        context: Context?,
        html: String?,
        imageGetter: ImageGetter?,
        clickableTableSpan: ClickableTableSpan?,
        drawTableLinkSpan: DrawTableLinkSpan?,
        onClickATagListener: OnClickATagListener?,
        indent: Float,
        removeTrailingWhiteSpace: Boolean
    ): Spanned? {
        val htmlTagHandler = HtmlTagHandler(context)
        htmlTagHandler.setClickableTableSpan(clickableTableSpan)
        htmlTagHandler.setDrawTableLinkSpan(drawTableLinkSpan)
        htmlTagHandler.setOnClickATagListener(onClickATagListener)
        htmlTagHandler.setListIndentPx(indent)
        val h = htmlTagHandler.overrideTags(html)
        val formattedHtml: Spanned?
        formattedHtml = if (removeTrailingWhiteSpace) {
            removeHtmlBottomPadding(
                Html.fromHtml(
                    h,
                    imageGetter,
                    WrapperContentHandler(htmlTagHandler)
                )
            )
        } else {
            Html.fromHtml(h, imageGetter, WrapperContentHandler(htmlTagHandler))
        }
        return formattedHtml
    }

    /**
     * Html.fromHtml sometimes adds extra space at the bottom.
     * This methods removes this space again.
     * See https://github.com/SufficientlySecure/html-textview/issues/19
     */
    private fun removeHtmlBottomPadding(text: Spanned?): Spanned? {
        var t = text ?: return null
        while (t.isNotEmpty() && t[t.length - 1] == '\n') {
            t = t.subSequence(0, t.length - 1) as Spanned
        }
        return t
    }
}