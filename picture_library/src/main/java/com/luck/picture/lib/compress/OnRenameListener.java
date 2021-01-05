/*
 * ==================================================
 * =  PROJECT     地下铁的故事
 * =  MODULE      地下铁的故事.picture_library
 * =  FILE NAME   OnRenameListener
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

/**
 * Author: zibin
 * Datetime: 2018/5/18
 * <p>
 * 提供修改压缩图片命名接口
 * <p>
 * A functional interface (callback) that used to rename the file after compress.
 */
public interface OnRenameListener {

    /**
     * 压缩前调用该方法用于修改压缩后文件名
     * <p>
     * Call before compression begins.
     *
     * @param filePath 传入文件路径/ file path
     * @return 返回重命名后的字符串/ file name
     */
    String rename(String filePath);
}
