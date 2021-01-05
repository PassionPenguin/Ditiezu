/*
 * ==================================================
 * =  PROJECT     地下铁的故事
 * =  MODULE      地下铁的故事.picture_library
 * =  FILE NAME   PhotoItemSelectedDialog
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

package com.luck.picture.lib.dialog;

import android.app.Dialog;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.luck.picture.lib.R;
import com.luck.picture.lib.listener.OnItemClickListener;
import com.luck.picture.lib.tools.ScreenUtils;

/**
 * @author：luck
 * @date：2019-12-12 16:39
 * @describe：PhotoSelectedDialog
 */
public class PhotoItemSelectedDialog extends DialogFragment implements View.OnClickListener {
    public static final int IMAGE_CAMERA = 0;
    public static final int VIDEO_CAMERA = 1;
    private TextView tvPicturePhoto, tvPictureVideo, tvPictureCancel;
    private OnItemClickListener onItemClickListener;

    public static PhotoItemSelectedDialog newInstance() {
        PhotoItemSelectedDialog selectedDialog = new PhotoItemSelectedDialog();
        return selectedDialog;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        if (getDialog() != null) {
            getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
            if (getDialog().getWindow() != null) {
                getDialog().getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            }
        }
        return inflater.inflate(R.layout.picture_dialog_camera_selected, container);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        tvPicturePhoto = view.findViewById(R.id.picture_tv_photo);
        tvPictureVideo = view.findViewById(R.id.picture_tv_video);
        tvPictureCancel = view.findViewById(R.id.picture_tv_cancel);
        tvPictureVideo.setOnClickListener(this);
        tvPicturePhoto.setOnClickListener(this);
        tvPictureCancel.setOnClickListener(this);
    }

    @Override
    public void onStart() {
        super.onStart();
        initDialogStyle();
    }

    /**
     * DialogFragment Style
     */
    private void initDialogStyle() {
        Dialog dialog = getDialog();
        if (dialog != null) {
            Window window = dialog.getWindow();
            if (window != null) {
                window.setLayout(ScreenUtils.getScreenWidth(getContext()), RelativeLayout.LayoutParams.WRAP_CONTENT);
                window.setGravity(Gravity.BOTTOM);
                window.setWindowAnimations(R.style.PictureThemeDialogFragmentAnim);
            }
        }
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (onItemClickListener != null) {
            if (id == R.id.picture_tv_photo) {
                onItemClickListener.onItemClick(v, IMAGE_CAMERA);
            }
            if (id == R.id.picture_tv_video) {
                onItemClickListener.onItemClick(v, VIDEO_CAMERA);
            }
        }

        dismissAllowingStateLoss();
    }

    @Override
    public void show(FragmentManager manager, String tag) {
        FragmentTransaction ft = manager.beginTransaction();
        ft.add(this, tag);
        ft.commitAllowingStateLoss();
    }
}
