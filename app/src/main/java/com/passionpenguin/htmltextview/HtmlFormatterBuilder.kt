/*
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

import android.text.Html.ImageGetter

class HtmlFormatterBuilder {
    var html: String? = null
        private set
    var imageGetter: ImageGetter? = null
        private set
    var clickableTableSpan: ClickableTableSpan? = null
        private set
    var drawTableLinkSpan: DrawTableLinkSpan? = null
        private set
    var onClickATagListener: OnClickATagListener? = null
    var indent = 24.0f
        private set
    var isRemoveTrailingWhiteSpace = true
        private set

    fun setHtml(html: String?): HtmlFormatterBuilder {
        this.html = html
        return this
    }

    fun setImageGetter(imageGetter: ImageGetter?): HtmlFormatterBuilder {
        this.imageGetter = imageGetter
        return this
    }

    fun setClickableTableSpan(clickableTableSpan: ClickableTableSpan?): HtmlFormatterBuilder {
        this.clickableTableSpan = clickableTableSpan
        return this
    }

    fun setDrawTableLinkSpan(drawTableLinkSpan: DrawTableLinkSpan?): HtmlFormatterBuilder {
        this.drawTableLinkSpan = drawTableLinkSpan
        return this
    }

    fun setIndent(indent: Float): HtmlFormatterBuilder {
        this.indent = indent
        return this
    }

    fun setRemoveTrailingWhiteSpace(removeTrailingWhiteSpace: Boolean): HtmlFormatterBuilder {
        isRemoveTrailingWhiteSpace = removeTrailingWhiteSpace
        return this
    }
}