/*
 * ==================================================
 * =  PROJECT     地下铁的故事
 * =  MODULE      地下铁的故事.picture_library
 * =  FILE NAME   PictureConfig
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

package com.luck.picture.lib.config;

/**
 * @author：luck
 * @data：2017/5/24 1:00
 * @describe : constant
 */
public final class PictureConfig {
    public final static int APPLY_STORAGE_PERMISSIONS_CODE = 1;
    public final static int APPLY_CAMERA_PERMISSIONS_CODE = 2;
    public final static int APPLY_AUDIO_PERMISSIONS_CODE = 3;
    public final static int APPLY_RECORD_AUDIO_PERMISSIONS_CODE = 4;
    public final static int APPLY_CAMERA_STORAGE_PERMISSIONS_CODE = 5;

    public final static String EXTRA_MEDIA_KEY = "mediaKey";
    public final static String EXTRA_MEDIA_PATH = "mediaPath";
    public final static String EXTRA_AUDIO_PATH = "audioPath";
    public final static String EXTRA_VIDEO_PATH = "videoPath";
    public final static String EXTRA_PREVIEW_VIDEO = "isExternalPreviewVideo";
    public final static String EXTRA_PREVIEW_DELETE_POSITION = "position";
    public final static String EXTRA_FC_TAG = "picture";
    public final static String EXTRA_RESULT_SELECTION = "extra_result_media";
    public final static String EXTRA_PREVIEW_SELECT_LIST = "previewSelectList";
    public final static String EXTRA_SELECT_LIST = "selectList";
    public final static String EXTRA_COMPLETE_SELECTED = "isCompleteOrSelected";
    public final static String EXTRA_CHANGE_SELECTED_DATA = "isChangeSelectedData";
    public final static String EXTRA_CHANGE_ORIGINAL = "isOriginal";
    public final static String EXTRA_POSITION = "position";
    public final static String EXTRA_OLD_CURRENT_LIST_SIZE = "oldCurrentListSize";
    public final static String EXTRA_DIRECTORY_PATH = "directory_path";
    public final static String EXTRA_BOTTOM_PREVIEW = "bottom_preview";
    public final static String EXTRA_CONFIG = "PictureSelectorConfig";
    public final static String EXTRA_SHOW_CAMERA = "isShowCamera";
    public final static String EXTRA_IS_CURRENT_DIRECTORY = "currentDirectory";
    public final static String EXTRA_BUCKET_ID = "bucket_id";
    public final static String EXTRA_PAGE = "page";
    public final static String EXTRA_DATA_COUNT = "count";
    public final static String CAMERA_FACING = "android.intent.extras.CAMERA_FACING";

    public final static String EXTRA_ALL_FOLDER_SIZE = "all_folder_size";
    public final static String EXTRA_QUICK_CAPTURE = "android.intent.extra.quickCapture";

    public final static int MAX_PAGE_SIZE = 60;

    public final static int MIN_PAGE_SIZE = 10;

    public final static int LOADED = 0;

    public final static int NORMAL = -1;

    public final static int CAMERA_BEFORE = 1;

    public final static int TYPE_ALL = 0;
    public final static int TYPE_IMAGE = 1;
    public final static int TYPE_VIDEO = 2;

    @Deprecated
    public final static int TYPE_AUDIO = 3;

    public final static int MAX_COMPRESS_SIZE = 100;

    public final static int TYPE_CAMERA = 1;
    public final static int TYPE_PICTURE = 2;

    public final static int SINGLE = 1;
    public final static int MULTIPLE = 2;

    public final static int PREVIEW_VIDEO_CODE = 166;
    public final static int CHOOSE_REQUEST = 188;
    public final static int REQUEST_CAMERA = 909;
}
