/*
 * ==================================================
 * =  PROJECT     地下铁的故事
 * =  MODULE      地下铁的故事.picture_library
 * =  FILE NAME   ToastUtils
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

package com.luck.picture.lib.tools;

import android.content.Context;
import android.widget.Toast;

/**
 * @author：luck
 * @data：2018/3/28 下午4:10
 * @描述: Toast工具类
 */

public final class ToastUtils {
    private final static long TIME = 1500;
    /**
     * Prevent continuous click, jump two pages
     */
    private static long lastToastTime;

    public static void s(Context context, String s) {
        if (!isShowToast()) {
            Toast.makeText(context.getApplicationContext(), s, Toast.LENGTH_SHORT)
                    .show();
        }
    }

    public static boolean isShowToast() {
        long time = System.currentTimeMillis();
        if (time - lastToastTime < TIME) {
            return true;
        }
        lastToastTime = time;
        return false;
    }
}
