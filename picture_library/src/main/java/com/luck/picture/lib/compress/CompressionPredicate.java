/*
 * ==================================================
 * =  PROJECT     地下铁的故事
 * =  MODULE      地下铁的故事.picture_library
 * =  FILE NAME   CompressionPredicate
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
 * Created on 2018/1/3 19:43
 *
 * @author andy
 * <p>
 * A functional interface (callback) that returns true or false for the given input path should be compressed.
 */

public interface CompressionPredicate {

    /**
     * Determine the given input path should be compressed and return a boolean.
     *
     * @param path input path
     * @return the boolean result
     */
    boolean apply(String path);
}
