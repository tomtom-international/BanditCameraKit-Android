package com.tomtom.camera.api.v2;

import com.google.gson.annotations.SerializedName;
import com.tomtom.camera.api.model.GpsActionData;
import com.tomtom.camera.api.model.SensorData;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by bokan on 12/15/15.
 */
class SensorDataV2 implements SensorData {
    @SerializedName("offset_secs")
    int mOffsetSecs;

    @SerializedName("offset_msecs")
    int mOffsetMilliSecs;

    @SerializedName("accel_mg")
    ArrayList<List<Float>> mAccelerometer;

    @SerializedName("gyro_degsec")
    ArrayList<List<Integer>> mGyro;

    @SerializedName("magnet_mt")
    ArrayList<List<Integer>> mCompass;

    @SerializedName("temp_mc")
    ArrayList<Integer> mTemp;

    @SerializedName("pressure_pa")
    ArrayList<Integer> mPressure;

    @SerializedName("gnss")
    ArrayList<GpsActionDataV2> mGps;

    @SerializedName("heart_rate")
    byte[] mHeartRate;

    @Override
    public int getOffsetSecs() {
        return mOffsetSecs;
    }

    public void setOffsetSecs(int offsetSecs) {
        mOffsetSecs = offsetSecs;
    }

    @Override
    public int getOffsetMillisecs() {
        return mOffsetMilliSecs;
    }

    public void setOffsetMilliSecs(int offsetMilliSecs) {
        mOffsetMilliSecs = offsetMilliSecs;
    }

    @Override
    public ArrayList<List<Float>> getAccelerometer() {
        return mAccelerometer;
    }

    public void setAccelerometer(ArrayList<List<Float>> accelerometer) {
        mAccelerometer = accelerometer;
    }

    @Override
    public ArrayList<List<Integer>> getGyro() {
        return mGyro;
    }

    public void setGyro(ArrayList<List<Integer>> gyro) {
        mGyro = gyro;
    }

    @Override
    public ArrayList<List<Integer>> getCompass() {
        return mCompass;
    }

    public void setCompass(ArrayList<List<Integer>> compass) {
        mCompass = compass;
    }

    @Override
    public ArrayList<Integer> getTemp() {
        return mTemp;
    }

    public void setTemp(ArrayList<Integer> temp) {
        mTemp = temp;
    }

    @Override
    public ArrayList<Integer> getPressure() {
        return mPressure;
    }

    public void setPressure(ArrayList<Integer> pressure) {
        mPressure = pressure;
    }

    @Override
    public ArrayList<? extends GpsActionData> getGps() {
        return mGps;
    }

    public void setGps(ArrayList<GpsActionDataV2> gps) {
        mGps = gps;
    }

    @Override
    public byte[] getHeartRate() {
        return mHeartRate;
    }

    public void setHeartRate(byte[] heartRate) {
        mHeartRate = heartRate;
    }
}
