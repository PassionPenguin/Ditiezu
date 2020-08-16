/*
 * ==================================================
 * =  PROJECT     地下铁的故事
 * =  MODULE      地下铁的故事.app
 * =  FILE NAME   CategoryItemAdapter.kt
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
import android.view.MenuInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.PopupMenu
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.ditiezu.android.ForumActivity
import com.ditiezu.android.R
import com.ditiezu.android.data.categoryList

/**
 * @param mode: [false - ALL DATA] [true - FOLLOWED DATA]
 */

class CategoryItemAdapter(private val activity: Activity, IDs: MutableList<Int>, private val mode: Boolean = false) : RecyclerView.Adapter<CategoryItemAdapter.ViewHolder>() {

    private val mInflater: LayoutInflater = LayoutInflater.from(activity)
    var mItems: MutableList<Int> = IDs

    private val spHelper = com.passionpenguin.SPHelper(activity)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(mInflater.inflate(R.layout.item_category_item, parent, false))
    }

    override fun getItemCount(): Int {
        return mItems.size
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val name: TextView = view.findViewById(R.id.name)
        val description: TextView = view.findViewById(R.id.description)
        val icon: ImageView = view.findViewById(R.id.icon)
        val root: LinearLayout = view.findViewById(R.id.categoryItem)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = mItems[position]
        holder.name.text = categoryList[item].name
        holder.description.text = categoryList[item].description
        Glide.with(activity).load(categoryList[item].icon).into(holder.icon)

        holder.itemView.setOnClickListener {
            val i = Intent(activity, ForumActivity::class.java)
            i.putExtra("ID", mItems[position])
            i.flags = FLAG_ACTIVITY_NEW_TASK
            activity.startActivity(i)
        }

        holder.itemView.setOnLongClickListener {
            bindLongClickListener(position, holder)
            true
        }
    }

    private fun bindLongClickListener(position: Int, holder: ViewHolder) {
        val listID = mutableListOf<Int>()
        with(spHelper.getString("followed_category").split("-")) {
            val dataIDList = categoryList.map { item -> item.ID }
            this.forEach { dataID ->
                try {
                    val id = dataID.toInt()
                    if (dataIDList.contains(id))
                        listID.add(id)
                } catch (e: Exception) {
                    println(e)
                }
            }
        }
        if (listID.contains(categoryList[mItems[position]].ID)) {
            // 当前长按的元素是已经收藏的
            val popupMenu = PopupMenu(activity, holder.root)
            MenuInflater(activity).inflate(R.menu.unfollow_item, popupMenu.menu)

            popupMenu.setOnMenuItemClickListener { item ->
                if (item.itemId == R.id.unfollow_submit) {
                    listID.removeAt(listID.indexOf(categoryList[mItems[position]].ID))
                    if(mode) {
                        mItems.removeAt(position)
                        notifyItemRemoved(position)
                        notifyItemRangeChanged(position, listID.size - position)
                    }
                    spHelper.edit("followed_category", listID.joinToString("-"))
                    println(spHelper.getString("followed_category"))
                    println(listID)
                }
                false
            }
            popupMenu.show()
        } else if (!mode) {
            // 全部数据
            val popupMenu = PopupMenu(activity, holder.root)
            MenuInflater(activity).inflate(R.menu.follow_item, popupMenu.menu)

            popupMenu.setOnMenuItemClickListener { item ->
                if (item.itemId == R.id.follow_submit) {
                    listID.add(categoryList[mItems[position]].ID)
                    println(listID)
                    spHelper.edit("followed_category", listID.joinToString("-"))
                    println(spHelper.getString("followed_category"))
                }
                false
            }
            popupMenu.show()
        }
    }
}