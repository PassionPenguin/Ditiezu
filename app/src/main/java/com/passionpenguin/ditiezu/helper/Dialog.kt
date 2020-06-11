package com.passionpenguin.ditiezu.helper

import android.app.Activity
import android.graphics.Color
import android.view.*
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

    fun create(
        activity: Activity,
        target: ViewGroup,
        request: String,
        title: String,
        description: String,
        action: (layout: ViewGroup, popupWindow: PopupWindow) -> Unit
    ) {
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