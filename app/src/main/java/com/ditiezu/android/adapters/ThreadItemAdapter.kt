/*
 * ==================================================
 * =  PROJECT     地下铁的故事
 * =  MODULE      地下铁的故事.app
 * =  FILE NAME   ThreadItemAdapter.kt
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
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.ditiezu.android.R
import com.ditiezu.android.ViewThread

class ThreadItem(
    val authorId: Int,
    val title: String,
    val content: String,
    val authorName: String,
    val time: String,
    val meta: String? = null,
    val views: String? = null,
    val replies: String? = null,
    val target: Int
)

class ThreadItemAdapter(private val activity: Activity, items: List<ThreadItem>) : RecyclerView.Adapter<ThreadItemAdapter.ViewHolder>() {

    private val mInflater: LayoutInflater = LayoutInflater.from(activity)
    private var mItems: List<ThreadItem> = items

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(mInflater.inflate(R.layout.item_thread_item, parent, false))
    }

    override fun getItemCount(): Int {
        return mItems.size
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val title: TextView = view.findViewById(R.id.threadTitle)
        val content: TextView = view.findViewById(R.id.threadContent)
        val avatar: ImageView = view.findViewById(R.id.avatar)
        val authorName: TextView = view.findViewById(R.id.threadAuthorName)
        val postTime: TextView = view.findViewById(R.id.threadPostTime)
        val viewsWrap: ConstraintLayout = view.findViewById(R.id.viewsWrap)
        val views: TextView = view.findViewById(R.id.views)
        val repliesWrap: ConstraintLayout = view.findViewById(R.id.repliesWrap)
        val replies: TextView = view.findViewById(R.id.replies)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = mItems[position]
        holder.title.text = when (item.meta) {
            null -> item.title
            else -> {
                "[${item.meta}]${item.title}"
            }
        }
        if (item.content.isEmpty()) holder.content.visibility = View.GONE
        holder.content.text = item.content
        holder.postTime.text = item.time
        when (item.views) {
            null -> holder.viewsWrap.visibility = View.GONE
            else -> {
                holder.views.text = item.views
            }
        }
        when (item.replies) {
            null -> holder.repliesWrap.visibility = View.GONE
            else -> {
                holder.replies.text = item.replies
            }
        }
        holder.authorName.text = item.authorName
        if (item.authorId == -1)
            holder.avatar.visibility = View.GONE
        else Glide.with(activity)
            .load("http://www.ditiezu.com/uc_server/avatar.php?mod=avatar&uid=${item.authorId}")
            .placeholder(R.mipmap.noavatar_middle)
            .error(R.mipmap.noavatar_middle)
            .apply(RequestOptions.bitmapTransform(RoundedCorners(8)))
            .into(holder.avatar)

        holder.itemView.setOnClickListener {
            val i = Intent(activity, ViewThread::class.java)
            i.putExtra("tid", mItems[position].target)
            i.flags = FLAG_ACTIVITY_NEW_TASK
            activity.startActivity(i)
        }

        holder.itemView.setOnLongClickListener {
//                if (position != 0 || !(isHome || withHeader)) {
//                }
            true
        }
    }
}