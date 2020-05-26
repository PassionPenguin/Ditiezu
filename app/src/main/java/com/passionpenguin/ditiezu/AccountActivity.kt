package com.passionpenguin.ditiezu

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.animation.TranslateAnimation
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.postDelayed
import com.squareup.picasso.Picasso
import org.jsoup.Jsoup


class AccountActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_account)

        if (!HttpExt().checkLogin())
            startActivity(Intent(this@AccountActivity, LoginActivity::class.java))

        val homeButton: LinearLayout = findViewById(R.id.HomeButton)
        val categoryButton: LinearLayout = findViewById(R.id.CategoryButton)
        val notificationButton: LinearLayout = findViewById(R.id.NotificationButton)
        val accountButton: LinearLayout = findViewById(R.id.AccountButton)
        val categoryListContainer: LinearLayout = findViewById(R.id.CategoryListContainer)
        val categoryListMaskContainer: LinearLayout = findViewById(R.id.CategoryListMaskContainer)
        val categoryListView: ListView = findViewById(R.id.CategoryList)
        val mask: LinearLayout = findViewById(R.id.LoadingMaskContainer)
        val categoryContent = CategoryContent(applicationContext)
        val categoryList = categoryContent.categoryList
        val categoryId = categoryContent.categoryId

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
            mask.visibility = View.GONE
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
                    arrayOf(homeButton, categoryButton, notificationButton),
                    accountButton
                )
            }
        }

        categoryListView.adapter =
            CategoryAdapter(
                this,
                R.layout.category_popup,
                categoryList
            )
        categoryListView.setOnItemClickListener { _, _, position, _ ->
            val i = Intent(this@AccountActivity, ForumDisplay::class.java)
            i.putExtra(
                "fid",
                categoryId[position]
            )
            startActivity(i)
            categoryDialogController(true)
        }
        categoryListMaskContainer.setOnClickListener {
            categoryDialogController(true)
        }
        findViewById<LinearLayout>(R.id.LoadingMaskContainer).setOnClickListener {
            it.visibility = View.GONE
        }
        homeButton.setOnClickListener {
            bottomButtonHighlight(
                arrayOf(categoryButton, notificationButton, accountButton),
                homeButton
            )

            startActivity(Intent(this@AccountActivity, MainActivity::class.java))
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

        HttpExt().retrievePage("http://www.ditiezu.com/home.php?mod=space") {
            runOnUiThread {
                findViewById<LinearLayout>(R.id.LoadingMaskContainer).visibility = View.VISIBLE
                findViewById<LinearLayout>(R.id.LoadingAnimation).startAnimation(Animation().fadeOutAnimation())
                findViewById<LinearLayout>(R.id.LoadingMaskContainer).postDelayed(400) {
                    findViewById<LinearLayout>(R.id.LoadingMaskContainer).visibility = View.GONE
                }
            }

            if (it == "Failed Retrieved") {
                // Failed Retrieved
                Log.i("HTTPEXT", "FAILED RETRIEVED")
            }

            runOnUiThread {
                val parser = Jsoup.parse(it)
                val absUrl = parser.select("strong a").attr("href")
                val id = absUrl.substring(33, absUrl.lastIndexOf(".html"))

                findViewById<TextView>(R.id.userName).text =
                    parser.select("h2.mbn")[0].childNodes()[0].outerHtml()
                Picasso.with(applicationContext)
                    .load(parser.select(".avt img").attr("src"))
                    .placeholder(R.mipmap.noavatar_middle_rounded)
                    .error(R.mipmap.noavatar_middle_rounded)
                    .transform(CircularCornersTransform())
                    .into(findViewById<ImageView>(R.id.avatar))
                findViewById<TextView>(R.id.value_level).text =
                    parser.select(".pbm span a")[0].text()
                findViewById<TextView>(R.id.value_friends).text =
                    parser.select(".pbm.mbm.bbda.cl:first-child ul > :nth-child(5) a")[0].text()
                        .trim()
                        .substring(4)
                findViewById<TextView>(R.id.value_replies).text =
                    parser.select(".pbm.mbm.bbda.cl:first-child ul > :nth-child(5) a")[1].text()
                        .trim()
                        .substring(4)
                findViewById<TextView>(R.id.value_threads).text =
                    parser.select(".pbm.mbm.bbda.cl:first-child ul > :nth-child(5) a")[2].text()
                        .trim()
                        .substring(4)
                findViewById<TextView>(R.id.userPoints).text =
                    resources.getString(
                        R.string.user_integral,
                        parser.select("#psts li")[0].textNodes()[0].text().trim().toInt()
                    )
                findViewById<TextView>(R.id.value_points).text =
                    parser.select("#psts li")[0].textNodes()[0].text().trim()
                findViewById<TextView>(R.id.value_prestige).text =
                    parser.select("#psts li")[1].textNodes()[0].text().trim()
                findViewById<TextView>(R.id.value_money).text =
                    parser.select("#psts li")[2].textNodes()[0].text().trim()
                findViewById<TextView>(R.id.value_m_score).text =
                    parser.select("#psts li")[3].textNodes()[0].text().trim()
                findViewById<TextView>(R.id.value_popularity).text =
                    parser.select("#psts li")[4].textNodes()[0].text().trim()

                val list = findViewById<ListView>(R.id.personal_pref_list)
                val prefItem = mutableListOf<PrefListItem>()
                prefItem.add(
                    PrefListItem(
                        resources.getString(R.string.user_id),
                        "",
                        id,
                        false
                    ) {}
                )
                prefItem.add(
                    PrefListItem(
                        resources.getString(R.string.online_hour),
                        resources.getString(R.string.online_hour_description),
                        parser.select("#pbbs>:first-child").textNodes()[0].text(),
                        false
                    ) {}
                )
                list.adapter = PrefAdapter(this, 0, prefItem)
                list.setOnItemClickListener { _, _, position, _ ->
                    prefItem[position].execFunc()
                }
            }
        }
    }
}