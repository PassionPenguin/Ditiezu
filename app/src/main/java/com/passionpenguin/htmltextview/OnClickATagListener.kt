package com.passionpenguin.htmltextview

import android.view.View

/**
 * This listener can define what happens when the a tag is clicked
 */
interface OnClickATagListener {
    fun onClick(widget: View?, href: String?)
}