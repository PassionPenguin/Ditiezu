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

import android.content.Context
import android.content.Intent
import android.text.style.ClickableSpan
import android.view.View
import com.passionpenguin.ditiezu.ZoomImage

class CustomIMGSpan(
    private val context: Context?,
    private val url: String?
) : ClickableSpan() {
    override fun onClick(widget: View) {
        if (!url?.contains("static/image/smiley")!!) {
            val intent = Intent(context, ZoomImage::class.java)
            intent.putExtra("filePath", url)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            context!!.startActivity(intent)
        }
    }
}