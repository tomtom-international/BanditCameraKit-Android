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

package com.tomtom.camera.api.v2;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.tomtom.camera.api.model.Video;
import com.tomtom.camera.api.model.VideoCapabilities;
import com.tomtom.camera.api.model.capability.Framerate;
import com.tomtom.camera.api.model.capability.Resolution;
import com.tomtom.camera.api.model.capability.VideoModeSetting;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

/**
 * Concrete implementation of interface {@link VideoCapabilities}
 */
class VideoCapabilitiesV2 implements VideoCapabilities {

    public static final String TAG = "VideoCapabilities";

    HashMultimap<Video.Mode, VideoModeV2> mVideoCapabilitiesV2;

    HashMap<Video.Mode, ArrayList<VideoModeSetting>> mVideoModeSettingsMap;

    VideoCapabilitiesV2(ArrayList<VideoModeV2> videoCapabilityItems) {
        mVideoCapabilitiesV2 = HashMultimap.create();
        mVideoModeSettingsMap = new HashMap<>();
        initVideoModeSettings(videoCapabilityItems);
    }

    private void initVideoModeSettings(ArrayList<VideoModeV2> videoCapabilityItems) {
        Multimap<Resolution, Framerate> normalFramerates = HashMultimap.create();
        Multimap<Resolution, Integer> normalIntervals = HashMultimap.create();
        Multimap<Resolution, Integer> normalSlowMotionRates = HashMultimap.create();
        Multimap<Resolution, String> normalFieldOfViews = HashMultimap.create();

        Multimap<Resolution, Framerate> slowMoFramerates = HashMultimap.create();
        Multimap<Resolution, Integer> slowMoIntervals = HashMultimap.create();
        Multimap<Resolution, Integer> slowMoSlowMotionRates = HashMultimap.create();
        Multimap<Resolution, String> slowMoFieldOfViews = HashMultimap.create();

        Multimap<Resolution, Framerate> timeLapseFramerates = HashMultimap.create();
        Multimap<Resolution, Integer> timeLapseIntervals = HashMultimap.create();
        Multimap<Resolution, Integer> timeLapseSlowMotionRates = HashMultimap.create();
        Multimap<Resolution, String> timeLapseFieldOfViews = HashMultimap.create();

        Multimap<Resolution, Framerate> nightLapseFramerates = HashMultimap.create();
        Multimap<Resolution, Integer> nightLapseIntervals = HashMultimap.create();
        Multimap<Resolution, Integer> nightLapseSlowMotionRates = HashMultimap.create();
        Multimap<Resolution, String> nightLapseFieldOfViews = HashMultimap.create();

        for(VideoModeV2 itemV2 : videoCapabilityItems) {
            mVideoCapabilitiesV2.put(itemV2.getMode(), itemV2);
            switch(itemV2.getMode()) {
                case NORMAL:
                    normalFramerates.put(itemV2.getResolution(), itemV2.getFramerate());
                    normalFieldOfViews.put(itemV2.getResolution(), itemV2.getFieldOfView());
                    break;

                case SLOW_MOTION:
                    slowMoFramerates.put(itemV2.getResolution(), itemV2.getFramerate());
                    slowMoSlowMotionRates.put(itemV2.getResolution(), itemV2.getSlowMotionRate());
                    slowMoFieldOfViews.put(itemV2.getResolution(), itemV2.getFieldOfView());
                    break;

                case TIME_LAPSE:
                    timeLapseFramerates.put(itemV2.getResolution(), itemV2.getFramerate());
                    timeLapseIntervals.put(itemV2.getResolution(), itemV2.getIntervalSecs());
                    timeLapseFieldOfViews.put(itemV2.getResolution(), itemV2.getFieldOfView());
                    break;

                case NIGHT_LAPSE:
                    nightLapseFramerates.put(itemV2.getResolution(), itemV2.getFramerate());
                    nightLapseIntervals.put(itemV2.getResolution(), itemV2.getIntervalSecs());
                    nightLapseFieldOfViews.put(itemV2.getResolution(), itemV2.getFieldOfView());
                    break;
            }
        }

        for(Video.Mode mode : mVideoCapabilitiesV2.keySet()) {
            switch(mode) {
                case NORMAL:
                    createVideoModeSettings(mode, normalFramerates, normalIntervals, normalSlowMotionRates, normalFieldOfViews);
                    break;

                case TIME_LAPSE:
                    createVideoModeSettings(mode, timeLapseFramerates, timeLapseIntervals, timeLapseSlowMotionRates, timeLapseFieldOfViews);
                    break;

                case SLOW_MOTION:
                    createVideoModeSettings(mode, slowMoFramerates, slowMoIntervals, slowMoSlowMotionRates, slowMoFieldOfViews);
                    break;

                case NIGHT_LAPSE:
                    createVideoModeSettings(mode, nightLapseFramerates, nightLapseIntervals, nightLapseSlowMotionRates, nightLapseFieldOfViews);
                    break;
            }
        }
    }

    private void createVideoModeSettings(Video.Mode mode, Multimap<Resolution, Framerate> framerates, Multimap<Resolution, Integer> intervals, Multimap<Resolution, Integer> slowMotionRates, Multimap<Resolution, String> fieldOfViews) {
        ArrayList<VideoModeSetting> videoModeSettings = new ArrayList<>();
        ArrayList<Resolution> resolutions = new ArrayList<>(framerates.keySet());
        Collections.sort(resolutions);
        for(Resolution resolution : resolutions) {
            ArrayList<Framerate> framerateArrayList = new ArrayList<>(framerates.get(resolution));
            Collections.sort(framerateArrayList);
            ArrayList<Integer> intervalsArrayList = new ArrayList<Integer>(intervals.get(resolution));
            Collections.sort(intervalsArrayList);
            VideoModeSetting videoModeSetting = new VideoModeSetting(resolution, framerateArrayList, intervalsArrayList, new ArrayList<Integer>(slowMotionRates.get(resolution)), new ArrayList<String>(fieldOfViews.get(resolution)));
            videoModeSettings.add(videoModeSetting);
        }
        mVideoModeSettingsMap.put(mode, videoModeSettings);
    }

    public ArrayList<VideoModeSetting> getSettings(Video.Mode videoMode) {
        ArrayList<VideoModeSetting> videoModeSettings = mVideoModeSettingsMap.get(videoMode);
        if(videoModeSettings == null) {
            return null;
        }
        return new ArrayList<>(videoModeSettings);
    }

    public ArrayList<Video.Mode> getSupportedModes() {
        return new ArrayList<Video.Mode>(mVideoModeSettingsMap.keySet());
    }
}
