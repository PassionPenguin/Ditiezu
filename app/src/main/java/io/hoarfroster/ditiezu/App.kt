package io.hoarfroster.ditiezu

import android.app.Application
import com.hjq.toast.ToastUtils
import io.hoarfroster.ditiezu.utilities.SharedPreferences

class App: Application() {
    override fun onCreate() {
        super.onCreate()
        ToastUtils.init(this)
        SharedPreferences.init(this)
    }
}