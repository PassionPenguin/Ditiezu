package io.hoarfroster.ditiezu.layouts

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.core.content.res.ResourcesCompat
import androidx.viewpager.widget.ViewPager
import androidx.viewpager2.widget.ViewPager2
import io.hoarfroster.ditiezu.R
import kotlin.properties.Delegates

@SuppressLint("ViewConstructor")
class TabLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0,
    defStyleRes: Int = 0
) : LinearLayout(context, attrs, defStyle, defStyleRes) {

    private val root: ViewGroup = LayoutInflater.from(context)
        .inflate(R.layout.layout_tab_layout, this, true) as ViewGroup

    private var tabs: Array<TabItem> = arrayOf()

    var tabNames: Array<String> = arrayOf()
        set(value) {
            field = value
            updateTabs()
        }

    var viewPager: ViewPager2? = null

    init {
        orientation = HORIZONTAL
        background = ResourcesCompat.getDrawable(resources, R.drawable.tab_layout, null)
    }

    private fun updateTabs() {
        root.removeAllViews()
        tabs = Array(tabNames.size) {
            val tab = TabItem(context, tabNames[it]) { _ ->
                viewPager?.currentItem = it
            }
            root.addView(tab)
            tab
        }
    }
}