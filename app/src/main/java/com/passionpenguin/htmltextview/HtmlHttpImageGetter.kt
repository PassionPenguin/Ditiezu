/*
 * Copyright (C) 2014-2016 Dominik Schürmann <dominik@schuermann.eu>
 * Copyright (C) 2013 Antarix Tandon
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

import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.AsyncTask
import android.text.Html.ImageGetter
import android.util.Log
import android.view.View
import android.widget.TextView
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.io.InputStream
import java.lang.ref.WeakReference
import java.net.URI
import java.net.URL

class HtmlHttpImageGetter(textView: TextView) : ImageGetter {
    private var container: TextView = textView
    private var baseUri: URI? = null
    private var matchParentWidth = false
    private var compressImage = false
    private var qualityImage = 50

    init {
        matchParentWidth = false
    }

    override fun getDrawable(source: String): Drawable {
        val urlDrawable = UrlDrawable()

        // get the actual source
        val asyncTask = ImageGetterAsyncTask(
            urlDrawable,
            this,
            container,
            compressImage,
            qualityImage
        )
        asyncTask.execute(source)

        // return reference to URLDrawable which will asynchronously load the image specified in the src tag
        return urlDrawable
    }

    /**
     * Static inner [AsyncTask] that keeps a [WeakReference] to the [UrlDrawable]
     * and [HtmlHttpImageGetter].
     *
     *
     * This way, if the AsyncTask has a longer life span than the UrlDrawable,
     * we won't leak the UrlDrawable or the HtmlRemoteImageGetter.
     */
    private class ImageGetterAsyncTask(
        d: UrlDrawable,
        imageGetter: HtmlHttpImageGetter,
        container: View,
        compressImage: Boolean,
        qualityImage: Int
    ) : AsyncTask<String?, Void?, Drawable?>() {
        private val drawableReference: WeakReference<UrlDrawable> = WeakReference(d)
        private val imageGetterReference: WeakReference<HtmlHttpImageGetter> =
            WeakReference(imageGetter)
        private val containerReference: WeakReference<View> = WeakReference(container)
        private val resources: WeakReference<Resources?> = WeakReference(container.resources)
        private var source: String? = null
        private var scale = 0f
        private var compressImage = false
        private var qualityImage = 50
        override fun doInBackground(vararg params: String?): Drawable? {
            source = params[0]
            return if (resources.get() != null) {
                if (compressImage) {
                    fetchCompressedDrawable(resources.get(), source)
                } else {
                    fetchDrawable(resources.get(), source)
                }
            } else null
        }

        override fun onPostExecute(result: Drawable?) {
            if (result == null) {
                Log.w(
                    HtmlTextView.TAG,
                    "Drawable result is null! (source: $source)"
                )
                return
            }
            val urlDrawable = drawableReference.get() ?: return
            // set the correct bound according to the result from HTTP call
            urlDrawable.setBounds(
                0,
                0,
                (result.intrinsicWidth * scale).toInt(),
                (result.intrinsicHeight * scale).toInt()
            )

            // change the reference of the current drawable to the result from the HTTP call
            urlDrawable.drawable = result
            val imageGetter = imageGetterReference.get() ?: return
            // redraw the image by invalidating the container
            imageGetter.container.invalidate()
            // re-set text to fix images overlapping text
            imageGetter.container.text = imageGetter.container.text
        }

        /**
         * Get the Drawable from URL
         */
        fun fetchDrawable(
            res: Resources?,
            urlString: String?
        ): Drawable? {
            return try {
                val `is` = fetch(urlString)
                val drawable: Drawable = BitmapDrawable(res, `is`)
                scale = getScale(drawable)
                drawable.setBounds(
                    0,
                    0,
                    (drawable.intrinsicWidth * scale).toInt(),
                    (drawable.intrinsicHeight * scale).toInt()
                )
                drawable
            } catch (e: Exception) {
                null
            }
        }

        /**
         * Get the compressed image with specific quality from URL
         */
        fun fetchCompressedDrawable(
            res: Resources?,
            urlString: String?
        ): Drawable? {
            return try {
                val `is` = fetch(urlString)
                val original = BitmapDrawable(res, `is`).bitmap
                val out = ByteArrayOutputStream()
                original.compress(Bitmap.CompressFormat.JPEG, qualityImage, out)
                original.recycle()
                `is`!!.close()
                val decoded =
                    BitmapFactory.decodeStream(ByteArrayInputStream(out.toByteArray()))
                out.close()
                scale = getScale(decoded)
                val b = BitmapDrawable(res, decoded)
                b.setBounds(
                    0,
                    0,
                    (b.intrinsicWidth * scale).toInt(),
                    (b.intrinsicHeight * scale).toInt()
                )
                b
            } catch (e: Exception) {
                null
            }
        }

        private fun getScale(bitmap: Bitmap): Float {
            val container = containerReference.get() ?: return 1f
            val maxWidth = container.width.toFloat()
            val originalDrawableWidth = bitmap.width.toFloat()
            if (source?.contains("static/image")!!) {
                Log.i("", originalDrawableWidth.toString())
                return 2f
            }
            return if (maxWidth < originalDrawableWidth) maxWidth / originalDrawableWidth else 1f
        }

        private fun getScale(drawable: Drawable): Float {
            val container = containerReference.get() ?: /*!matchParentWidth || */return 1f
            val maxWidth = container.width.toFloat()
            val originalDrawableWidth = drawable.intrinsicWidth.toFloat()
            if (source?.contains("static/image")!!) {
                Log.i("", originalDrawableWidth.toString())
                return 2f
            }
            return if (maxWidth < originalDrawableWidth) maxWidth / originalDrawableWidth else 1f
        }

        @Throws(IOException::class)
        private fun fetch(urlString: String?): InputStream? {
            val url: URL
            val imageGetter = imageGetterReference.get() ?: return null
            url = if (imageGetter.baseUri != null) {
                imageGetter.baseUri!!.resolve(urlString).toURL()
            } else {
                URI.create(urlString).toURL()
            }
            return url.content as InputStream
        }

        init {
            this.compressImage = compressImage
            this.qualityImage = qualityImage
        }
    }

    inner class UrlDrawable : BitmapDrawable() {
        var drawable: Drawable? = null
        override fun draw(canvas: Canvas) {
            // override the draw to facilitate refresh function later
            if (drawable != null) {
                drawable!!.draw(canvas)
            }
        }
    }
}