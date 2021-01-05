/*
 * ==================================================
 * =  PROJECT     地下铁的故事
 * =  MODULE      地下铁的故事.picture_library
 * =  FILE NAME   PictureLoadingDialog
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
import android.content.Context;
import android.os.Bundle;
import android.view.Window;

import com.luck.picture.lib.R;

public class PictureLoadingDialog extends Dialog {

    public PictureLoadingDialog(Context context) {
        super(context, R.style.Picture_Theme_AlertDialog);
        setCancelable(true);
        setCanceledOnTouchOutside(false);
        Window window = getWindow();
        window.setWindowAnimations(R.style.PictureThemeDialogWindowStyle);
    }


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.picture_alert_dialog);
    }
}