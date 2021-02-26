package io.hoarfroster.ditiezu.layouts

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import android.widget.TextView
import io.hoarfroster.ditiezu.R

@SuppressLint("ViewConstructor")
class TabItem constructor(
    context: Context,
    title: String,
    callback: OnClickListener
) : LinearLayout(context) {

    private val root = LayoutInflater.from(context)
        .inflate(R.layout.layout_tab_item, this, true)

    init {
        root.setOnClickListener(callback)
        root.findViewById<TextView>(R.id.tabTitle).text = title

        orientation = VERTICAL
        layoutParams = LayoutParams(
            LayoutParams.MATCH_PARENT,
            LayoutParams.MATCH_PARENT,
            1.0f
        )
    }
}