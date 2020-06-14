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

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.provider.Browser
import android.text.style.ClickableSpan
import android.util.Log
import android.view.View
import com.passionpenguin.ditiezu.ViewThread

open class CustomURLSpan(
    var URL: String?
) : ClickableSpan() {

    override fun onClick(widget: View) {
        val uri = Uri.parse(URL)
        val context = widget.context
        val href = uri.toString()

        // IN BBS-THREAD REDIRECT
        if (href.contains("ditiezu.com") && (href.contains("mod=viewthread") || href.contains("thread-"))) {
            val i = Intent(context, ViewThread::class.java)
            val tid = if (href.contains("mod=viewthread")) href.substring(
                href.indexOf("tid=") + 4,
                href.indexOf("&", href.indexOf("tid=") + 4)
            ) else href.substring(
                href.indexOf("thread-") + 7,
                href.indexOf("-", href.indexOf("thread-") + 7)
            )

            i.putExtra("tid", tid.toInt())
            val page = when {
                (href.contains("thread-")) -> href.substring(
                    href.indexOf("-", href.indexOf("thread-") + 7) + 1,
                    href.indexOf("-", href.indexOf("-", href.indexOf("thread-") + 7) + 1)
                )
                (href.contains("page=")) -> href.substring(
                    href.indexOf(
                        "page=" + 5,
                        href.indexOf("&", href.indexOf("page") + 5)
                    )
                )
                else -> "1"
            }
            i.putExtra("page", page.toInt())
            i.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            context.startActivity(i)
        }
        // NON BBS-THREAD REDIRECT
        else {
            val intent = Intent(Intent.ACTION_VIEW, uri)
            intent.putExtra(Browser.EXTRA_APPLICATION_ID, context.packageName)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            try {
                context.startActivity(intent)
            } catch (e: ActivityNotFoundException) {
                Log.w("URLSpan", "Actvity was not found for intent, $intent")
            }
        }
    }
}