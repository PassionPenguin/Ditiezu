/*
 * ==================================================
 * =  PROJECT     地下铁的故事
 * =  MODULE      地下铁的故事.app
 * =  FILE NAME   MainActivity.kt
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

package com.ditiezu.android

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentPagerAdapter
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import androidx.viewpager.widget.ViewPager
import com.ditiezu.android.fragments.AccountFragment
import com.ditiezu.android.fragments.MainScreenFragment
import com.ditiezu.android.fragments.category.CategoryFragment
import com.ditiezu.android.fragments.notifications.NotificationsFragment
import com.ditiezu.android.fragments.notifications.Read
import com.ditiezu.android.fragments.notifications.Unread
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.tabs.TabLayout
import com.passionpenguin.SPHelper

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val sectionsPagerAdapter = object : FragmentPagerAdapter(supportFragmentManager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
            override fun getCount(): Int {
                return 4
            }

            override fun getItem(position: Int): Fragment {
                return arrayOf(MainScreenFragment(), CategoryFragment(), NotificationsFragment(), AccountFragment())[position]
            }
        }

        val viewPager: ViewPager = findViewById(R.id.view_pager)
        val tabs = findViewById<BottomNavigationView>(R.id.nav_view)
        viewPager.adapter = sectionsPagerAdapter
        viewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
            }

            override fun onPageSelected(position: Int) {
                when (position) {
                    0 -> tabs.menu.findItem(R.id.navigation_home).isChecked = true
                    1 -> tabs.menu.findItem(R.id.navigation_category).isChecked = true
                    2 -> tabs.menu.findItem(R.id.navigation_notifications).isChecked = true
                    3 -> tabs.menu.findItem(R.id.navigation_account).isChecked = true
                }
            }

            override fun onPageScrollStateChanged(state: Int) {
            }

        })

        tabs.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.navigation_home -> viewPager.currentItem = 0
                R.id.navigation_category -> viewPager.currentItem = 1
                R.id.navigation_notifications -> viewPager.currentItem = 2
                R.id.navigation_account -> viewPager.currentItem = 3
            }
            true
        }
    }
}