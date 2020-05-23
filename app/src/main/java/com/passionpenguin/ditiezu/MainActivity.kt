package com.passionpenguin.ditiezu

import android.os.Bundle
import android.view.View
import android.view.animation.AccelerateInterpolator
import android.view.animation.AlphaAnimation
import android.view.animation.AnimationSet
import android.view.animation.DecelerateInterpolator
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val homeButton: LinearLayout = findViewById(R.id.HomeButton)
        val categoryButton: LinearLayout = findViewById(R.id.CategoryButton)
        val notificationButton: LinearLayout = findViewById(R.id.NotificationButton)
        val accountButton: LinearLayout = findViewById(R.id.AccountButton)
        val categoryListContainer: LinearLayout = findViewById(R.id.CategoryListContainer)
        val categoryList: ListView = findViewById(R.id.CategoryList)
        fun string(int: Int): String {
            return resources.getString(int)
        }

        val list = mutableListOf<Model>()

        list.add(
            Model(
                string(R.string.category_Beijing),
                "",
                R.mipmap.ic_launcher
            )
        )
        list.add(
            Model(
                string(R.string.category_Tianjin),
                "",
                R.mipmap.ic_launcher
            )
        )
        list.add(
            Model(
                string(R.string.category_Shanghai),
                "",
                R.mipmap.ic_launcher
            )
        )
        list.add(
            Model(
                string(R.string.category_Guangzhou),
                "",
                R.mipmap.ic_launcher
            )
        )
        list.add(
            Model(
                string(R.string.category_Changchun),
                "",
                R.mipmap.ic_launcher
            )
        )
        list.add(
            Model(
                string(R.string.category_Dalian),
                "",
                R.mipmap.ic_launcher
            )
        )
        list.add(
            Model(
                string(R.string.category_Wuhan),
                "",
                R.mipmap.ic_launcher
            )
        )
        list.add(
            Model(
                string(R.string.category_Chongqing),
                "",
                R.mipmap.ic_launcher
            )
        )
        list.add(
            Model(
                string(R.string.category_Shenzhen),
                "",
                R.mipmap.ic_launcher
            )
        )
        list.add(
            Model(
                string(R.string.category_Nanjing),
                "",
                R.mipmap.ic_launcher
            )
        )
        list.add(
            Model(
                string(R.string.category_Chengdu),
                "",
                R.mipmap.ic_launcher
            )
        )
        list.add(
            Model(
                string(R.string.category_Shenyang),
                "",
                R.mipmap.ic_launcher
            )
        )
        list.add(
            Model(
                string(R.string.category_Foshan),
                "",
                R.mipmap.ic_launcher
            )
        )
        list.add(
            Model(
                string(R.string.category_Xian),
                "",
                R.mipmap.ic_launcher
            )
        )
        list.add(
            Model(
                string(R.string.category_Suzhou),
                "",
                R.mipmap.ic_launcher
            )
        )
        list.add(
            Model(
                string(R.string.category_Kunming),
                "",
                R.mipmap.ic_launcher
            )
        )
        list.add(
            Model(
                string(R.string.category_Hangzhou),
                "",
                R.mipmap.ic_launcher
            )
        )
        list.add(
            Model(
                string(R.string.category_Harbin),
                "",
                R.mipmap.ic_launcher
            )
        )
        list.add(
            Model(
                string(R.string.category_Zhengzhou),
                "",
                R.mipmap.ic_launcher
            )
        )
        list.add(
            Model(
                string(R.string.category_Changsha),
                "",
                R.mipmap.ic_launcher
            )
        )
        list.add(
            Model(
                string(R.string.category_Ningbo),
                "",
                R.mipmap.ic_launcher
            )
        )
        list.add(
            Model(
                string(R.string.category_Wuxi),
                "",
                R.mipmap.ic_launcher
            )
        )
        list.add(
            Model(
                string(R.string.category_Qingdao),
                "",
                R.mipmap.ic_launcher
            )
        )
        list.add(
            Model(
                string(R.string.category_Nanchang),
                "",
                R.mipmap.ic_launcher
            )
        )
        list.add(
            Model(
                string(R.string.category_Fuzhou),
                "",
                R.mipmap.ic_launcher
            )
        )

        list.add(
            Model(
                string(R.string.category_Dongguan),
                "",
                R.mipmap.ic_launcher
            )
        )
        list.add(
            Model(
                string(R.string.category_Nanning),
                "",
                R.mipmap.ic_launcher
            )
        )
        list.add(
            Model(
                string(R.string.category_Hefei),
                "",
                R.mipmap.ic_launcher
            )
        )
        list.add(
            Model(
                string(R.string.category_Shijiazhuang),
                "",
                R.mipmap.ic_launcher
            )
        )
        list.add(
            Model(
                string(R.string.category_Guiyang),
                "",
                R.mipmap.ic_launcher
            )
        )
        list.add(
            Model(
                string(R.string.category_Xiamen),
                "",
                R.mipmap.ic_launcher
            )
        )
        list.add(
            Model(
                string(R.string.category_Urumqi),
                "",
                R.mipmap.ic_launcher
            )
        )
        list.add(
            Model(
                string(R.string.category_Wenzhou),
                "",
                R.mipmap.ic_launcher
            )
        )
        list.add(
            Model(
                string(R.string.category_Jinan),
                "",
                R.mipmap.ic_launcher
            )
        )
        list.add(
            Model(
                string(R.string.category_Lanzhou),
                "",
                R.mipmap.ic_launcher
            )
        )
        list.add(
            Model(
                string(R.string.category_Changzhou),
                "",
                R.mipmap.ic_launcher
            )
        )
        list.add(
            Model(
                string(R.string.category_Xuzhou),
                "",
                R.mipmap.ic_launcher
            )
        )
        list.add(
            Model(
                string(R.string.category_Hongkong),
                "",
                R.mipmap.ic_launcher
            )
        )
        list.add(
            Model(
                string(R.string.category_Macau),
                "",
                R.mipmap.ic_launcher
            )
        )
        list.add(
            Model(
                string(R.string.category_Taiwan),
                "",
                R.mipmap.ic_launcher
            )
        )
        list.add(
            Model(
                string(R.string.category_Oversea),
                "",
                R.mipmap.ic_launcher
            )
        )
        list.add(
            Model(
                string(R.string.category_Comprehensive),
                "",
                R.mipmap.ic_launcher
            )
        )

        CategoryList.adapter = CategoryAdapter(this, R.layout.category_popup, list)

        CategoryList.setOnItemClickListener { _, _, position, _ ->
            CategoryListContainer.visibility = View.GONE
            Toast.makeText(this@MainActivity, position.toString(), Toast.LENGTH_LONG).show()
        }

        fun bottomButtonHighlight(clear: Array<LinearLayout>, add: LinearLayout) {
            clear.forEach {
                (it.getChildAt(0) as ImageView).setColorFilter(
                    ContextCompat.getColor(applicationContext, R.color.black),
                    android.graphics.PorterDuff.Mode.SRC_IN
                )
                (it.getChildAt(1) as TextView).setTextColor(
                    ContextCompat.getColor(
                        applicationContext,
                        R.color.black
                    )
                )
            }
            (add.getChildAt(0) as ImageView).setColorFilter(
                ContextCompat.getColor(applicationContext, R.color.primary700),
                android.graphics.PorterDuff.Mode.SRC_IN
            )
            (add.getChildAt(1) as TextView).setTextColor(
                ContextCompat.getColor(
                    applicationContext,
                    R.color.primary700
                )
            )
        }

        homeButton.setOnClickListener {
            bottomButtonHighlight(
                arrayOf(categoryButton, notificationButton, accountButton),
                homeButton
            )
        }
        categoryButton.setOnClickListener {
            bottomButtonHighlight(
                arrayOf(homeButton, notificationButton, accountButton),
                categoryButton
            )

            CategoryListContainer.visibility = View.VISIBLE
        }
        notificationButton.setOnClickListener {
            bottomButtonHighlight(
                arrayOf(homeButton, categoryButton, accountButton),
                notificationButton
            )
        }
        accountButton.setOnClickListener {
            bottomButtonHighlight(
                arrayOf(homeButton, notificationButton, categoryButton),
                accountButton
            )
        }
    }
}