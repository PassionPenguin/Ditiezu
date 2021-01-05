/*
 * ==================================================
 * =  PROJECT     地下铁的故事
 * =  MODULE      地下铁的故事.picture_library
 * =  FILE NAME   PermissionChecker
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

package com.luck.picture.lib.permissions;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.provider.Settings;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

/**
 * @author：luck
 * @date：2019-11-20 19:07
 * @describe：权限检查
 */
public class PermissionChecker {

    /**
     * 检查是否有某个权限
     *
     * @param ctx
     * @param permission
     * @return
     */
    public static boolean checkSelfPermission(Context ctx, String permission) {
        return ContextCompat.checkSelfPermission(ctx.getApplicationContext(), permission)
                == PackageManager.PERMISSION_GRANTED;
    }


    /**
     * 动态申请多个权限
     *
     * @param activity
     * @param code
     */
    public static void requestPermissions(Activity activity, @NonNull String[] permissions, int code) {
        ActivityCompat.requestPermissions(activity, permissions, code);
    }


    /**
     * Launch the application's details settings.
     */
    public static void launchAppDetailsSettings(Context context) {
        Context applicationContext = context.getApplicationContext();
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.setData(Uri.parse("package:" + applicationContext.getPackageName()));
        if (!isIntentAvailable(context, intent)) return;
        applicationContext.startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
    }

    private static boolean isIntentAvailable(Context context, final Intent intent) {
        return context.getApplicationContext()
                .getPackageManager()
                .queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY)
                .size() > 0;
    }
}
