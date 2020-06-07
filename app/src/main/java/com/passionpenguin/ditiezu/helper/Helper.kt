package com.passionpenguin.ditiezu.helper

import android.content.res.ColorStateList
import android.graphics.drawable.Drawable
import androidx.core.graphics.drawable.DrawableCompat


fun tintDrawable(
    drawable: Drawable?,
    colors: ColorStateList?
): Drawable? {
    val wrappedDrawable = DrawableCompat.wrap(drawable!!)
    DrawableCompat.setTintList(wrappedDrawable, colors)
    return wrappedDrawable
}