/*
 * ==================================================
 * =  PROJECT     地下铁的故事
 * =  MODULE      地下铁的故事.picture_library
 * =  FILE NAME   PictureWindowAnimationStyle
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

package com.luck.picture.lib.style;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.AnimRes;

/**
 * @author：luck
 * @date：2019-11-25 18:17
 * @describe：PictureSelector Activity动画管理Style
 */
public class PictureWindowAnimationStyle implements Parcelable {
    public static final Creator<PictureWindowAnimationStyle> CREATOR = new Creator<PictureWindowAnimationStyle>() {
        @Override
        public PictureWindowAnimationStyle createFromParcel(Parcel source) {
            return new PictureWindowAnimationStyle(source);
        }

        @Override
        public PictureWindowAnimationStyle[] newArray(int size) {
            return new PictureWindowAnimationStyle[size];
        }
    };
    /**
     * 相册启动动画
     */
    @AnimRes
    public int activityEnterAnimation;
    /**
     * 相册退出动画
     */
    @AnimRes
    public int activityExitAnimation;
    /**
     * 预览界面启动动画
     */
    @AnimRes
    public int activityPreviewEnterAnimation;
    /**
     * 预览界面退出动画
     */
    @AnimRes
    public int activityPreviewExitAnimation;
    /**
     * 裁剪界面启动动画
     */
    @AnimRes
    public int activityCropEnterAnimation;
    /**
     * 裁剪界面退出动画
     */
    @AnimRes
    public int activityCropExitAnimation;

    public PictureWindowAnimationStyle() {
        super();
    }

    public PictureWindowAnimationStyle(@AnimRes int activityEnterAnimation,
                                       @AnimRes int activityExitAnimation) {
        super();
        this.activityEnterAnimation = activityEnterAnimation;
        this.activityExitAnimation = activityExitAnimation;
    }

    public PictureWindowAnimationStyle(@AnimRes int activityEnterAnimation,
                                       @AnimRes int activityExitAnimation,
                                       @AnimRes int activityPreviewEnterAnimation,
                                       @AnimRes int activityPreviewExitAnimation) {
        super();
        this.activityEnterAnimation = activityEnterAnimation;
        this.activityExitAnimation = activityExitAnimation;
        this.activityPreviewEnterAnimation = activityPreviewEnterAnimation;
        this.activityPreviewExitAnimation = activityPreviewExitAnimation;
    }

    protected PictureWindowAnimationStyle(Parcel in) {
        this.activityEnterAnimation = in.readInt();
        this.activityExitAnimation = in.readInt();
        this.activityPreviewEnterAnimation = in.readInt();
        this.activityPreviewExitAnimation = in.readInt();
        this.activityCropEnterAnimation = in.readInt();
        this.activityCropExitAnimation = in.readInt();
    }

    /**
     * 全局所有动画样式
     *
     * @param enterAnimation
     * @param exitAnimation
     */
    public void ofAllAnimation(int enterAnimation, int exitAnimation) {
        this.activityEnterAnimation = enterAnimation;
        this.activityExitAnimation = exitAnimation;

        this.activityPreviewEnterAnimation = enterAnimation;
        this.activityPreviewExitAnimation = exitAnimation;

        this.activityCropEnterAnimation = enterAnimation;
        this.activityCropExitAnimation = exitAnimation;

    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.activityEnterAnimation);
        dest.writeInt(this.activityExitAnimation);
        dest.writeInt(this.activityPreviewEnterAnimation);
        dest.writeInt(this.activityPreviewExitAnimation);
        dest.writeInt(this.activityCropEnterAnimation);
        dest.writeInt(this.activityCropExitAnimation);
    }
}
