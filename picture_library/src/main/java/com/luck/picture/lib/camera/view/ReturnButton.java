/*
 * ==================================================
 * =  PROJECT     地下铁的故事
 * =  MODULE      地下铁的故事.picture_library
 * =  FILE NAME   ReturnButton
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

package com.luck.picture.lib.camera.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.view.View;

/**
 * =====================================
 * 作    者: 陈嘉桐 445263848@qq.com
 * 版    本：1.0.4
 * 创建日期：2017/4/26
 * 描    述：向下箭头的退出按钮
 * =====================================
 */
public class ReturnButton extends View {

    Path path;
    private int size;
    private int center_X;
    private int center_Y;
    private float strokeWidth;
    private Paint paint;

    public ReturnButton(Context context, int size) {
        this(context);
        this.size = size;
        center_X = size / 2;
        center_Y = size / 2;

        strokeWidth = size / 15f;

        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(Color.WHITE);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(strokeWidth);

        path = new Path();
    }

    public ReturnButton(Context context) {
        super(context);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(size, size / 2);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        path.moveTo(strokeWidth, strokeWidth / 2);
        path.lineTo(center_X, center_Y - strokeWidth / 2);
        path.lineTo(size - strokeWidth, strokeWidth / 2);
        canvas.drawPath(path, paint);
    }
}
