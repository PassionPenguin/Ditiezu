/*
 * ==================================================
 * =  PROJECT     地下铁的故事
 * =  MODULE      地下铁的故事.picture_library
 * =  FILE NAME   ImageEngine
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

package com.luck.picture.lib.engine;

import android.content.Context;
import android.widget.ImageView;

import androidx.annotation.NonNull;

import com.luck.picture.lib.listener.OnImageCompleteCallback;
import com.luck.picture.lib.widget.longimage.SubsamplingScaleImageView;

/**
 * @author：luck
 * @date：2019-11-13 16:59
 * @describe：ImageEngine
 */
public interface ImageEngine {
    /**
     * Loading image
     *
     * @param context
     * @param url
     * @param imageView
     */
    void loadImage(@NonNull Context context, @NonNull String url, @NonNull ImageView imageView);

    /**
     * Loading image
     *
     * @param context
     * @param url
     * @param imageView
     */
    void loadImage(@NonNull Context context, @NonNull String url, @NonNull ImageView imageView, SubsamplingScaleImageView longImageView, OnImageCompleteCallback callback);

    /**
     * Load network long graph adaption
     *
     * @param context
     * @param url
     * @param imageView
     */
    @Deprecated
    void loadImage(@NonNull Context context, @NonNull String url, @NonNull ImageView imageView, SubsamplingScaleImageView longImageView);


    /**
     * Load album catalog pictures
     *
     * @param context
     * @param url
     * @param imageView
     */
    void loadFolderImage(@NonNull Context context, @NonNull String url, @NonNull ImageView imageView);

    /**
     * Load GIF image
     *
     * @param context
     * @param url
     * @param imageView
     */
    void loadAsGifImage(@NonNull Context context, @NonNull String url, @NonNull ImageView imageView);

    /**
     * Load picture list picture
     *
     * @param context
     * @param url
     * @param imageView
     */
    void loadGridImage(@NonNull Context context, @NonNull String url, @NonNull ImageView imageView);
}
