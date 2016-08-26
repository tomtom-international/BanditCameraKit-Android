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

package com.tomtom.camera.api.model;

import android.support.annotation.StringDef;

import com.tomtom.camera.api.model.capability.Framerate;
import com.tomtom.camera.api.model.capability.Resolution;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;

/**
 * This interface defines functions, which provide access to video parameters (mode,resolution,
 * field of view, framerate, etc...)
 */
public interface VideoMode {

    /**
     * These constants define possible values for field of view
     */
    @Retention(RetentionPolicy.SOURCE)
    @StringDef({
        WIDE,
        NORMAL
    })
    public @interface FieldOfView{}
    public String WIDE = "wide";
    public String NORMAL = "normal";

    /**
     *  Returns mode of video
     * @return {@link Video.Mode}
     */
    Video.Mode getMode();

    /**
     *  Returns resolution of video
     * @return {@link Resolution}
     */
    Resolution getResolution();

    /**
     *  Returns framerate of video
     * @return {@link Framerate}
     */
    Framerate getFramerate();

    /**
     *  Returns field of view of video
     * @return one of the {@link FieldOfView} strings
     */
    @FieldOfView String getFieldOfView();

    /**
     *  Returns value of slow motion rate , if video is slow motion
     * @return {@link Integer} if video is slow motion,  otherwise null
     */
    Integer getIntervalSecs();

    /**
     *  Returns value of time interval (in seconds) between taking two consecutive input frames.
     *  Valid for time lapse mode.
     * @return {@link Integer} if video is time lapse, otherwise null
     */
    Integer getSlowMotionRate();

    /**
     *  Returns an array of strings, containing names of the scenes which are not supported
     *  by that particular mode
     * @return Array list of Strings
     */
    ArrayList<String> getScenesDisabled();
}
