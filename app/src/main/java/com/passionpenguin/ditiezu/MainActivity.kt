package com.passionpenguin.ditiezu

import android.os.Bundle
import android.view.View
import android.view.animation.TranslateAnimation
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.postDelayed


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val homeButton: LinearLayout = findViewById(R.id.HomeButton)
        val categoryButton: LinearLayout = findViewById(R.id.CategoryButton)
        val notificationButton: LinearLayout = findViewById(R.id.NotificationButton)
        val accountButton: LinearLayout = findViewById(R.id.AccountButton)
        val categoryListContainer: LinearLayout = findViewById(R.id.CategoryListContainer)
        val categoryListMaskContainer: LinearLayout = findViewById(R.id.CategoryListMaskContainer)
        val categoryList: ListView = findViewById(R.id.CategoryList)
        fun string(int: Int): String {
            return resources.getString(int)
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

        fun categoryDialogController(state: Boolean) {
            val ctrlAnimation = if (state) TranslateAnimation(
                TranslateAnimation.RELATIVE_TO_SELF, 0F, TranslateAnimation.RELATIVE_TO_SELF, 0F,
                TranslateAnimation.RELATIVE_TO_SELF, 0F, TranslateAnimation.RELATIVE_TO_SELF, 1F
            ) else TranslateAnimation(
                TranslateAnimation.RELATIVE_TO_SELF, 0F, TranslateAnimation.RELATIVE_TO_SELF, 0F,
                TranslateAnimation.RELATIVE_TO_SELF, 1F, TranslateAnimation.RELATIVE_TO_SELF, 0F
            )
            ctrlAnimation.duration = 400L //设置动画的过渡时间

            categoryListMaskContainer.visibility = View.VISIBLE
            categoryListContainer.startAnimation(ctrlAnimation)
            if (state) {
                categoryListMaskContainer.postDelayed(400) {
                    categoryListMaskContainer.visibility = View.GONE
                }
                bottomButtonHighlight(
                    arrayOf(categoryButton, notificationButton, accountButton),
                    homeButton
                )
            }
        }

        val list = mutableListOf(
            Model(
                string(R.string.category_Beijing),
                R.drawable.beijing
            ),
            Model(
                string(R.string.category_Tianjin),
                R.drawable.tianjin
            ),
            Model(
                string(R.string.category_Shanghai),
                R.drawable.shanghai
            ),
            Model(
                string(R.string.category_Guangzhou),
                R.drawable.guangzhou
            ),
            Model(
                string(R.string.category_Changchun),
                R.drawable.changchun
            ),
            Model(
                string(R.string.category_Dalian),
                R.drawable.dalian
            ),
            Model(
                string(R.string.category_Wuhan),
                R.drawable.wuhan
            ),
            Model(
                string(R.string.category_Chongqing),
                R.drawable.chongqing
            ),
            Model(
                string(R.string.category_Shenzhen),
                R.drawable.shenzhen
            ),
            Model(
                string(R.string.category_Nanjing),
                R.drawable.nanjing
            ),
            Model(
                string(R.string.category_Chengdu),
                R.drawable.chengdu
            ),
            Model(
                string(R.string.category_Shenyang),
                R.drawable.shenyang
            ),
            Model(
                string(R.string.category_Foshan),
                R.drawable.foshan
            ),
            Model(
                string(R.string.category_Xian),
                R.drawable.xian
            ),
            Model(
                string(R.string.category_Suzhou),
                R.drawable.suzhou
            ),
            Model(
                string(R.string.category_Kunming),
                R.drawable.kunming
            ),
            Model(
                string(R.string.category_Hangzhou),
                R.drawable.hangzhou
            ),
            Model(
                string(R.string.category_Harbin),
                R.drawable.harbin
            ),
            Model(
                string(R.string.category_Zhengzhou),
                R.drawable.zhengzhou
            ),
            Model(
                string(R.string.category_Changsha),
                R.drawable.changsha
            ),
            Model(
                string(R.string.category_Ningbo),
                R.drawable.ningbo
            ),
            Model(
                string(R.string.category_Wuxi),
                R.drawable.wuxi
            ),
            Model(
                string(R.string.category_Qingdao),
                R.drawable.qingdao
            ),
            Model(
                string(R.string.category_Nanchang),
                R.drawable.nanchang
            ),
            Model(
                string(R.string.category_Fuzhou),
                R.drawable.fuzhou
            ),
            Model(
                string(R.string.category_Dongguan),
                R.drawable.dongguan
            ),
            Model(
                string(R.string.category_Nanning),
                R.drawable.nanning
            ),
            Model(
                string(R.string.category_Hefei),
                R.drawable.hefei
            ),
            Model(
                string(R.string.category_Shijiazhuang),
                R.drawable.shijiazhuang
            ),
            Model(
                string(R.string.category_Guiyang),
                R.drawable.guiyang
            ),
            Model(
                string(R.string.category_Xiamen),
                R.drawable.xiamen
            ),
            Model(
                string(R.string.category_Urumqi),
                R.drawable.urumqi
            ),
            Model(
                string(R.string.category_Wenzhou),
                R.drawable.wenzhou
            ),
            Model(
                string(R.string.category_Jinan),
                R.drawable.jinan
            ),
            Model(
                string(R.string.category_Lanzhou),
                R.drawable.lanzhou
            ),
            Model(
                string(R.string.category_Changzhou),
                R.drawable.changzhou
            ),
            Model(
                string(R.string.category_Xuzhou),
                R.drawable.xuzhou
            ),
            Model(
                string(R.string.category_Hongkong),
                R.drawable.hongkong
            ),
            Model(
                string(R.string.category_Macau),
                R.drawable.macau
            ),
            Model(
                string(R.string.category_Taiwan),
                R.drawable.taiwan
            )
        )

        categoryList.adapter = CategoryAdapter(this, R.layout.category_popup, list)

        categoryList.setOnItemClickListener { _, _, position, _ ->
            categoryDialogController(true)
            Toast.makeText(this@MainActivity, position.toString(), Toast.LENGTH_LONG).show()
        }

        categoryListMaskContainer.setOnClickListener {
            categoryDialogController(true)
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

            categoryDialogController(false)
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