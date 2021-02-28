package io.hoarfroster.ditiezu.layouts

import android.content.Context
import android.content.Intent
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import io.hoarfroster.ditiezu.App
import io.hoarfroster.ditiezu.R

class ShortcutItem @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0,
    defStyleRes: Int = 0
) : LinearLayout(context, attrs, defStyle, defStyleRes) {

    private val root: ViewGroup = LayoutInflater.from(context)
        .inflate(R.layout.layout_shortcut, this, true) as ViewGroup

    init {
        orientation = HORIZONTAL

        attrs?.let {
            val typedArray = context.obtainStyledAttributes(
                it,
                R.styleable.ShortcutItem, 0, 0
            )
            val name = resources.getText(
                typedArray.getResourceId(
                    R.styleable.ShortcutItem_shortcutName,
                    R.string.shortcut
                )
            )
            val target = resources.getText(
                typedArray.getResourceId(
                    R.styleable.ShortcutItem_shortcutTarget,
                    R.string.shortcut_class
                )
            ).toString()
            val `class` = Class.forName(target)
            val drawable = ResourcesCompat.getDrawable(
                resources,
                typedArray.getResourceId(
                    R.styleable.ShortcutItem_shortcutDrawable,
                    R.drawable.shortcut_icon
                ), null
            )

            findViewById<TextView>(R.id.shortcutName).text = name
            findViewById<ImageView>(R.id.shortcutIcon).setImageDrawable(drawable)
            root.setOnClickListener {
                App.app.startActivity(Intent(App.app, `class`))
            }

            typedArray.recycle()
        }
    }
}