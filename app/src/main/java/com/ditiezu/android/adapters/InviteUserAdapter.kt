/*
 * ==================================================
 * =  PROJECT     地下铁的故事
 * =  MODULE      地下铁的故事.app
 * =  FILE NAME   InviteUserAdapter.kt
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
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.ditiezu.android.R

class User(
    val uid: Int,
    val name: String,
    val avatarUrl: String,
    var isChecked: Boolean = false
)

class InviteUserAdapter(val activity: Activity, items: List<User>) : RecyclerView.Adapter<InviteUserAdapter.ViewHolder>() {
    private val mInflater: LayoutInflater = LayoutInflater.from(activity)
    private var mItems: List<User> = items

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return with(ViewHolder(
            mInflater.inflate(
                R.layout.item_invite,
                parent,
                false
            )
        )) {
            this.setIsRecyclable(false)
            this
        }
    }

    override fun getItemCount(): Int {
        return mItems.size
    }

    class ViewHolder(view: View) :
        RecyclerView.ViewHolder(view) {
        var userName: TextView = view.findViewById(R.id.userName)
        var avatar: ImageView = view.findViewById(R.id.avatar)
        var checkbox: CheckBox = view.findViewById(R.id.checkbox)
    }

    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int
    ) {
        holder.userName.text = mItems[position].name
        Glide.with(activity)
            .load(mItems[position].avatarUrl)
            .apply(RequestOptions.bitmapTransform(RoundedCorners(8)))
            .into(holder.avatar)
        holder.checkbox.setOnCheckedChangeListener { _, b ->
            mItems[position].isChecked = b
        }
        holder.avatar.setOnClickListener {
            holder.checkbox.performClick()
        }
        holder.checkbox.isChecked = mItems[position].isChecked
    }
}