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

import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;
import com.tomtom.camera.api.CameraApi;
import com.tomtom.camera.api.model.Video;
import com.tomtom.camera.app.Camera;
import com.tomtom.camera.app.R;
import com.tomtom.camera.app.preview.PreviewActivity;

import java.util.List;

/**
 * RecyclerView Adapter extension, used for library screen
 */

public class LibraryAdapter extends RecyclerView.Adapter<LibraryAdapter.LibraryViewHolder> {

    final List<Video> mDataset;

    public static class LibraryViewHolder extends RecyclerView.ViewHolder {

        public ImageView mThumbnail;

        public LibraryViewHolder(View v) {
            super(v);
            mThumbnail = (ImageView) v.findViewById(R.id.thumbnail);
            mThumbnail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Video video = (Video) mThumbnail.getTag();
                    Intent previewIntent = new Intent(mThumbnail.getContext(), PreviewActivity.class);
                    previewIntent.putExtra(PreviewActivity.EXTRA_VIDEO_JSON, video.toJsonString());
                    mThumbnail.getContext().startActivity(previewIntent);
                }
            });
        }
    }

    public LibraryAdapter(List dataset) {
        mDataset = dataset;
    }

    @Override
    public LibraryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        CardView cardView = (CardView) inflater.inflate(R.layout.library_item, parent, false);
        return  new LibraryViewHolder(cardView);
    }

    @Override
    public void onBindViewHolder(LibraryViewHolder holder, int position) {
        String thumbnailUrl = new StringBuilder()
                .append(CameraApi.BASE_URL)
                .append("/")
                .append(Camera.getCameraApiVersion().getVersion())
                .append("/")
                .append(mDataset.get(position).getThumbnailUrl())
                .toString();
        holder.mThumbnail.setTag(mDataset.get(position));

        Picasso.with(holder.mThumbnail.getContext()).load(thumbnailUrl)
                .into(holder.mThumbnail);
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }
}




