/*
 * ==================================================
 * =  PROJECT     地下铁的故事
 * =  MODULE      地下铁的故事.app
 * =  FILE NAME   AttachImageView.kt
 * =  LAST MODIFIED BY PASSIONPENGUIN [8/14/20 1:41 AM]
 * ==================================================
 * Copyright 2020 PassionPenguin. All rights reserved.
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
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import androidx.core.graphics.scale
import com.bumptech.glide.Glide
import com.ditiezu.android.R
import java.io.IOException

fun attachImageView(mCtx: Activity, url: String): View {
    val layoutInflater: LayoutInflater = LayoutInflater.from(mCtx)
    val view: View = layoutInflater.inflate(R.layout.item_image, null)

    fun loadBitmap(URL: String, returnVal: (bitmap: Bitmap?) -> Unit) {
        var bitmap: Bitmap?
        Thread {
            try {
                NetUtils(mCtx).openHttpUrlConn(URL) {
                    bitmap = BitmapFactory.decodeStream(it)
                    val height = bitmap!!.height
                    val width = bitmap!!.width
                    val size = mCtx.resources.getDimension(R.dimen._360)
                    bitmap = if (height > width) bitmap!!.scale((size * width / height).toInt(), size.toInt()) else bitmap!!.scale(size.toInt(), (size * height / width).toInt())
                    returnVal(bitmap)
                }
            } catch (ignored: IOException) {
            }
        }.start()
    }
    loadBitmap("http://www.ditiezu.com/$url") {
        mCtx.runOnUiThread {
            Glide.with(mCtx).load(it).into(view.findViewById(R.id.image))
        }
    }
    return view
}