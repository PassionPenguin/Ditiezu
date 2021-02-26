package io.hoarfroster.ditiezu.transforms

import android.view.View
import kotlin.math.abs

fun transformPage(view: View, position: Float) {
    view.translationX = view.width * -position
    if (position <= -1.0f || position >= 1.0f) {
        view.alpha = 0.0f
    } else if (position == 0.0f) {
        view.alpha = 1.0f
    } else {
        // position is between -1.0F & 0.0F OR 0.0F & 1.0F
        view.alpha = 1.0f - abs(position)
    }
}