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

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;

/**
 * This interface corresponds to scene model and defines the functions to retrieve values from model
 */
public interface Scene {

    /**
     * Enum which corresponds to available camera scenes.
     */
    enum Mode {
        AUTO("auto"),
        BRIGHT("bright"),
        NIGHT("night"),
        UNDERWATER("underwater"),
        PHONE("phone"),
        UNKNOWN("unknown");

        String mVal;

        public static Mode fromString(String value) {
            for(Mode mode : Mode.values()) {
                if(mode.value().equals(value)) {
                    return mode;
                }
            }
            return UNKNOWN;
        }

        Mode(String val) {
            mVal = val;
        }

        public String value() {
            return mVal;
        }

        /**
         * Gson adapter for {@link Mode} enum.
         */
        public static class GsonAdapter extends TypeAdapter<Mode> {

            @Override
            public Mode read(JsonReader in) throws IOException {
                String mode = in.nextString();
                return Mode.fromString(mode);
            }

            @Override
            public void write(JsonWriter out, Mode mode) throws IOException {
                if (mode != null) {
                    out.value(mode.value());
                }
                else {
                    out.nullValue();
                }
            }
        }
    }

    /**
     * Returns {@link Mode}, which represents type of the scene.
     * @return Mode of the scene
     */
    Mode getSceneMode();

    /**
     * Returns int value, which represents brightness of scene
     * Possible values are 0-255
     * @return int value for brightness level
     */
    int getBrightness();

    /**
     * Returns int value, which represents contrast of scene
     * Possible values are 0-255
     * @return int value for contrast level
     */
    int getContrast();

    /**
     * Returns int value, which represents hue of scene
     * Possible values are 0-255
     * @return int value for hue level
     */
    int getHue();

    /**
     * Returns int value, which represents saturation of scene
     * Possible values are 0-255
     * @return int value for saturation level
     */
    int getSaturation();

    /**
     * Returns int value, which represents sharpness of scene
     * Possible values are 0-255
     * @return int value for sharpness level
     */
    int getSharpness();
}
