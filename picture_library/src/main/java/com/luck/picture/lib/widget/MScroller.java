/*
 * ==================================================
 * =  PROJECT     地下铁的故事
 * =  MODULE      地下铁的故事.picture_library
 * =  FILE NAME   MScroller
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

package com.luck.picture.lib.widget;

import android.content.Context;
import android.view.animation.Interpolator;
import android.widget.Scroller;

/**
 * @author：luck
 * @date：2020-04-11 14:41
 * @describe：MScroller
 */
public class MScroller extends Scroller {

    private static final Interpolator sInterpolator = t -> {
        t -= 1.0f;
        return t * t * t * t * t + 1.0f;
    };
    public boolean noDuration;

    public MScroller(Context context) {
        this(context, sInterpolator);
    }

    public MScroller(Context context, Interpolator interpolator) {
        super(context, interpolator);
    }

    public void setNoDuration(boolean noDuration) {
        this.noDuration = noDuration;
    }

    @Override
    public void startScroll(int startX, int startY, int dx, int dy, int duration) {
        if (noDuration) {
            //界面滑动不需要时间间隔
            super.startScroll(startX, startY, dx, dy, 0);
        } else {
            super.startScroll(startX, startY, dx, dy, duration);
        }
    }
}
