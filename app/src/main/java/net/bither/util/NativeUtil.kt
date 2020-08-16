/*
 * ==================================================
 * =  PROJECT     地下铁的故事
 * =  MODULE      地下铁的故事.app
 * =  FILE NAME   NativeUtil.kt
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
package net.bither.util

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Rect
import android.util.Log

object NativeUtil {
    fun compressBitmap(bit: Bitmap, fileName: String, optimize: Boolean, quality: Int = 75) {
        compressBitmap(bit, quality, fileName, optimize)
    }

    private fun compressBitmap(bit: Bitmap, quality: Int, fileName: String, optimize: Boolean) {
        Log.d("native", "compress of native")
        if (bit.config != Bitmap.Config.ARGB_8888) {
            val result: Bitmap = Bitmap.createBitmap(bit.width, bit.height, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(result)
            val rect = Rect(0, 0, bit.width, bit.height)
            canvas.drawBitmap(bit, null, rect, null)
            saveBitmap(result, quality, fileName, optimize)
            result.recycle()
        } else {
            saveBitmap(bit, quality, fileName, optimize)
        }
    }

    private fun saveBitmap(bit: Bitmap, quality: Int, fileName: String, optimize: Boolean) {
        compressBitmap(bit, bit.width, bit.height, quality, fileName.toByteArray(), optimize)
    }

    private external fun compressBitmap(bit: Bitmap, w: Int, h: Int, quality: Int, fileNameBytes: ByteArray, optimize: Boolean): String?

    init {
        System.loadLibrary("jpegbither")
        System.loadLibrary("bitherjni")
    }
}