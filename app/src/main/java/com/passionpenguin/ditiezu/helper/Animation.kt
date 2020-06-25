package com.passionpenguin.ditiezu.helper

import android.view.animation.AlphaAnimation

class Animation {
    fun fadeOutAnimation(): AlphaAnimation {
        val fadeOutAnimation = AlphaAnimation(1.0f, 0f)
        fadeOutAnimation.duration = 400L
        fadeOutAnimation.startOffset = 5000
        fadeOutAnimation.fillAfter = true

        return fadeOutAnimation
    }
}