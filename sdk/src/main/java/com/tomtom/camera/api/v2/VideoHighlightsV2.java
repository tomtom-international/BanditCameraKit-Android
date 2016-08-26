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

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.annotations.JsonAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import com.tomtom.camera.api.model.Highlight;
import com.tomtom.camera.api.model.VideoHighlights;
import com.tomtom.camera.util.ApiUtil;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Concrete implementation of interface {@link VideoHighlights}
 */

@JsonAdapter(VideoHighlightsV2.VideoHighlightsV2Adapter.class)
class VideoHighlightsV2 implements VideoHighlights {

    ArrayList<HighlightV2> mItems;

    @Override
    public ArrayList<? extends Highlight> getVideoHighlights() {
        if(mItems != null) {
            return mItems;
        }
        return new ArrayList<Highlight>(0);
    }

    static class VideoHighlightsV2Adapter extends TypeAdapter<VideoHighlightsV2> {

        @Override
        public void write(JsonWriter out, VideoHighlightsV2 value) throws IOException {
            out.beginArray();
            ArrayList<? extends Highlight> highlights = value.getVideoHighlights();
            if(highlights != null) {
                Gson gson = ApiUtil.getDateHandlingGson();
                int size = highlights.size();
                for(int i = 0; i < size; i++) {
                    out.jsonValue(gson.toJson(highlights.get(i)));
                }
            }
            out.endArray();
        }

        @Override
        public VideoHighlightsV2 read(JsonReader in) throws IOException {
            VideoHighlightsV2 highlightsV2 = new VideoHighlightsV2();
            ArrayList<HighlightV2> highlights = new ArrayList<>();
            Gson gson = ApiUtil.getDateHandlingGson();
            in.beginArray();
            while(in.hasNext()) {
                HighlightV2 highlight = gson.fromJson(in, HighlightV2.class);
                highlights.add(highlight);
            }
            in.endArray();
            highlightsV2.mItems = highlights;
            return highlightsV2;
        }
    }
}
