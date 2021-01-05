/*
 * ==================================================
 * =  PROJECT     地下铁的故事
 * =  MODULE      地下铁的故事.picture_library
 * =  FILE NAME   PictureSelectorExternalUtils
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

package com.luck.picture.lib;

import android.content.Context;
import android.media.ExifInterface;
import android.net.Uri;

import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.tools.PictureFileUtils;
import com.luck.picture.lib.tools.SdkVersionUtils;

import java.io.InputStream;

/**
 * @author：luck
 * @date：2020-04-12 13:13
 * @describe：PictureSelector对外提供的一些方法
 */
public class PictureSelectorExternalUtils {
    /**
     * 获取ExifInterface
     *
     * @param context
     * @param url
     * @return
     */
    public static ExifInterface getExifInterface(Context context, String url) {
        ExifInterface exifInterface = null;
        InputStream inputStream = null;
        try {
            if (SdkVersionUtils.checkedAndroid_Q() && PictureMimeType.isContent(url)) {
                inputStream = context.getContentResolver().openInputStream(Uri.parse(url));
                if (inputStream != null) {
                    exifInterface = new ExifInterface(inputStream);
                }
            } else {
                exifInterface = new ExifInterface(url);
            }
            return exifInterface;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            PictureFileUtils.close(inputStream);
        }
        return null;
    }
}
