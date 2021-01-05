/*
 * ==================================================
 * =  PROJECT     地下铁的故事
 * =  MODULE      地下铁的故事.picture_library
 * =  FILE NAME   LocaleTransform
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

package com.luck.picture.lib.language;

import java.util.Locale;

/**
 * @author：luck
 * @date：2019-11-25 21:58
 * @describe：语言转换
 */
public class LocaleTransform {
    public static Locale getLanguage(int language) {
        switch (language) {
            case LanguageConfig.ENGLISH:
                // 英语-美国
                return Locale.ENGLISH;
            case LanguageConfig.TRADITIONAL_CHINESE:
                // 繁体中文
                return Locale.TRADITIONAL_CHINESE;
            case LanguageConfig.KOREA:
                // 韩语
                return Locale.KOREA;
            case LanguageConfig.GERMANY:
                // 德语
                return Locale.GERMANY;
            case LanguageConfig.FRANCE:
                // 法语
                return Locale.FRANCE;
            case LanguageConfig.JAPAN:
                // 日语
                return Locale.JAPAN;
            case LanguageConfig.VIETNAM:
                // 越南语
                return new Locale("vi");
            default:
                // 简体中文
                return Locale.CHINESE;
        }
    }
}
