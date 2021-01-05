/*
 * ==================================================
 * =  PROJECT     地下铁的故事
 * =  MODULE      地下铁的故事.app
 * =  FILE NAME   NotificationAdapter.kt
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

package com.ditiezu.android.adapters

import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.ditiezu.android.R
import com.ditiezu.android.ViewThread

class NotificationItem(
    val imageUrl: String,
    val value: String,
    val description: String? = null,
    val time: String,
    val tid: String? = "-1",
    val page: String? = "1"
)

class NotificationItemAdapter(val activity: Activity, items: List<NotificationItem>) : RecyclerView.Adapter<NotificationItemAdapter.ViewHolder>() {

    private val mInflater: LayoutInflater = LayoutInflater.from(activity)
    private var mItems: List<NotificationItem> = items

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            mInflater.inflate(
                R.layout.item_notification,
                parent,
                false
            )
        )
    }


    override fun getItemCount(): Int {
        return mItems.size
    }

    class ViewHolder(view: View) :
        RecyclerView.ViewHolder(view) {
        val avatar: ImageView = view.findViewById(R.id.avatar)
        val value: TextView = view.findViewById(R.id.notification_value)
        val meta: TextView = view.findViewById(R.id.notification_meta)
        val extraInfo: TextView = view.findViewById(R.id.extraInfo)
    }

    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int
    ) {
        val notification = mItems[position]
        holder.value.text = notification.value
        holder.meta.text = notification.time
        if (notification.description != null)
            holder.extraInfo.text = notification.description
        else holder.extraInfo.visibility = View.GONE
        if (notification.imageUrl.isEmpty())
            Glide.with(activity)
                .load(R.mipmap.noavatar_middle)
                .apply(RequestOptions.bitmapTransform(RoundedCorners(8)))
                .into(holder.avatar)
        else Glide.with(activity)
            .load(notification.imageUrl)
            .placeholder(R.mipmap.noavatar_middle)
            .error(R.mipmap.noavatar_middle)
            .apply(RequestOptions.bitmapTransform(RoundedCorners(8)))
            .into(holder.avatar)

        holder.itemView.setOnClickListener {
            if (notification.tid != "-1") {
                val i = Intent(activity, ViewThread::class.java)
                i.putExtra("tid", notification.tid?.toInt())
                i.putExtra("page", notification.page?.toInt())
                activity.startActivity(i)
            }
        }
    }
}