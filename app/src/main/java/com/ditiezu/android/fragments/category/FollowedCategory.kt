/*
 * ==================================================
 * =  PROJECT     地下铁的故事
 * =  MODULE      地下铁的故事.app
 * =  FILE NAME   FollowedCategory.kt
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

package com.ditiezu.android.fragments.category

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentPagerAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import com.ditiezu.android.R
import com.ditiezu.android.adapters.CategoryItemAdapter
import com.ditiezu.android.data.categoryList
import com.google.android.material.tabs.TabLayout
import com.passionpenguin.SPHelper

class FollowedCategory : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_followed_category, container, false)
    }

    override fun onResume() {
        super.onResume()

        activity?.let {
            val spHelper = SPHelper(it)
            val list = it.findViewById<RecyclerView>(R.id.followed_list)

            with(spHelper.getString("followed_category").split("-")) {
                val listID = mutableListOf<Int>()
                val dataIDList = categoryList.map { item -> item.ID }
                this.forEach { dataID ->
                    try {
                        val id = dataID.toInt()
                        if (dataIDList.contains(id))
                            listID.add(dataIDList.indexOf(id))
                    } catch (e: Exception) {
                        println(e)
                    }
                }
                when {
                    listID.isEmpty() -> {
                        list.visibility = View.GONE
                        it.findViewById<TextView>(R.id.tip).visibility = View.VISIBLE
                    }
                    else -> {
                        list.visibility = View.VISIBLE
                        it.findViewById<TextView>(R.id.tip).visibility = View.GONE
                        list.adapter = CategoryItemAdapter(it, listID, true)
                        list.layoutManager = LinearLayoutManager(it, RecyclerView.VERTICAL, false)
                    }
                }
            }
        }
    }
}