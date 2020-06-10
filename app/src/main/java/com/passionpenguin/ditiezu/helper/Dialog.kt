package com.passionpenguin.ditiezu.helper

import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.view.*
import android.widget.LinearLayout
import android.widget.PopupWindow
import android.widget.TextView
import com.passionpenguin.ditiezu.R

class Dialog {
    fun create(
        activity: Activity,
        target: View,
        context: Context,
        title: String,
        description: String,
        action: () -> Unit
    ) {
        val popupContentView: View =
            LayoutInflater.from(context).inflate(R.layout.fragment_dialog, null)
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

        popupWindow.showAtLocation(target, Gravity.CENTER, 0, 0);
        popupWindow.animationStyle = android.R.style.Animation_Dialog
        popupContentView.findViewById<TextView>(R.id.dialogName).text = title
        popupContentView.findViewById<TextView>(R.id.CancelButton)
            .setTextColor(context.resources.getColor(R.color.black, null))
        popupContentView.findViewById<TextView>(R.id.dialogDescription).text = description
        popupContentView.findViewById<TextView>(R.id.ConfirmButton)
            .setTextColor(context.resources.getColor(R.color.primary500, null))
        popupContentView.findViewById<TextView>(R.id.CancelButton).setOnClickListener {
            popupWindow.dismiss()
            window.statusBarColor = Color.TRANSPARENT
        }
        popupContentView.findViewById<LinearLayout>(R.id.background).setOnClickListener {
            popupWindow.dismiss()
            window.statusBarColor = Color.TRANSPARENT
        }
        popupContentView.findViewById<TextView>(R.id.ConfirmButton).setOnClickListener {
            action()
            popupWindow.dismiss()
        }
        popupWindow.update()
    }
}