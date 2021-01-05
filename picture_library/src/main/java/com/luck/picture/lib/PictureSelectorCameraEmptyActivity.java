/*
 * ==================================================
 * =  PROJECT     地下铁的故事
 * =  MODULE      地下铁的故事.picture_library
 * =  FILE NAME   PictureSelectorCameraEmptyActivity
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

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.config.PictureSelectionConfig;
import com.luck.picture.lib.entity.LocalMedia;
import com.luck.picture.lib.immersive.ImmersiveManage;
import com.luck.picture.lib.permissions.PermissionChecker;
import com.luck.picture.lib.thread.PictureThreadUtils;
import com.luck.picture.lib.tools.BitmapUtils;
import com.luck.picture.lib.tools.MediaUtils;
import com.luck.picture.lib.tools.PictureFileUtils;
import com.luck.picture.lib.tools.SdkVersionUtils;
import com.luck.picture.lib.tools.ToastUtils;
import com.luck.picture.lib.tools.ValueOf;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * @author：luck
 * @date：2019-11-15 21:41
 * @describe：PictureSelectorCameraEmptyActivity
 */
public class PictureSelectorCameraEmptyActivity extends PictureBaseActivity {

    @Override
    public void immersive() {
        ImmersiveManage.immersiveAboveAPI23(this
                , ContextCompat.getColor(this, R.color.picture_color_transparent)
                , ContextCompat.getColor(this, R.color.picture_color_transparent)
                , openWhiteStatusBar);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (config == null) {
            closeActivity();
            return;
        }
        if (!config.isUseCustomCamera) {
            if (savedInstanceState == null) {
                if (PermissionChecker
                        .checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) &&
                        PermissionChecker
                                .checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

                    if (PictureSelectionConfig.onCustomCameraInterfaceListener != null) {
                        if (config.chooseMode == PictureConfig.TYPE_VIDEO) {
                            PictureSelectionConfig.onCustomCameraInterfaceListener.onCameraClick(getContext(), config, PictureConfig.TYPE_VIDEO);
                        } else {
                            PictureSelectionConfig.onCustomCameraInterfaceListener.onCameraClick(getContext(), config, PictureConfig.TYPE_IMAGE);
                        }
                    } else {
                        onTakePhoto();
                    }
                } else {
                    PermissionChecker.requestPermissions(this, new String[]{
                            Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE}, PictureConfig.APPLY_STORAGE_PERMISSIONS_CODE);
                }
            }
            setTheme(R.style.Picture_Theme_Translucent);
        }
    }


    @Override
    public int getResourceId() {
        return R.layout.picture_empty;
    }


    /**
     * open camera
     */
    private void onTakePhoto() {
        if (PermissionChecker
                .checkSelfPermission(this, Manifest.permission.CAMERA)) {
            boolean isPermissionChecker = true;
            if (config != null && config.isUseCustomCamera) {
                isPermissionChecker = PermissionChecker.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO);
            }
            if (isPermissionChecker) {
                startCamera();
            } else {
                PermissionChecker
                        .requestPermissions(this,
                                new String[]{Manifest.permission.RECORD_AUDIO}, PictureConfig.APPLY_RECORD_AUDIO_PERMISSIONS_CODE);
            }
        } else {
            PermissionChecker.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA}, PictureConfig.APPLY_CAMERA_PERMISSIONS_CODE);
        }
    }

    /**
     * Open the Camera by type
     */
    private void startCamera() {
        switch (config.chooseMode) {
            case PictureConfig.TYPE_ALL:
            case PictureConfig.TYPE_IMAGE:
                startOpenCamera();
                break;
            case PictureConfig.TYPE_VIDEO:
                startOpenCameraVideo();
                break;
            case PictureConfig.TYPE_AUDIO:
                startOpenCameraAudio();
                break;
            default:
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case PictureConfig.REQUEST_CAMERA:
                    dispatchHandleCamera(data);
                    break;
                default:
                    break;
            }
        } else if (resultCode == RESULT_CANCELED) {
            if (config != null && PictureSelectionConfig.listener != null) {
                PictureSelectionConfig.listener.onCancel();
            }
            closeActivity();
        }
    }

    /**
     * dispatchHandleCamera
     *
     * @param intent
     */
    protected void dispatchHandleCamera(Intent intent) {
        boolean isAudio = config.chooseMode == PictureMimeType.ofAudio();
        config.cameraPath = isAudio ? getAudioPath(intent) : config.cameraPath;
        if (TextUtils.isEmpty(config.cameraPath)) {
            return;
        }
        showPleaseDialog();
        PictureThreadUtils.executeByIo(new PictureThreadUtils.SimpleTask<LocalMedia>() {

            @Override
            public LocalMedia doInBackground() {
                LocalMedia media = new LocalMedia();
                String mimeType = isAudio ? PictureMimeType.MIME_TYPE_AUDIO : "";
                int[] newSize = new int[2];
                long duration = 0;
                if (!isAudio) {
                    if (PictureMimeType.isContent(config.cameraPath)) {
                        // content: Processing rules
                        String path = PictureFileUtils.getPath(getContext(), Uri.parse(config.cameraPath));
                        if (!TextUtils.isEmpty(path)) {
                            File cameraFile = new File(path);
                            mimeType = PictureMimeType.getMimeType(config.cameraMimeType);
                            media.setSize(cameraFile.length());
                        }
                        if (PictureMimeType.isHasImage(mimeType)) {
                            newSize = MediaUtils.getImageSizeForUrlToAndroidQ(getContext(), config.cameraPath);
                        } else if (PictureMimeType.isHasVideo(mimeType)) {
                            newSize = MediaUtils.getVideoSizeForUri(getContext(), Uri.parse(config.cameraPath));
                            duration = MediaUtils.extractDuration(getContext(), SdkVersionUtils.checkedAndroid_Q(), config.cameraPath);
                        }
                        int lastIndexOf = config.cameraPath.lastIndexOf("/") + 1;
                        media.setId(lastIndexOf > 0 ? ValueOf.toLong(config.cameraPath.substring(lastIndexOf)) : -1);
                        media.setRealPath(path);
                        // Custom photo has been in the application sandbox into the file
                        String mediaPath = intent != null ? intent.getStringExtra(PictureConfig.EXTRA_MEDIA_PATH) : null;
                        media.setAndroidQToPath(mediaPath);
                    } else {
                        File cameraFile = new File(config.cameraPath);
                        mimeType = PictureMimeType.getMimeType(config.cameraMimeType);
                        media.setSize(cameraFile.length());
                        if (PictureMimeType.isHasImage(mimeType)) {
                            int degree = PictureFileUtils.readPictureDegree(getContext(), config.cameraPath);
                            BitmapUtils.rotateImage(degree, config.cameraPath);
                            newSize = MediaUtils.getImageSizeForUrl(config.cameraPath);
                        } else if (PictureMimeType.isHasVideo(mimeType)) {
                            newSize = MediaUtils.getVideoSizeForUrl(config.cameraPath);
                            duration = MediaUtils.extractDuration(getContext(), SdkVersionUtils.checkedAndroid_Q(), config.cameraPath);
                        }
                        // Taking a photo generates a temporary id
                        media.setId(System.currentTimeMillis());
                    }
                    media.setPath(config.cameraPath);
                    media.setDuration(duration);
                    media.setMimeType(mimeType);
                    media.setWidth(newSize[0]);
                    media.setHeight(newSize[1]);
                    if (SdkVersionUtils.checkedAndroid_Q() && PictureMimeType.isHasVideo(media.getMimeType())) {
                        media.setParentFolderName(Environment.DIRECTORY_MOVIES);
                    } else {
                        media.setParentFolderName(PictureMimeType.CAMERA);
                    }
                    media.setChooseModel(config.chooseMode);
                    long bucketId = MediaUtils.getCameraFirstBucketId(getContext());
                    media.setBucketId(bucketId);
                    // The width and height of the image are reversed if there is rotation information
                    MediaUtils.setOrientationSynchronous(getContext(), media, config.isAndroidQChangeWH, config.isAndroidQChangeVideoWH);
                }
                return media;
            }

            @Override
            public void onSuccess(LocalMedia result) {
                // Refresh the system library
                dismissDialog();
                if (!SdkVersionUtils.checkedAndroid_Q()) {
                    if (config.isFallbackVersion3) {
                        new PictureMediaScannerConnection(getContext(), config.cameraPath);
                    } else {
                        sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(new File(config.cameraPath))));
                    }
                }
                dispatchCameraHandleResult(result);
                // Solve some phone using Camera, DCIM will produce repetitive problems
                if (!SdkVersionUtils.checkedAndroid_Q() && PictureMimeType.isHasImage(result.getMimeType())) {
                    int lastImageId = MediaUtils.getDCIMLastImageId(getContext());
                    if (lastImageId != -1) {
                        MediaUtils.removeMedia(getContext(), lastImageId);
                    }
                }
            }
        });
    }

    /**
     * dispatchCameraHandleResult
     *
     * @param media
     */
    private void dispatchCameraHandleResult(LocalMedia media) {
        boolean isHasImage = PictureMimeType.isHasImage(media.getMimeType());
        if (config.enableCrop && isHasImage) {
            config.originalPath = config.cameraPath;
        } else if (config.isCompress && isHasImage && !config.isCheckOriginalImage) {
            List<LocalMedia> result = new ArrayList<>();
            result.add(media);
            compressImage(result);
        } else {
            List<LocalMedia> result = new ArrayList<>();
            result.add(media);
            onResult(result);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PictureConfig.APPLY_STORAGE_PERMISSIONS_CODE:
                // Store Permissions
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    PermissionChecker.requestPermissions(this,
                            new String[]{Manifest.permission.CAMERA}, PictureConfig.APPLY_CAMERA_PERMISSIONS_CODE);
                } else {
                    ToastUtils.s(getContext(), getString(R.string.picture_jurisdiction));
                    closeActivity();
                }
                break;
            case PictureConfig.APPLY_CAMERA_PERMISSIONS_CODE:
                // Camera Permissions
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    onTakePhoto();
                } else {
                    closeActivity();
                    ToastUtils.s(getContext(), getString(R.string.picture_camera));
                }
                break;
            case PictureConfig.APPLY_RECORD_AUDIO_PERMISSIONS_CODE:
                // Recording Permissions
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    onTakePhoto();
                } else {
                    closeActivity();
                    ToastUtils.s(getContext(), getString(R.string.picture_audio));
                }
                break;
        }
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        closeActivity();
    }

}