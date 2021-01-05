/*
 * ==================================================
 * =  PROJECT     地下铁的故事
 * =  MODULE      地下铁的故事.picture_library
 * =  FILE NAME   PreviewViewPager
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
import android.util.AttributeSet;
import android.view.MotionEvent;

import androidx.viewpager.widget.ViewPager;

/**
 * @author：luck
 * @date：2016-12-31 22:12
 * @describe：PreviewViewPager
 */

public class PreviewViewPager extends ViewPager {
    private MyViewPageHelper helper;

    public PreviewViewPager(Context context) {
        super(context);
    }

    public PreviewViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        helper = new MyViewPageHelper(this);
    }

    @Override
    public void setCurrentItem(int item) {
        setCurrentItem(item, true);
    }

    @Override
    public void setCurrentItem(int item, boolean smoothScroll) {
        MScroller scroller = helper.getScroller();
        if (Math.abs(getCurrentItem() - item) > 1) {
            scroller.setNoDuration(true);
            super.setCurrentItem(item, smoothScroll);
            scroller.setNoDuration(false);
        } else {
            scroller.setNoDuration(false);
            super.setCurrentItem(item, smoothScroll);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        try {
            return super.onTouchEvent(ev);
        } catch (IllegalArgumentException ex) {
            ex.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        try {
            return super.onInterceptTouchEvent(ev);
        } catch (IllegalArgumentException ex) {
            ex.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        try {
            return super.dispatchTouchEvent(ev);
        } catch (IllegalArgumentException | ArrayIndexOutOfBoundsException ignored) {
        }
        return false;
    }
}
