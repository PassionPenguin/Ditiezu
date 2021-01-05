/*
 * ==================================================
 * =  PROJECT     地下铁的故事
 * =  MODULE      地下铁的故事.picture_library
 * =  FILE NAME   SdkVersionUtils
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

import android.os.Build;

/**
 * @author：luck
 * @date：2019-07-17 15:12
 * @describe：Android Sdk版本判断
 */
public class SdkVersionUtils {
    /**
     * 判断是否是Android Q版本
     *
     * @return
     */
    public static boolean checkedAndroid_Q() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q;
    }
}
