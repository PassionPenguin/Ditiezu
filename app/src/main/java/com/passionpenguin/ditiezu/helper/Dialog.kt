package com.passionpenguin.ditiezu.helper

import android.app.Activity
import android.content.res.ColorStateList
import android.graphics.Color
import android.view.*
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.PopupWindow
import android.widget.TextView
import com.passionpenguin.ditiezu.R

class Dialog {
    fun create(
        activity: Activity,
        target: ViewGroup,
        request: String,
        title: String,
        description: String,
        action: (layout: ViewGroup, popupWindow: PopupWindow) -> Unit,
        promise: (layout: LinearLayout, popupWindow: PopupWindow) -> Unit = { _, _ -> }
    ) {
        activity.runOnUiThread {
            val popupContentView: View =
                LayoutInflater.from(activity).inflate(R.layout.fragment_dialog, target, false)
            val popupWindow = PopupWindow(
                popupContentView,
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT,
                true
            )
            val window = activity.window
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.statusBarColor = Color.argb(192, 123, 123, 123)

            popupWindow.showAtLocation(target, Gravity.CENTER, 0, 0)
            popupWindow.animationStyle = android.R.style.Animation_Dialog
            popupContentView.findViewById<TextView>(R.id.dialog_request).text = request
            popupContentView.findViewById<TextView>(R.id.dialog_name).text = title
            popupContentView.findViewById<TextView>(R.id.CancelButton)
                .setTextColor(activity.resources.getColor(R.color.black, null))
            popupContentView.findViewById<TextView>(R.id.dialogDescription).text = description
            popupContentView.findViewById<TextView>(R.id.CancelButton).setOnClickListener {
                popupWindow.dismiss()
                window.statusBarColor = Color.TRANSPARENT
            }
            popupContentView.findViewById<TextView>(R.id.ConfirmButton).setOnClickListener {
                action(popupContentView as ViewGroup, popupWindow)
                popupWindow.dismiss()
                window.statusBarColor = Color.TRANSPARENT
            }
            popupWindow.update()
            promise(popupContentView.findViewById(R.id.dialog_content), popupWindow)
        }
    }

    fun create(
        activity: Activity,
        target: ViewGroup,
        request: String,
        title: String,
        description: String,
        action: (layout: ViewGroup, popupWindow: PopupWindow) -> Unit
    ) {
        activity.runOnUiThread {
            val popupContentView: View =
                LayoutInflater.from(activity).inflate(R.layout.fragment_dialog, target, false)
            val popupWindow = PopupWindow(
                popupContentView,
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT,
                true
            )
            val window = activity.window
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.statusBarColor = Color.argb(192, 123, 123, 123)

            popupWindow.showAtLocation(target, Gravity.CENTER, 0, 0)
            popupWindow.animationStyle = android.R.style.Animation_Dialog
            popupContentView.findViewById<TextView>(R.id.dialog_request).text = request
            popupContentView.findViewById<TextView>(R.id.dialog_name).text = title
            popupContentView.findViewById<TextView>(R.id.CancelButton)
                .setTextColor(activity.resources.getColor(R.color.black, null))
            popupContentView.findViewById<TextView>(R.id.dialogDescription).text = description
            popupContentView.findViewById<TextView>(R.id.CancelButton).setOnClickListener {
                popupWindow.dismiss()
                window.statusBarColor = Color.TRANSPARENT
            }
            popupContentView.findViewById<TextView>(R.id.ConfirmButton).setOnClickListener {
                action(popupContentView as ViewGroup, popupWindow)
                popupWindow.dismiss()
                window.statusBarColor = Color.TRANSPARENT
            }
            popupWindow.update()
        }
    }

    fun tip(
        text: String,
        iconResource: Int,
        iconColor: Int,
        activity: Activity,
        target: ViewGroup,
        length: Long
    ) {
        activity.runOnUiThread {
            val view = LayoutInflater.from(activity).inflate(R.layout.fragment_tips, target, false)
            view.findViewById<ImageView>(R.id.icon).setImageResource(iconResource)
            view.findViewById<ImageView>(R.id.icon).backgroundTintList =
                ColorStateList.valueOf(activity.resources.getColor(iconColor, null))
            view.findViewById<TextView>(R.id.text).text = text
            val popupWindow = PopupWindow(
                view,
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT,
                true
            )
            val window = activity.window
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            popupWindow.showAtLocation(target, Gravity.TOP, 0, 0)
            view.postDelayed({
                popupWindow.dismiss()
            }, length)
        }
    }

    companion object {
        val TIME_SHORT: Long get() = 1000
        val TIME_LONG: Long get() = 2000
    }
}