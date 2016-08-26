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

import com.google.gson.annotations.SerializedName;
import com.tomtom.camera.api.model.ViewfinderStatus;

/**
 * Concrete implementation of interface {@link ViewfinderStatus}
 */
class ViewfinderStatusV2 implements ViewfinderStatus {

    @SerializedName("viewfinder_active")
    private boolean mActive = false;

    @SerializedName("viewfinder_streaming_port")
    private int mStreamingPort = 4001;

    public static ViewfinderStatusV2 createStart(int streamingPort){
        return new ViewfinderStatusV2(true, streamingPort);
    }

    public static ViewfinderStatusV2 createStop(){
        return new ViewfinderStatusV2(false, -1);
    }

    private ViewfinderStatusV2(boolean start, int streamingPort){
        mActive = start;
        mStreamingPort = streamingPort;
    }

    @Override
    public boolean isActive(){
        return mActive;
    }

    @Override
    public int getStreamingPort(){
        return mStreamingPort;
    }

}
