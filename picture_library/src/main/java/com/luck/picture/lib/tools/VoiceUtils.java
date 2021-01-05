/*
 * ==================================================
 * =  PROJECT     地下铁的故事
 * =  MODULE      地下铁的故事.picture_library
 * =  FILE NAME   VoiceUtils
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

package com.luck.picture.lib.tools;

import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;

import com.luck.picture.lib.R;

/**
 * @author：luck
 * @data：2017/5/25 19:12
 * @描述: voice utils
 */
public class VoiceUtils {

    private static VoiceUtils instance;
    private SoundPool soundPool;
    /**
     * 创建某个声音对应的音频ID
     */
    private int soundID;

    public VoiceUtils() {
    }

    public static VoiceUtils getInstance() {
        if (instance == null) {
            synchronized (VoiceUtils.class) {
                if (instance == null) {
                    instance = new VoiceUtils();
                }
            }
        }
        return instance;
    }

    public void init(Context context) {
        initPool(context);
    }

    private void initPool(Context context) {
        if (soundPool == null) {
            soundPool = new SoundPool(1, AudioManager.STREAM_ALARM, 0);
            soundID = soundPool.load(context.getApplicationContext(), R.raw.picture_music, 1);
        }
    }

    /**
     * 播放音频
     */
    public void play() {
        if (soundPool != null) {
            soundPool.play(soundID, 0.1f, 0.5f, 0, 1, 1);
        }
    }

    /**
     * 释放资源
     */
    public void releaseSoundPool() {
        try {
            if (soundPool != null) {
                soundPool.release();
                soundPool = null;
            }
            instance = null;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
