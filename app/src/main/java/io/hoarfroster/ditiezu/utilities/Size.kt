package io.hoarfroster.ditiezu.utilities

import android.app.Application
import android.util.DisplayMetrics

class Size {
    companion object {
        lateinit var application: Application

        fun init(app: Application) {
            this.application = app
        }

        /**
         * This method converts dp unit to equivalent pixels, depending on device density.
         *
         * @param dp A value in dp (density independent pixels) unit. Which we need to convert into pixels
         * @return A float value to represent px equivalent to dp depending on device density
         */
        fun convertDpToPixel(dp: Float): Float {
            return dp * (application.resources
                .displayMetrics.densityDpi.toFloat() / DisplayMetrics.DENSITY_DEFAULT)
        }

        /**
         * This method converts device specific pixels to density independent pixels.
         *
         * @param px A value in px (pixels) unit. Which we need to convert into db
         * @return A float value to represent dp equivalent to px value
         */
        fun convertPixelsToDp(px: Float): Float {
            return px / (application.resources
                .displayMetrics.densityDpi.toFloat() / DisplayMetrics.DENSITY_DEFAULT)
        }
    }
}
