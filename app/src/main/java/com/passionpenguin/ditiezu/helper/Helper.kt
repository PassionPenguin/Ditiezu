package com.passionpenguin.ditiezu.helper

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
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

class Preference(activity: Activity) {
    private val sp: SharedPreferences =
        activity.getPreferences(Context.MODE_PRIVATE)

    fun edit(key: String, value: Any) {
        with(sp.edit()) {
            when (value) {
                is String -> this.putString(key, value)
                is Int -> this.putInt(key, value)
                is Long -> this.putLong(key, value)
                is Float -> this.putFloat(key, value)
            }
            this.commit()
        }
    }

    fun getString(key: String): String? {
        return sp.getString(key, "")
    }

    fun getInt(key: String): Int? {
        return sp.getInt(key, 0)
    }

    fun getLong(key: String): Long? {
        return sp.getLong(key, 0)
    }

    fun getFloat(key: String): Float? {
        return sp.getFloat(key, 0f)
    }
}