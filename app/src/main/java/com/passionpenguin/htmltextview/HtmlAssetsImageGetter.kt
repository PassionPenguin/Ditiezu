/*
 * Copyright (C) 2016 Daniel Passos <daniel@passos.me>
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
import android.graphics.drawable.Drawable
import android.text.Html.ImageGetter
import android.util.Log
import android.widget.TextView
import com.passionpenguin.htmltextview.HtmlTextView
import java.io.IOException

/**
 * Assets Image Getter
 *
 *
 * Load image from assets folder
 *
 * @author [Daniel Passos](mailto:daniel@passos.me)
 */
class HtmlAssetsImageGetter : ImageGetter {
    private val context: Context

    constructor(context: Context) {
        this.context = context
    }

    constructor(textView: TextView) {
        context = textView.context
    }

    override fun getDrawable(source: String): Drawable? {
        return try {
            val inputStream = context.assets.open(source)
            val d =
                Drawable.createFromStream(inputStream, null)
            d.setBounds(0, 0, d.intrinsicWidth, d.intrinsicHeight)
            d
        } catch (e: IOException) {
            // prevent a crash if the resource still can't be found
            Log.e(HtmlTextView.TAG, "source could not be found: $source")
            null
        }
    }
}