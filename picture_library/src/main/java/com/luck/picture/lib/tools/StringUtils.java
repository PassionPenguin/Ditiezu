/*
 * ==================================================
 * =  PROJECT     地下铁的故事
 * =  MODULE      地下铁的故事.picture_library
 * =  FILE NAME   StringUtils
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

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.RelativeSizeSpan;
import android.widget.TextView;

import com.luck.picture.lib.R;
import com.luck.picture.lib.config.PictureMimeType;

import java.util.regex.Pattern;

/**
 * @author：luck
 * @data：2017/5/25 19:12
 * @描述: String Utils
 */
public class StringUtils {

    public static void tempTextFont(TextView tv, int mimeType) {
        String text = tv.getText().toString().trim();
        String str = mimeType == PictureMimeType.ofAudio() ?
                tv.getContext().getString(R.string.picture_empty_audio_title)
                : tv.getContext().getString(R.string.picture_empty_title);
        String sumText = str + text;
        Spannable placeSpan = new SpannableString(sumText);
        placeSpan.setSpan(new RelativeSizeSpan(0.8f), str.length(), sumText.length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        tv.setText(placeSpan);
    }

    /**
     * 匹配数值
     *
     * @param str
     * @return
     */
    public static int stringToInt(String str) {
        Pattern pattern = Pattern.compile("^[-+]?[\\d]+$");
        return pattern.matcher(str).matches() ? Integer.valueOf(str) : 0;
    }

    /**
     * 根据类型获取相应的Toast文案
     *
     * @param context
     * @param mimeType
     * @param maxSelectNum
     * @return
     */
    @SuppressLint("StringFormatMatches")
    public static String getMsg(Context context, String mimeType, int maxSelectNum) {
        if (PictureMimeType.isHasVideo(mimeType)) {
            return context.getString(R.string.picture_message_video_max_num, maxSelectNum);
        } else if (PictureMimeType.isHasAudio(mimeType)) {
            return context.getString(R.string.picture_message_audio_max_num, maxSelectNum);
        } else {
            return context.getString(R.string.picture_message_max_num, maxSelectNum);
        }
    }

    /**
     * 重命名相册拍照
     *
     * @param fileName
     * @return
     */
    public static String rename(String fileName) {
        String temp = fileName.substring(0, fileName.lastIndexOf("."));
        String suffix = fileName.substring(fileName.lastIndexOf("."));
        return temp + "_" + DateUtils.getCreateFileName() + suffix;
    }

    /**
     * 重命名后缀
     *
     * @param fileName
     * @return
     */
    public static String renameSuffix(String fileName, String suffix) {
        String temp = fileName.substring(0, fileName.lastIndexOf("."));
        return temp + suffix;
    }

    /**
     * getEncryptionValue
     *
     * @param url
     * @param width
     * @param height
     * @return
     */
    public static String getEncryptionValue(String url, int width, int height) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(url).append("_").append(width).append("x").append(height);
        return ValueOf.toString(Math.abs(hash(stringBuilder.hashCode())));
    }

    /**
     * hash
     *
     * @param key
     * @return
     */
    public static final int hash(Object key) {
        int h;
        return (key == null) ? 0 : (h = key.hashCode()) ^ (h >>> 16);
    }
}
