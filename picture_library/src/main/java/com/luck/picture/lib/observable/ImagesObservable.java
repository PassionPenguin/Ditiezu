/*
 * ==================================================
 * =  PROJECT     地下铁的故事
 * =  MODULE      地下铁的故事.picture_library
 * =  FILE NAME   ImagesObservable
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

package com.luck.picture.lib.observable;

import com.luck.picture.lib.entity.LocalMedia;

import java.util.ArrayList;
import java.util.List;

/**
 * @author：luck
 * @date：2017-1-12 21:30
 * @describe：解决预览时传值过大问题
 */
public class ImagesObservable {
    private static ImagesObservable sObserver;
    private List<LocalMedia> data;

    public static ImagesObservable getInstance() {
        if (sObserver == null) {
            synchronized (ImagesObservable.class) {
                if (sObserver == null) {
                    sObserver = new ImagesObservable();
                }
            }
        }
        return sObserver;
    }

    /**
     * 存储图片用于预览时用
     *
     * @param data
     */
    public void savePreviewMediaData(List<LocalMedia> data) {
        this.data = data;
    }

    /**
     * 读取预览的图片
     */
    public List<LocalMedia> readPreviewMediaData() {
        return data == null ? new ArrayList<>() : data;
    }

    /**
     * 清空预览的图片
     */
    public void clearPreviewMediaData() {
        if (data != null) {
            data.clear();
        }
    }
}