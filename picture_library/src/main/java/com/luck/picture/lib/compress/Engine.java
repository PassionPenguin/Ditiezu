/*
 * ==================================================
 * =  PROJECT     地下铁的故事
 * =  MODULE      地下铁的故事.picture_library
 * =  FILE NAME   Engine
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

package com.luck.picture.lib.compress;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Responsible for starting compress and managing active and cached resources.
 */
class Engine {
    private static final int DEFAULT_QUALITY = 80;
    private final InputStreamProvider srcImg;
    private final File tagImg;
    private int srcWidth;
    private int srcHeight;
    private final boolean focusAlpha;
    private int compressQuality;

    Engine(InputStreamProvider srcImg, File tagImg, boolean focusAlpha, int compressQuality) throws IOException {
        this.tagImg = tagImg;
        this.srcImg = srcImg;
        this.focusAlpha = focusAlpha;
        this.compressQuality = compressQuality <= 0 ? DEFAULT_QUALITY : compressQuality;

        if (srcImg.getMedia() != null
                && srcImg.getMedia().getWidth() > 0
                && srcImg.getMedia().getHeight() > 0) {
            this.srcWidth = srcImg.getMedia().getWidth();
            this.srcHeight = srcImg.getMedia().getHeight();
        } else {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            options.inSampleSize = 1;
            BitmapFactory.decodeStream(srcImg.open(), null, options);
            this.srcWidth = options.outWidth;
            this.srcHeight = options.outHeight;
        }
    }

    private int computeSize() {
        srcWidth = srcWidth % 2 == 1 ? srcWidth + 1 : srcWidth;
        srcHeight = srcHeight % 2 == 1 ? srcHeight + 1 : srcHeight;

        int longSide = Math.max(srcWidth, srcHeight);
        int shortSide = Math.min(srcWidth, srcHeight);

        float scale = ((float) shortSide / longSide);
        if (scale <= 1 && scale > 0.5625) {
            if (longSide < 1664) {
                return 1;
            } else if (longSide < 4990) {
                return 2;
            } else if (longSide > 4990 && longSide < 10240) {
                return 4;
            } else {
                return longSide / 1280;
            }
        } else if (scale <= 0.5625 && scale > 0.5) {
            return longSide / 1280 == 0 ? 1 : longSide / 1280;
        } else {
            return (int) Math.ceil(longSide / (1280.0 / scale));
        }
    }

    private Bitmap rotatingImage(Bitmap bitmap, int angle) {
        Matrix matrix = new Matrix();

        matrix.postRotate(angle);

        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }

    File compress() throws IOException {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = computeSize();
        Bitmap tagBitmap = BitmapFactory.decodeStream(srcImg.open(), null, options);
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        if (srcImg.getMedia() != null && !srcImg.getMedia().isCut()) {
            if (Checker.SINGLE.isJPG(srcImg.getMedia().getMimeType())) {
                int orientation = srcImg.getMedia().getOrientation();
                if (orientation > 0) {
                    boolean isOrientation = true;
                    switch (orientation) {
                        case ExifInterface.ORIENTATION_ROTATE_90:
                            orientation = 90;
                            break;
                        case ExifInterface.ORIENTATION_ROTATE_180:
                            orientation = 180;
                            break;
                        case ExifInterface.ORIENTATION_ROTATE_270:
                            orientation = 270;
                            break;
                        default:
                            isOrientation = false;
                            break;
                    }
                    if (isOrientation) {
                        tagBitmap = rotatingImage(tagBitmap, orientation);
                    }
                }
            }
        }
        if (tagBitmap != null) {
            compressQuality = compressQuality <= 0 || compressQuality > 100 ? DEFAULT_QUALITY : compressQuality;
            tagBitmap.compress(focusAlpha ? Bitmap.CompressFormat.PNG : Bitmap.CompressFormat.JPEG, compressQuality, stream);
            tagBitmap.recycle();
        }
        FileOutputStream fos = new FileOutputStream(tagImg);
        fos.write(stream.toByteArray());
        fos.flush();
        fos.close();
        stream.close();
        return tagImg;
    }
}