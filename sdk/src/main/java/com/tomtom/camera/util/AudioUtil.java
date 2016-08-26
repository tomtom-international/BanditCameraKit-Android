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

import java.nio.ByteBuffer;
import java.util.HashMap;

/**
 * Audio util class for handling AAC frames.
 */
public class AudioUtil {

    private static final String TAG = "AudioUtil";

    public static final String AAC_MIME_TYPE = "audio/mp4a-latm";
    public static final int SAMPLE_RATE_DEFAULT = 48000;

    private static final int SAMPLE_RATE_SLOW_MO_1080P = 24000;
    private static final int SAMPLE_RATE_SLOW_MO_720P = 12000;
    private static final int SAMPLE_RATE_SLOW_MO_WVGA = 8000;

    private static final short ADTS_DEFAULT = 3;
    private static final short ADTS_SLOW_MO_1080P = 6;
    private static final short ADTS_SLOW_MO_720P = 9;
    private static final short ADTS_SLOW_MO_WVGA = 11;

    private static HashMap<Short, Integer> sSampleRateMap;

    static {
        sSampleRateMap = new HashMap<>();
        sSampleRateMap.put(ADTS_DEFAULT, SAMPLE_RATE_DEFAULT);
        sSampleRateMap.put(ADTS_SLOW_MO_1080P, SAMPLE_RATE_SLOW_MO_1080P);
        sSampleRateMap.put(ADTS_SLOW_MO_720P, SAMPLE_RATE_SLOW_MO_720P);
        sSampleRateMap.put(ADTS_SLOW_MO_WVGA, SAMPLE_RATE_SLOW_MO_WVGA);
    }

    public static int getSampleRateFromAacFrame(byte[] frame) {
        byte headerByte = frame[2];
        Logger.debug(TAG, "Original byte" + String.format("%02X ", headerByte));
        headerByte = (byte) (headerByte >> 2);
        Logger.debug(TAG, "Shifted byte" + String.format("%02X ", headerByte));
        short sampleRate = (short) (headerByte & 0xf);
        Logger.debug(TAG, "Sample rate " + String.format("%02X ", sampleRate));

        if(sSampleRateMap.get(sampleRate) != null) {
            int finalSampleRate = sSampleRateMap.get(sampleRate);
            Logger.error(TAG, "final Sample rate is " + finalSampleRate);
            return sSampleRateMap.get(sampleRate);
        }
        else {
            return SAMPLE_RATE_DEFAULT;
        }
    }

    public static ByteBuffer getCsdForFrame(byte[] frame) {
        byte thirdHeaderByte = frame[2];
        byte mpegObjectType = (byte) (thirdHeaderByte >> 6);
        mpegObjectType++;
        Logger.debug(TAG, "Mpeg object type: " + mpegObjectType);

        thirdHeaderByte = (byte) (thirdHeaderByte >> 3);
        thirdHeaderByte = (byte) (thirdHeaderByte & 0x7);

        byte firstCsdByte = (byte) ((mpegObjectType << 3) | thirdHeaderByte);
        Logger.info(TAG, "First Csd byte is " + firstCsdByte);

        byte thirdHeaderByteCopy = frame[2];
        Logger.info(TAG, "Third header byte copy " + thirdHeaderByteCopy);
        thirdHeaderByteCopy = (byte) (thirdHeaderByteCopy << 5);

        byte fourthHeaderByte = frame[3];
        Logger.info(TAG, "Fourth header byte copy " + fourthHeaderByte);
        fourthHeaderByte = (byte) (fourthHeaderByte >> 3);
        byte secondCsdByte = (byte) (thirdHeaderByteCopy | fourthHeaderByte);
        Logger.info(TAG, "Second Csd byte is " + secondCsdByte);
        return ByteBuffer.wrap(new byte[] {firstCsdByte, secondCsdByte});
    }
}
