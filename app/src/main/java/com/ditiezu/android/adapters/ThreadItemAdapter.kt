/*
 * ==================================================
 * =  PROJECT     地下铁的故事
 * =  MODULE      地下铁的故事.app
 * =  FILE NAME   ThreadItemAdapter.kt
 * =  LAST MODIFIED BY PASSIONPENGUIN [1/5/21, 11:11 PM]
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
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.ditiezu.android.R
import com.ditiezu.android.ViewThread
import com.ditiezu.android.views.main.AccountActivity
import com.ditiezu.android.views.main.category.CategoryActivity
import com.ditiezu.android.views.main.notifications.NotificationActivity
import kotlinx.android.synthetic.main.item_home_mixed.view.*
import kotlinx.android.synthetic.main.item_thread_item.view.*

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

class ThreadItemAdapter(private val activity: Activity, items: List<ThreadItem>, private val isHomeMixed: Boolean = false) : RecyclerView.Adapter<ThreadItemAdapter.ViewHolder>() {

    private val mInflater: LayoutInflater = LayoutInflater.from(activity)
    private var mItems: List<ThreadItem> = items
    override fun getItemViewType(position: Int): Int {
        return if (position == 0 && isHomeMixed) 1 else 0
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(mInflater.inflate(if (viewType == 1) R.layout.item_home_mixed else R.layout.item_thread_item, parent, false))
    }

    override fun getItemCount(): Int {
        return mItems.size + if (isHomeMixed) 1 else 0
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var base: ViewGroup = if (view.findViewById<LinearLayout>(R.id.threadItem) != null) view.threadItem else view.homeMixed
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (position == 0 && isHomeMixed) {
            holder.base.categoryItem.setOnClickListener {
                activity.startActivity(Intent(activity, CategoryActivity::class.java))
            }
            holder.base.notificationItem.setOnClickListener {
                activity.startActivity(Intent(activity, NotificationActivity::class.java))
            }
            holder.base.accountItem.setOnClickListener {
                activity.startActivity(Intent(activity, AccountActivity::class.java))
            }
            return
        }
        val item = mItems[position - if (isHomeMixed) 1 else 0]
        holder.base.title.text = when (item.meta) {
            null -> item.title
            else -> {
                "[${item.meta}]${item.title}"
            }
        }
        if (item.content.isEmpty()) holder.base.content.visibility = View.GONE
        holder.base.content.text = item.content
        holder.base.postTime.text = item.time
        when (item.views) {
            null -> holder.base.viewsWrap.visibility = View.GONE
            else -> {
                holder.base.views.text = item.views
            }
        }
        when (item.replies) {
            null -> holder.base.repliesWrap.visibility = View.GONE
            else -> {
                holder.base.replies.text = item.replies
            }
        }
        holder.base.authorName.text = item.authorName
        if (item.authorId == -1)
            holder.base.avatar.visibility = View.GONE
        else Glide.with(activity)
            .load("http://www.ditiezu.com/uc_server/avatar.php?mod=avatar&uid=${item.authorId}")
            .placeholder(R.mipmap.noavatar_middle)
            .error(R.mipmap.noavatar_middle)
            .apply(RequestOptions.bitmapTransform(RoundedCorners(8)))
            .into(holder.base.avatar)

        holder.itemView.setOnClickListener {
            val i = Intent(activity, ViewThread::class.java)
            i.putExtra("tid", item.target)
            i.flags = FLAG_ACTIVITY_NEW_TASK
            activity.startActivity(i)
        }
    }
}