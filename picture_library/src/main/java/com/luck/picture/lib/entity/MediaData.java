/*
 * ==================================================
 * =  PROJECT     地下铁的故事
 * =  MODULE      地下铁的故事.picture_library
 * =  FILE NAME   MediaData
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

package com.luck.picture.lib.entity;

import java.util.List;

/**
 * @author：luck
 * @date：2020-04-17 13:52
 * @describe：MediaData
 */
public class MediaData {

    /**
     * Is there more
     */
    public boolean isHasNextMore;

    /**
     * data
     */
    public List<LocalMedia> data;


    public MediaData() {
        super();
    }

    public MediaData(boolean isHasNextMore, List<LocalMedia> data) {
        super();
        this.isHasNextMore = isHasNextMore;
        this.data = data;
    }
}
