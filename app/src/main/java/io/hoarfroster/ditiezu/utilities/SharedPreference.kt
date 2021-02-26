package io.hoarfroster.ditiezu.utilities

import android.app.Application
import android.content.Context
import android.content.SharedPreferences

class SharedPreferences {
    companion object {
        lateinit var application: Application
        lateinit var sp: SharedPreferences

        fun init(app: Application) {
            this.application = app

            this.sp =
                application.getSharedPreferences("storedData", Context.MODE_PRIVATE)
        }

        fun edit(key: String, value: Any) {
            with(sp.edit()) {
                when (value) {
                    is Boolean -> this.putBoolean(key, value)
                    is String -> this.putString(key, value)
                    is Int -> this.putInt(key, value)
                    is Long -> this.putLong(key, value)
                    is Float -> this.putFloat(key, value)
                }
                this.commit()
            }
        }

        fun getString(key: String): String {
            return sp.getString(key, "") ?: ""
        }

        fun getBoolean(key: String): Boolean {
            return sp.getBoolean(key, false)
        }

        fun getInt(key: String): Int {
            return sp.getInt(key, 0)
        }

        fun getLong(key: String): Long {
            return sp.getLong(key, 0)
        }

        fun getFloat(key: String): Float {
            return sp.getFloat(key, 0f)
        }
    }
}