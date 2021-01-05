/*
 * ==================================================
 * =  PROJECT     地下铁的故事
 * =  MODULE      地下铁的故事.picture_library
 * =  FILE NAME   InputStreamAdapter
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

import java.io.IOException;
import java.io.InputStream;

/**
 * Automatically close the previous InputStream when opening a new InputStream,
 * and finally need to manually call {@link #close()} to release the resource.
 */
public abstract class InputStreamAdapter implements InputStreamProvider {

    private InputStream inputStream;

    @Override
    public InputStream open() throws IOException {
        close();
        inputStream = openInternal();
        return inputStream;
    }

    public abstract InputStream openInternal() throws IOException;

    @Override
    public void close() {
        if (inputStream != null) {
            try {
                inputStream.close();
            } catch (IOException ignore) {
            } finally {
                inputStream = null;
            }
        }
    }
}