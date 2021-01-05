/*
 * ==================================================
 * =  PROJECT     地下铁的故事
 * =  MODULE      地下铁的故事.picture_library
 * =  FILE NAME   CheckPermission
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

package com.luck.picture.lib.camera;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;

/**
 * =====================================
 * 作    者: 陈嘉桐
 * 版    本：1.1.4
 * 创建日期：2017/6/8
 * 描    述：
 * =====================================
 */
public class CheckPermission {
    public static final int STATE_RECORDING = -1;
    public static final int STATE_NO_PERMISSION = -2;
    public static final int STATE_SUCCESS = 1;

    /**
     * 用于检测是否具有录音权限
     *
     * @return
     */
    public static int getRecordState() {
        int minBuffer = AudioRecord.getMinBufferSize(44100, AudioFormat.CHANNEL_IN_MONO, AudioFormat
                .ENCODING_PCM_16BIT);
        AudioRecord audioRecord = new AudioRecord(MediaRecorder.AudioSource.DEFAULT, 44100, AudioFormat
                .CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT, (minBuffer * 100));
        short[] point = new short[minBuffer];
        int readSize = 0;
        try {

            audioRecord.startRecording();//检测是否可以进入初始化状态
        } catch (Exception e) {
            if (audioRecord != null) {
                audioRecord.release();
                audioRecord = null;
            }
            return STATE_NO_PERMISSION;
        }
        if (audioRecord.getRecordingState() != AudioRecord.RECORDSTATE_RECORDING) {
            //6.0以下机型都会返回此状态，故使用时需要判断bulid版本
            //检测是否在录音中
            if (audioRecord != null) {
                audioRecord.stop();
                audioRecord.release();
                audioRecord = null;
            }
            return STATE_RECORDING;
        } else {
            //检测是否可以获取录音结果

            readSize = audioRecord.read(point, 0, point.length);


            if (readSize <= 0) {
                if (audioRecord != null) {
                    audioRecord.stop();
                    audioRecord.release();
                    audioRecord = null;

                }
                return STATE_NO_PERMISSION;

            } else {
                if (audioRecord != null) {
                    audioRecord.stop();
                    audioRecord.release();
                    audioRecord = null;

                }

                return STATE_SUCCESS;
            }
        }
    }

}