package com.passionpenguin.ditiezu

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.postDelayed
import com.squareup.picasso.Picasso
import org.jsoup.Jsoup


class AccountActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_account)

        if (!HttpExt().checkLogin())
            startActivity(Intent(this@AccountActivity, LoginActivity::class.java))

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

                findViewById<TextView>(R.id.userName).text = parser.select("h2.mbn")[0].text()
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
                        .substring(4)
                findViewById<TextView>(R.id.value_replies).text =
                    parser.select(".pbm.mbm.bbda.cl:first-child ul > :nth-child(5) a")[1].text()
                        .substring(4)
                findViewById<TextView>(R.id.value_threads).text =
                    parser.select(".pbm.mbm.bbda.cl:first-child ul > :nth-child(5) a")[2].text()
                        .substring(4)
                findViewById<TextView>(R.id.userId).text = id
                findViewById<TextView>(R.id.onlineHour).text =
                    parser.select("#pbbs>:first-child").textNodes()[0].text()
                findViewById<TextView>(R.id.value_points).text =
                    parser.select("#psts li")[0].textNodes()[0].text()
                findViewById<TextView>(R.id.value_prestige).text =
                    parser.select("#psts li")[1].textNodes()[0].text()
                findViewById<TextView>(R.id.value_money).text =
                    parser.select("#psts li")[2].textNodes()[0].text()
                findViewById<TextView>(R.id.value_m_score).text =
                    parser.select("#psts li")[3].textNodes()[0].text()
                findViewById<TextView>(R.id.value_popularity).text =
                    parser.select("#psts li")[4].textNodes()[0].text()
            }
        }
    }
}