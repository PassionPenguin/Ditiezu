/*
 * ==================================================
 * =  PROJECT     地下铁的故事
 * =  MODULE      地下铁的故事.picture_library
 * =  FILE NAME   PictureWeChatPreviewGalleryAdapter
 * =  LAST MODIFIED BY PASSIONPENGUIN [1/5/21, 9:25 PM]
 * ==================================================
 * Copyright 2021 PassionPenguin. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.luck.picture.lib.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.luck.picture.lib.R;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.config.PictureSelectionConfig;
import com.luck.picture.lib.entity.LocalMedia;

import java.util.ArrayList;
import java.util.List;

/**
 * @author：luck
 * @date：2019-11-30 20:50
 * @describe：WeChat style selected after image preview
 */
public class PictureWeChatPreviewGalleryAdapter
        extends RecyclerView.Adapter<PictureWeChatPreviewGalleryAdapter.ViewHolder> {
    private List<LocalMedia> list;
    private final PictureSelectionConfig config;
    private OnItemClickListener listener;

    public PictureWeChatPreviewGalleryAdapter(PictureSelectionConfig config) {
        super();
        this.config = config;
    }

    public void setNewData(List<LocalMedia> data) {
        this.list = data == null ? new ArrayList<>() : data;
        notifyDataSetChanged();
    }

    public void addSingleMediaToData(LocalMedia media) {
        if (this.list != null) {
            list.clear();
            list.add(media);
            notifyDataSetChanged();
        }
    }

    public void removeMediaToData(LocalMedia media) {
        if (this.list != null && this.list.size() > 0) {
            this.list.remove(media);
            notifyDataSetChanged();
        }
    }

    public boolean isDataEmpty() {
        return list == null || list.size() == 0;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.picture_wechat_preview_gallery, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        LocalMedia item = getItem(position);
        if (item != null) {
            holder.viewBorder.setVisibility(item.isChecked() ? View.VISIBLE : View.GONE);
            if (config != null && PictureSelectionConfig.imageEngine != null) {
                PictureSelectionConfig.imageEngine.loadImage(holder.itemView.getContext(), item.getPath(), holder.ivImage);
            }
            holder.ivPlay.setVisibility(PictureMimeType.isHasVideo(item.getMimeType()) ? View.VISIBLE : View.GONE);
            holder.itemView.setOnClickListener(v -> {
                if (listener != null && holder.getAdapterPosition() >= 0) {
                    listener.onItemClick(holder.getAdapterPosition(), getItem(position), v);
                }
            });
        }
    }

    public LocalMedia getItem(int position) {
        return list != null && list.size() > 0 ? list.get(position) : null;
    }

    public void setItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    @Override
    public int getItemCount() {
        return list != null ? list.size() : 0;
    }

    public interface OnItemClickListener {
        void onItemClick(int position, LocalMedia media, View v);
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivImage;
        ImageView ivPlay;
        View viewBorder;

        public ViewHolder(View itemView) {
            super(itemView);
            ivImage = itemView.findViewById(R.id.ivImage);
            ivPlay = itemView.findViewById(R.id.ivPlay);
            viewBorder = itemView.findViewById(R.id.viewBorder);
        }
    }
}
