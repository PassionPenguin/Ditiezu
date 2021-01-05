/*
 * ==================================================
 * =  PROJECT     地下铁的故事
 * =  MODULE      地下铁的故事.picture_library
 * =  FILE NAME   NavBarUtils
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

package com.luck.picture.lib.immersive;

import android.app.Activity;
import android.os.Build;
import android.view.Window;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;

/**
 * @author：luck
 * @date：2019-11-25 20:58
 * @describe：NavBar工具类
 */
public class NavBarUtils {
    /**
     * 动态设置 NavBar 色值
     *
     * @param activity
     * @param color
     */
    public static void setNavBarColor(@NonNull final Activity activity, @ColorInt final int color) {
        setNavBarColor(activity.getWindow(), color);
    }

    public static void setNavBarColor(@NonNull final Window window, @ColorInt final int color) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.setNavigationBarColor(color);
        }
    }
}
