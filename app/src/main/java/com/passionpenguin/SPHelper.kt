/*
 * ==================================================
 * =  PROJECT     地下铁的故事
 * =  MODULE      地下铁的故事.app
 * =  FILE NAME   SPHelper.kt
 * =  LAST MODIFIED BY PASSIONPENGUIN [1/5/21, 9:25 PM]
 * ==================================================
 * Copyright 2021 PassionPenguin. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.passionpenguin

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences

class SPHelper(activity: Activity) {
    private val sp: SharedPreferences =
        activity.getPreferences(Context.MODE_PRIVATE)

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