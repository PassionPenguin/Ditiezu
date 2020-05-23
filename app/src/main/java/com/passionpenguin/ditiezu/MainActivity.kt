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
                R.drawable.beijing
            )
        )
        list.add(
            Model(
                string(R.string.category_Tianjin),
                "",
                R.drawable.tianjin
            )
        )
        list.add(
            Model(
                string(R.string.category_Shanghai),
                "",
                R.drawable.shanghai
            )
        )
        list.add(
            Model(
                string(R.string.category_Guangzhou),
                "",
                R.drawable.guangzhou
            )
        )
        list.add(
            Model(
                string(R.string.category_Changchun),
                "",
                R.drawable.changchun
            )
        )
        list.add(
            Model(
                string(R.string.category_Dalian),
                "",
                R.drawable.dalian
            )
        )
        list.add(
            Model(
                string(R.string.category_Wuhan),
                "",
                R.drawable.wuhan
            )
        )
        list.add(
            Model(
                string(R.string.category_Chongqing),
                "",
                R.drawable.chongqing
            )
        )
        list.add(
            Model(
                string(R.string.category_Shenzhen),
                "",
                R.drawable.shenzhen
            )
        )
        list.add(
            Model(
                string(R.string.category_Nanjing),
                "",
                R.drawable.nanjing
            )
        )
        list.add(
            Model(
                string(R.string.category_Chengdu),
                "",
                R.drawable.chengdu
            )
        )
        list.add(
            Model(
                string(R.string.category_Shenyang),
                "",
                R.drawable.shenyang
            )
        )
        list.add(
            Model(
                string(R.string.category_Foshan),
                "",
                R.drawable.foshan
            )
        )
        list.add(
            Model(
                string(R.string.category_Xian),
                "",
                R.drawable.xian
            )
        )
        list.add(
            Model(
                string(R.string.category_Suzhou),
                "",
                R.drawable.suzhou
            )
        )
        list.add(
            Model(
                string(R.string.category_Kunming),
                "",
                R.drawable.kunming
            )
        )
        list.add(
            Model(
                string(R.string.category_Hangzhou),
                "",
                R.drawable.hangzhou
            )
        )
        list.add(
            Model(
                string(R.string.category_Harbin),
                "",
                R.drawable.harbin
            )
        )
        list.add(
            Model(
                string(R.string.category_Zhengzhou),
                "",
                R.drawable.zhengzhou
            )
        )
        list.add(
            Model(
                string(R.string.category_Changsha),
                "",
                R.drawable.changsha
            )
        )
        list.add(
            Model(
                string(R.string.category_Ningbo),
                "",
                R.drawable.ningbo
            )
        )
        list.add(
            Model(
                string(R.string.category_Wuxi),
                "",
                R.drawable.wuxi
            )
        )
        list.add(
            Model(
                string(R.string.category_Qingdao),
                "",
                R.drawable.qingdao
            )
        )
        list.add(
            Model(
                string(R.string.category_Nanchang),
                "",
                R.drawable.nanchang
            )
        )
        list.add(
            Model(
                string(R.string.category_Fuzhou),
                "",
                R.drawable.fuzhou
            )
        )

        list.add(
            Model(
                string(R.string.category_Dongguan),
                "",
                R.drawable.dongguan
            )
        )
        list.add(
            Model(
                string(R.string.category_Nanning),
                "",
                R.drawable.nanning
            )
        )
        list.add(
            Model(
                string(R.string.category_Hefei),
                "",
                R.drawable.hefei
            )
        )
        list.add(
            Model(
                string(R.string.category_Shijiazhuang),
                "",
                R.drawable.shijiazhuang
            )
        )
        list.add(
            Model(
                string(R.string.category_Guiyang),
                "",
                R.drawable.guiyang
            )
        )
        list.add(
            Model(
                string(R.string.category_Xiamen),
                "",
                R.drawable.xiamen
            )
        )
        list.add(
            Model(
                string(R.string.category_Urumqi),
                "",
                R.drawable.urumqi
            )
        )
        list.add(
            Model(
                string(R.string.category_Wenzhou),
                "",
                R.drawable.wenzhou
            )
        )
        list.add(
            Model(
                string(R.string.category_Jinan),
                "",
                R.drawable.jinan
            )
        )
        list.add(
            Model(
                string(R.string.category_Lanzhou),
                "",
                R.drawable.lanzhou
            )
        )
        list.add(
            Model(
                string(R.string.category_Changzhou),
                "",
                R.drawable.changzhou
            )
        )
        list.add(
            Model(
                string(R.string.category_Xuzhou),
                "",
                R.drawable.xuzhou
            )
        )
        list.add(
            Model(
                string(R.string.category_Hongkong),
                "",
                R.drawable.hongkong
            )
        )
        list.add(
            Model(
                string(R.string.category_Macau),
                "",
                R.drawable.macau
            )
        )
        list.add(
            Model(
                string(R.string.category_Taiwan),
                "",
                R.drawable.taiwan
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