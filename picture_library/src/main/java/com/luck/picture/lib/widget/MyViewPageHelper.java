/*
 * ==================================================
 * =  PROJECT     地下铁的故事
 * =  MODULE      地下铁的故事.picture_library
 * =  FILE NAME   MyViewPageHelper
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

import androidx.viewpager.widget.ViewPager;

import java.lang.reflect.Field;

/**
 * @author：luck
 * @date：2020-04-11 14:43
 * @describe：MyViewPageHelper
 */
public class MyViewPageHelper {
    ViewPager viewPager;

    MScroller scroller;

    public MyViewPageHelper(ViewPager viewPager) {
        this.viewPager = viewPager;
        init();
    }

    public void setCurrentItem(int item) {
        setCurrentItem(item, true);
    }

    public MScroller getScroller() {
        return scroller;
    }


    public void setCurrentItem(int item, boolean smooth) {
        int current = viewPager.getCurrentItem();
        //如果页面相隔大于1,就设置页面切换的动画的时间为0
        if (Math.abs(current - item) > 1) {
            scroller.setNoDuration(true);
            viewPager.setCurrentItem(item, smooth);
            scroller.setNoDuration(false);
        } else {
            scroller.setNoDuration(false);
            viewPager.setCurrentItem(item, smooth);
        }
    }

    private void init() {
        scroller = new MScroller(viewPager.getContext());
        Class<ViewPager> cl = ViewPager.class;
        try {
            Field field = cl.getDeclaredField("mScroller");
            field.setAccessible(true);
            //利用反射设置mScroller域为自己定义的MScroller
            field.set(viewPager, scroller);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
