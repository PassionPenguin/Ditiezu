package com.passionpenguin.ditiezu.helper

import android.content.Context
import android.view.*
import android.widget.PopupWindow
import android.widget.TextView
import com.passionpenguin.ditiezu.R


class Dialog {
    fun create(
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
            ViewGroup.LayoutParams.WRAP_CONTENT,
            true
        )
        popupWindow.showAtLocation(target, Gravity.CENTER, 0, 0);
        popupWindow.animationStyle = android.R.style.Animation_Dialog
        popupContentView.findViewById<TextView>(R.id.dialogName).text = title
        popupContentView.findViewById<TextView>(R.id.CancelButton).setTextColor(context.resources.getColor(R.color.black, null))
        popupContentView.findViewById<TextView>(R.id.dialogDescription).text = description
        popupContentView.findViewById<TextView>(R.id.ConfirmButton).setTextColor(context.resources.getColor(R.color.primary500, null))
        popupContentView.findViewById<TextView>(R.id.CancelButton).setOnClickListener {
            popupWindow.dismiss()
        }
        popupContentView.findViewById<TextView>(R.id.ConfirmButton).setOnClickListener {
            action()
            popupWindow.dismiss()
        }
        popupWindow.update()
    }
}