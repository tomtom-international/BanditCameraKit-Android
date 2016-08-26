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
import com.tomtom.camera.api.model.SensorData;
import com.tomtom.camera.api.model.SensorDataCollection;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Concrete implementation of interface {@link SensorDataCollection}
 */
@JsonAdapter(SensorDataCollectionV2.SensorDataCollectionV2Adapter.class)
class SensorDataCollectionV2 implements SensorDataCollection {

    private ArrayList<SensorDataV2> mSensorDataList;

    public SensorDataCollectionV2() {

    }

    public SensorDataCollectionV2(ArrayList<SensorDataV2> sensorDataList) {
        mSensorDataList = sensorDataList;
    }

    @Override
    public List<? extends SensorData> getSensorDataList() {
        return mSensorDataList;
    }

    static class SensorDataCollectionV2Adapter extends TypeAdapter<SensorDataCollectionV2> {

        @Override
        public void write(JsonWriter out, SensorDataCollectionV2 value) throws IOException {
            Gson gson = new Gson();
            out.beginArray();
            ArrayList<SensorDataV2> sensorDataList = (ArrayList<SensorDataV2>)value.getSensorDataList();
            for (SensorDataV2 sensorDataV2 : sensorDataList) {
                out.value(gson.toJson(sensorDataV2));
            }
            out.endArray();
        }

        @Override
        public SensorDataCollectionV2 read(JsonReader in) throws IOException {
            Gson gson = new Gson();
            ArrayList<SensorDataV2> sensorDataList = new ArrayList<>();
            in.beginArray();
            while(in.hasNext()) {
                SensorDataV2 sensorDataV2 = gson.fromJson(in, SensorDataV2.class);
                sensorDataList.add(sensorDataV2);
            }
            in.endArray();
            return new SensorDataCollectionV2(sensorDataList);
        }
    }
}
