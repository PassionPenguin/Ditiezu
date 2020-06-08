/*
 * Copyright (C) 2013 Dominik Schürmann <dominik@schuermann.eu>
 * Copyright (C) 2012 Pierre-Yves Ricau <py.ricau@gmail.com>
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

import android.annotation.SuppressLint
import android.content.Context
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.util.AttributeSet
import android.util.Log
import java.util.*

/**
 *
 *
 * A [android.widget.TextView] that insert spaces around its text spans where needed to prevent
 * [IndexOutOfBoundsException] in [.onMeasure] on Jelly Bean.
 *
 *
 * When [.onMeasure] throws an exception, we try to fix the text by adding spaces
 * around spans, until it works again. We then try removing some of the added spans, to minimize the
 * insertions.
 *
 *
 * The fix is time consuming (a few ms, it depends on the size of your text), but it should only
 * happen once per text change.
 *
 *
 * See http://code.google.com/p/android/issues/detail?id=35466
 *
 *
 * From https://gist.github.com/pyricau/3424004 with fix from comments
 */
open class JellyBeanSpanFixTextView : androidx.appcompat.widget.AppCompatTextView {
    private class FixingResult private constructor(
        val fixed: Boolean, val spansWithSpacesBefore: List<Any>?,
        val spansWithSpacesAfter: List<Any>?
    ) {

        companion object {
            fun fixed(
                spansWithSpacesBefore: List<Any>?,
                spansWithSpacesAfter: List<Any>?
            ): FixingResult {
                return FixingResult(true, spansWithSpacesBefore, spansWithSpacesAfter)
            }

            fun notFixed(): FixingResult {
                return FixingResult(false, null, null)
            }
        }

    }

    constructor(
        context: Context?,
        attrs: AttributeSet?,
        defStyle: Int
    ) : super(context, attrs, defStyle) {
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(
        context,
        attrs
    ) {
    }

    constructor(context: Context?) : super(context) {}

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        try {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        } catch (e: IndexOutOfBoundsException) {
            fixOnMeasure(widthMeasureSpec, heightMeasureSpec)
        }
    }

    /**
     * If possible, fixes the Spanned text by adding spaces around spans when needed.
     */
    private fun fixOnMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val text = text
        if (text is Spanned) {
            val builder = SpannableStringBuilder(text)
            fixSpannedWithSpaces(builder, widthMeasureSpec, heightMeasureSpec)
        } else {
            if (HtmlTextView.DEBUG) {
                Log.d(HtmlTextView.TAG, "The text isn't a Spanned")
            }
            fallbackToString(widthMeasureSpec, heightMeasureSpec)
        }
    }

    /**
     * Add spaces around spans until the text is fixed, and then removes the unneeded spaces
     */
    private fun fixSpannedWithSpaces(
        builder: SpannableStringBuilder, widthMeasureSpec: Int,
        heightMeasureSpec: Int
    ) {
        val startFix = System.currentTimeMillis()
        val result = addSpacesAroundSpansUntilFixed(
            builder, widthMeasureSpec,
            heightMeasureSpec
        )
        if (result.fixed) {
            removeUnneededSpaces(widthMeasureSpec, heightMeasureSpec, builder, result)
        } else {
            fallbackToString(widthMeasureSpec, heightMeasureSpec)
        }
        if (HtmlTextView.DEBUG) {
            val fixDuration = System.currentTimeMillis() - startFix
            Log.d(
                HtmlTextView.TAG,
                "fixSpannedWithSpaces() duration in ms: $fixDuration"
            )
        }
    }

    private fun addSpacesAroundSpansUntilFixed(
        builder: SpannableStringBuilder,
        widthMeasureSpec: Int, heightMeasureSpec: Int
    ): FixingResult {
        val spans =
            builder.getSpans(0, builder.length, Any::class.java)
        val spansWithSpacesBefore: MutableList<Any> =
            ArrayList(spans.size)
        val spansWithSpacesAfter: MutableList<Any> =
            ArrayList(spans.size)
        for (span in spans) {
            val spanStart = builder.getSpanStart(span)
            if (isNotSpace(builder, spanStart - 1)) {
                builder.insert(spanStart, " ")
                spansWithSpacesBefore.add(span)
            }
            val spanEnd = builder.getSpanEnd(span)
            if (isNotSpace(builder, spanEnd)) {
                builder.insert(spanEnd, " ")
                spansWithSpacesAfter.add(span)
            }
            try {
                setTextAndMeasure(builder, widthMeasureSpec, heightMeasureSpec)
                return FixingResult.fixed(spansWithSpacesBefore, spansWithSpacesAfter)
            } catch (ignored: IndexOutOfBoundsException) {
            }
        }
        if (HtmlTextView.DEBUG) {
            Log.d(
                HtmlTextView.TAG,
                "Could not fix the Spanned by adding spaces around spans"
            )
        }
        return FixingResult.notFixed()
    }

    private fun isNotSpace(text: CharSequence, where: Int): Boolean {
        return where < 0 || where >= text.length || text[where] != ' '
    }

    @SuppressLint("WrongCall")
    private fun setTextAndMeasure(
        text: CharSequence,
        widthMeasureSpec: Int,
        heightMeasureSpec: Int
    ) {
        setText(text)
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
    }

    @SuppressLint("WrongCall")
    private fun removeUnneededSpaces(
        widthMeasureSpec: Int, heightMeasureSpec: Int,
        builder: SpannableStringBuilder, result: FixingResult
    ) {
        for (span in result.spansWithSpacesAfter!!) {
            val spanEnd = builder.getSpanEnd(span)
            builder.delete(spanEnd, spanEnd + 1)
            try {
                setTextAndMeasure(builder, widthMeasureSpec, heightMeasureSpec)
            } catch (ignored: IndexOutOfBoundsException) {
                builder.insert(spanEnd, " ")
            }
        }
        var needReset = true
        for (span in result.spansWithSpacesBefore!!) {
            val spanStart = builder.getSpanStart(span)
            builder.delete(spanStart - 1, spanStart)
            try {
                setTextAndMeasure(builder, widthMeasureSpec, heightMeasureSpec)
                needReset = false
            } catch (ignored: IndexOutOfBoundsException) {
                needReset = true
                val newSpanStart = spanStart - 1
                builder.insert(newSpanStart, " ")
            }
        }
        if (needReset) {
            text = builder
            super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        }
    }

    private fun fallbackToString(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        if (HtmlTextView.DEBUG) {
            Log.d(HtmlTextView.TAG, "Fallback to unspanned text")
        }
        val fallbackText = text.toString()
        setTextAndMeasure(fallbackText, widthMeasureSpec, heightMeasureSpec)
    }
}