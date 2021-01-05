/*
 * ==================================================
 * =  PROJECT     地下铁的故事
 * =  MODULE      地下铁的故事.picture_library
 * =  FILE NAME   ValueOf
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

/**
 * @author：luck
 * @date：2019-11-12 14:27
 * @describe：类型转换工具类
 */
public class ValueOf {
    public static String toString(Object o) {
        String value = "";
        try {
            value = o.toString();
        } catch (Exception e) {
        }

        return value;
    }


    public static double toDouble(Object o) {

        return toDouble(o, 0);
    }

    public static double toDouble(Object o, int defaultValue) {
        if (o == null) {
            return defaultValue;
        }

        double value;
        try {
            value = Double.valueOf(o.toString().trim());
        } catch (Exception e) {
            value = defaultValue;
        }

        return value;
    }

    public static long toLong(Object o, long defaultValue) {
        if (o == null) {
            return defaultValue;
        }
        long value = 0;
        try {
            String s = o.toString().trim();
            if (s.contains(".")) {
                value = Long.valueOf(s.substring(0, s.lastIndexOf(".")));
            } else {
                value = Long.valueOf(s);
            }
        } catch (Exception e) {
            value = defaultValue;
        }


        return value;
    }

    public static long toLong(Object o) {
        return toLong(o, 0);
    }


    public static float toFloat(Object o, long defaultValue) {
        if (o == null) {
            return defaultValue;
        }
        float value = 0;
        try {
            String s = o.toString().trim();
            value = Float.valueOf(s);
        } catch (Exception e) {
            value = defaultValue;
        }


        return value;
    }

    public static float toFloat(Object o) {
        return toFloat(o, 0);
    }


    public static int toInt(Object o, int defaultValue) {
        if (o == null) {
            return defaultValue;
        }
        int value;
        try {
            String s = o.toString().trim();
            if (s.contains(".")) {
                value = Integer.valueOf(s.substring(0, s.lastIndexOf(".")));
            } else {
                value = Integer.valueOf(s);
            }
        } catch (Exception e) {
            value = defaultValue;
        }

        return value;
    }

    public static int toInt(Object o) {
        return toInt(o, 0);
    }

    public static boolean toBoolean(Object o) {
        return toBoolean(o, false);

    }


    public static boolean toBoolean(Object o, boolean defaultValue) {
        if (o == null) {
            return false;
        }
        boolean value;
        try {
            String s = o.toString().trim();
            value = !"false".equals(s.trim());
        } catch (Exception e) {
            value = defaultValue;
        }

        return value;
    }


    public static <T> T to(Object o, T defaultValue) {
        if (o == null) {
            return defaultValue;
        }
        T value = (T) o;
        return value;
    }
}
