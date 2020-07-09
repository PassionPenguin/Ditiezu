package com.passionpenguin.ditiezu.helper

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.content.res.ColorStateList
import android.graphics.drawable.Drawable
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.core.graphics.drawable.DrawableCompat
import com.passionpenguin.ditiezu.R
import java.util.*

class LoadingButton(parent: LinearLayout, private val activity: Activity) {
    private val spinner = parent.findViewById<ImageView>(R.id.spinner)!!
    fun onLoading() {
        spinner.visibility = View.VISIBLE
        spinner.animate().alpha(1F).setDuration(250).start()
        spinner.startAnimation(with(AnimationUtils.loadAnimation(activity, R.anim.rotate)) {
            this.duration = 1000
            this.fillAfter = true
            this
        })
    }

    fun onLoaded() {
        spinner.visibility = View.GONE
        spinner.animate().alpha(0F).setDuration(475).start()
        Timer().schedule(object : TimerTask() {
            override fun run() {
                spinner.visibility = View.GONE
                spinner.clearAnimation()
            }
        }, 500)
    }
}

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
                is Boolean -> this.putBoolean(key, value)
            }
            this.commit()
        }
    }

    fun getString(key: String): String? {
        return sp.getString(key, "")
    }

    fun getBoolean(key: String): Boolean? {
        return sp.getBoolean(key, false)
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