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

package com.tomtom.camera.app.library;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.tomtom.camera.api.CameraApiCallback;
import com.tomtom.camera.api.model.Video;
import com.tomtom.camera.api.model.Videos;
import com.tomtom.camera.app.Camera;
import com.tomtom.camera.app.R;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Activity launched on library button press, on start screen. Screen contains recorded videos,
 * which can be previewed on thumbnail press
 */

public class LibraryActivity extends AppCompatActivity {

    @BindView(R.id.library_recycler_view)
    RecyclerView mLibraryRecyclerView;

    private RecyclerView.Adapter mAdapter;
    private List<Video> mVideos = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_library);
        ButterKnife.bind(this);
        mLibraryRecyclerView.setHasFixedSize(true);
        mLibraryRecyclerView.setLayoutManager(new GridLayoutManager(this, 3));
        getVideos();
    }

    private void getVideos() {
        Camera.getCameraApi().getVideoFiles(24, new CameraApiCallback<Videos>() {
            @Override
            public void success(Videos videoFiles) {
                mVideos.clear();
                mVideos.addAll(videoFiles.getItems());
                mAdapter = new LibraryAdapter(mVideos);
                mLibraryRecyclerView.setAdapter(mAdapter);
            }

            @Override
            public void error(int statusCode) {

            }

            @Override
            public void failure(Throwable throwable) {

            }
        });
    }
}
