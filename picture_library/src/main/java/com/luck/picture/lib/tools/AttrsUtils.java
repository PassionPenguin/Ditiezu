/*
 * ==================================================
 * =  PROJECT     地下铁的故事
 * =  MODULE      地下铁的故事.picture_library
 * =  FILE NAME   AttrsUtils
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
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.TypedValue;

/**
 * @author：luck
 * @data：2018/3/28 下午1:00
 * @描述: 动态获取attrs
 */

public class AttrsUtils {

    /**
     * get attrs color
     *
     * @param context
     * @param attr
     * @return
     */
    public static int getTypeValueColor(Context context, int attr) {
        try {
            TypedValue typedValue = new TypedValue();
            int[] attribute = new int[]{attr};
            TypedArray array = context.obtainStyledAttributes(typedValue.resourceId, attribute);
            int color = array.getColor(0, 0);
            array.recycle();
            return color;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * attrs status color or black
     *
     * @param context
     * @param attr
     * @return
     */
    public static boolean getTypeValueBoolean(Context context, int attr) {
        try {
            TypedValue typedValue = new TypedValue();
            int[] attribute = new int[]{attr};
            TypedArray array = context.obtainStyledAttributes(typedValue.resourceId, attribute);
            boolean statusFont = array.getBoolean(0, false);
            array.recycle();
            return statusFont;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * attrs drawable
     *
     * @param context
     * @param attr
     * @return
     */
    public static Drawable getTypeValueDrawable(Context context, int attr) {
        try {
            TypedValue typedValue = new TypedValue();
            int[] attribute = new int[]{attr};
            TypedArray array = context.obtainStyledAttributes(typedValue.resourceId, attribute);
            Drawable drawable = array.getDrawable(0);
            array.recycle();
            return drawable;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
