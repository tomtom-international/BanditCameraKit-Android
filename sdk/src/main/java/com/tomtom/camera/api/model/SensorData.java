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

import java.util.ArrayList;
import java.util.List;

/**
 * This interface corresponds with Sensor Data model and defines functions to retrieve data from
 * different type of sensors
 */
public interface SensorData {

    /**
     *  Returns int value of offset (in seconds) from beginning of a video marking the point
     *  from which sensor data is delivered.
     * @return int value of offset
     */
    int getOffsetSecs();

    /**
     *  Returns int value of offset (in milliseconds) from beginning of a video marking the point
     *  from which sensor data is delivered.
     * @return int value of offset
     */
    int getOffsetMillisecs();

    /**
     *  Returns values sampled from accelerometer. Accelerometer samples for all three axes in form
     *  [x,y,z] represented in milli G units.    Example value: [500,-200,1200]

     * @return list of float values of accelerometer data
     */
    ArrayList<List<Float>> getAccelerometer();

    /**
     *  Returns values sampled from gyroscope. Gyro samples for all three axes in form [x,y,z].
     *  Each value represents angular rotation speed around given axes in deg/sec units
     *  Example value: [408,-142,-435]
     * @return list of int values of gyroscope data
     */
    ArrayList<List<Integer>> getGyro();

    /**
     *  Returns values sampled from magnetometer. Magnetometer samples for all three axes in form
     *  [x,y,z]  represented in milli T (Tesla) units. Example value: [1408,-1242,-3435]
     * @return list of int values of magnetometer data
     */
    ArrayList<List<Integer>> getCompass();

    /**
     *  Returns values of temperature in mili-Celsius degrees taken inside of a camera housing
     *  sampled from temperature sensor.
      * @return list of int values from temperature senso
     */
    ArrayList<Integer> getTemp();

    /**
     *  Returns values of barometer samples in Pa
     * @return list of int values representing pressure samples
     */
    ArrayList<Integer> getPressure();

    /**
     *  Returns values of GNSS data as list of {@link GpsActionData}
     *  for current video in different point of time
     * @return array list of {@link GpsActionData}
     */

    ArrayList<? extends GpsActionData> getGps();

    /**
     *  Returns values of heart rate samples.
     * @return array of bytes from heart rate sensor
     */
    byte[] getHeartRate();
}
