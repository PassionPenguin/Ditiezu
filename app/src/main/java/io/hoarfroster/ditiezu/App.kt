package io.hoarfroster.ditiezu

import android.app.Application
import com.hjq.toast.ToastUtils
import io.hoarfroster.ditiezu.utilities.SharedPreferences
import io.hoarfroster.ditiezu.utilities.Size

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        ToastUtils.init(this)
        SharedPreferences.init(this)
        Size.init(this)

        app = this
    }

    companion object {
        lateinit var app: Application
    }
}