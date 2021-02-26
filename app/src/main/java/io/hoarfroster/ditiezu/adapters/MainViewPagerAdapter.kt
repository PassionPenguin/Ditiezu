package io.hoarfroster.ditiezu.adapters

import android.util.SparseArray
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import io.hoarfroster.ditiezu.pages.category.CategoryFragment
import io.hoarfroster.ditiezu.pages.main.MainFragment
import io.hoarfroster.ditiezu.pages.notification.NotificationFragment
import io.hoarfroster.ditiezu.pages.setting.SettingFragment

class MainViewPagerAdapter(fragActivity: FragmentActivity) :
    FragmentStateAdapter(fragActivity) {

    private val fragments: SparseArray<Fragment> = SparseArray()

    init {
        fragments.put(PAGE_HOME, MainFragment())
        fragments.put(PAGE_FIND, CategoryFragment())
        fragments.put(PAGE_INDICATOR, NotificationFragment())
        fragments.put(PAGE_OTHERS, SettingFragment())
    }

    override fun createFragment(position: Int): Fragment {
        return fragments[position]
    }

    override fun getItemCount(): Int {
        return fragments.size()
    }

    companion object {

        const val PAGE_HOME = 0

        const val PAGE_FIND = 1

        const val PAGE_INDICATOR = 2

        const val PAGE_OTHERS = 3

    }
}