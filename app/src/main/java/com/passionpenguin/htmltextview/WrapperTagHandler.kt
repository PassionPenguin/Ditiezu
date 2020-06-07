package com.passionpenguin.htmltextview

import android.text.Editable
import org.xml.sax.Attributes

interface WrapperTagHandler {
    fun handleTag(
        opening: Boolean,
        tag: String?,
        output: Editable?,
        attributes: Attributes?
    ): Boolean
}