/*
 * Copyright (C) 2012-2016. TomTom International BV (http://tomtom.com).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.tomtom.camera.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Helper class that works with {@link Matcher} object, which is used to search through a text
 * for multiple occurrences of a regular expression
 */
public class RegexUtil {

    /**
     * Returns object , which represent result of search of pattern occurrences in a path String
     * @param pattern expression which should be searched for in text
     * @param path text to search from
     * @return {@link Matcher}
     */
    public static Matcher getMatcher(String pattern, String path) {
        Matcher matcher = Pattern.compile(pattern).matcher(path);
        matcher.find();
        return matcher;
    }

}
