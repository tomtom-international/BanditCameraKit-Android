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

package com.tomtom.camera.app.start;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.Toast;

import com.tomtom.camera.app.Camera;
import com.tomtom.camera.app.R;
import com.tomtom.camera.app.library.LibraryActivity;
import com.tomtom.camera.app.viewfinder.ViewFinderActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Activity launched , when application is started. It has 3 buttons for init camera rest api,
 * launching viewfinder activity for demonstration of recording and launching library activity ,
 * where recorded videos can be seen and previewed on thumbnail click
 */

public class StartActivity extends AppCompatActivity {

    @BindView(R.id.btn_initialize)
    Button mInitButton;
    @BindView(R.id.btn_viewfinder)
    Button mViewFinderButton;
    @BindView(R.id.btn_library)
    Button mLibraryButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        ButterKnife.bind(this);
    }

    @OnClick(R.id.btn_initialize)
    public void initialize() {
        Camera.init(new Camera.OnCameraInitCallback() {
            @Override
            public void onCameraInitialisationFinished(boolean isSuccessful) {
                if(isSuccessful) {
                    Toast.makeText(getApplicationContext(), "Successfully initialised Camera Api, proceed", Toast.LENGTH_SHORT).show();
                    mViewFinderButton.setEnabled(true);
                    mLibraryButton.setEnabled(true);
                }
                else {
                    Toast.makeText(getApplicationContext(), "Failed to init Camera Api, please check if device is connected to the camera", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @OnClick(R.id.btn_viewfinder)
    public void openViewFinder() {
        Intent viewfinderIntent = new Intent(this, ViewFinderActivity.class);
        startActivity(viewfinderIntent);
    }

    @OnClick(R.id.btn_library)
    public void openLibrary() {
        Intent libraryIntent = new Intent(this, LibraryActivity.class);
        startActivity(libraryIntent);
    }
}
