package com.passionpenguin.ditiezu.helper

import android.app.Activity
import android.graphics.Rect
import android.graphics.drawable.ColorDrawable
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver.OnGlobalLayoutListener
import android.view.WindowManager
import android.widget.PopupWindow

class HeightHelper(private val mActivity: Activity) : PopupWindow(mActivity), OnGlobalLayoutListener {
    private val rootView: View
    private var listener: HeightListener? = null
    private var heightMax // Record the maximum height of the pop content area
            = 0

    fun init(): HeightHelper {
        if (!isShowing) {
            val view = mActivity.window.decorView
            // Delay loading popupwindow, if not, error will be reported
            view.post { showAtLocation(view, Gravity.NO_GRAVITY, 0, 0) }
        }
        return this
    }

    fun setHeightListener(listener: HeightListener?): HeightHelper {
        this.listener = listener
        return this
    }

    override fun onGlobalLayout() {
        val rect = Rect()
        rootView.getWindowVisibleDisplayFrame(rect)
        if (rect.bottom > heightMax) {
            heightMax = rect.bottom
        }

        // The difference between the two is the height of the keyboard
        val keyboardHeight = heightMax - rect.bottom
        if (listener != null) {
            listener!!.onHeightChanged(keyboardHeight)
        }
    }

    interface HeightListener {
        fun onHeightChanged(height: Int)
    }

    init {

        // Basic configuration
        rootView = View(mActivity)
        contentView = rootView

        // Monitor global Layout changes
        rootView.viewTreeObserver.addOnGlobalLayoutListener(this)
        setBackgroundDrawable(ColorDrawable(0))

        // Set width to 0 and height to full screen
        width = 0
        height = ViewGroup.LayoutParams.MATCH_PARENT

        // Set keyboard pop-up mode
        softInputMode = WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE
        inputMethodMode = INPUT_METHOD_NEEDED
    }
}