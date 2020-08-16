/*
 * ==================================================
 * =  PROJECT     地下铁的故事
 * =  MODULE      地下铁的故事.app
 * =  FILE NAME   EmotionItemAdapter.kt
 * =  LAST MODIFIED BY PASSIONPENGUIN [8/14/20 1:40 AM]
 * ==================================================
 * Copyright 2020 PassionPenguin. All rights reserved.
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

package com.ditiezu.android.adapters

import android.app.Activity
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.graphics.drawable.toDrawable
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.ditiezu.android.R
import java.io.FileNotFoundException


class EmotionItem(
    val insert: String,
    val src: String
)

class EmotionItemAdapter(
    val activity: Activity,
    items: List<EmotionItem>,
    val name: String,
    val onClickListener: (insert: String) -> Unit
) : RecyclerView.Adapter<EmotionItemAdapter.ViewHolder>() {

    private val mInflater: LayoutInflater = LayoutInflater.from(activity)
    private var mItems: List<EmotionItem> = items

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(mInflater.inflate(R.layout.item_emotion, parent, false))
    }


    override fun getItemCount(): Int {
        return mItems.size
    }

    class ViewHolder(view: View) :
        RecyclerView.ViewHolder(view) {
        val image: ImageView = view.findViewById(R.id.image)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        try {
            Glide.with(activity)
                .load(BitmapFactory.decodeStream(activity.assets.open("webHelper/smiley/$name/" + mItems[position].src)))
                .fitCenter()
                .error(BitmapFactory.decodeStream(activity.assets.open("webHelper/smiley/xiaobai/1.gif")).toDrawable(activity.resources))
                .into(holder.image)
        } catch (fne: FileNotFoundException) {
        } catch (ignored: Exception) {
        }
        holder.itemView.setOnClickListener {
            onClickListener(mItems[position].insert)
        }
    }
}