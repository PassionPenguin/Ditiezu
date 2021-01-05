/*
 * ==================================================
 * =  PROJECT     地下铁的故事
 * =  MODULE      地下铁的故事.picture_library
 * =  FILE NAME   PictureMediaScannerConnection
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
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.text.TextUtils;

/**
 * @author：luck
 * @date：2019-12-03 10:41
 * @describe：刷新相册
 */
public class PictureMediaScannerConnection implements MediaScannerConnection.MediaScannerConnectionClient {
    private final MediaScannerConnection mMs;
    private final String mPath;
    private ScanListener mListener;

    public PictureMediaScannerConnection(Context context, String path, ScanListener l) {
        this.mListener = l;
        this.mPath = path;
        this.mMs = new MediaScannerConnection(context.getApplicationContext(), this);
        this.mMs.connect();
    }

    public PictureMediaScannerConnection(Context context, String path) {
        this.mPath = path;
        this.mMs = new MediaScannerConnection(context.getApplicationContext(), this);
        this.mMs.connect();
    }

    @Override
    public void onMediaScannerConnected() {
        if (!TextUtils.isEmpty(mPath)) {
            mMs.scanFile(mPath, null);
        }
    }

    @Override
    public void onScanCompleted(String path, Uri uri) {
        mMs.disconnect();
        if (mListener != null) {
            mListener.onScanFinish();
        }
    }

    public interface ScanListener {
        void onScanFinish();
    }
}
