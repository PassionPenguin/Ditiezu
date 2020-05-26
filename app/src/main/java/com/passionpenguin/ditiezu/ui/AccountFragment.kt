package com.passionpenguin.ditiezu.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ListView
import android.widget.TextView
import com.passionpenguin.ditiezu.*
import com.passionpenguin.ditiezu.helper.CircularCornersTransform
import com.passionpenguin.ditiezu.helper.HttpExt
import com.passionpenguin.ditiezu.helper.PrefAdapter
import com.passionpenguin.ditiezu.helper.PrefListItem
import com.squareup.picasso.Picasso
import org.jsoup.Jsoup

class AccountFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        if (!HttpExt().checkLogin())
            activity?.startActivity(Intent(context, LoginActivity::class.java))

        HttpExt().retrievePage("http://www.ditiezu.com/home.php?mod=space") {
            if (it == "Failed Retrieved") {
                // Failed Retrieved
                Log.i("HTTPEXT", "FAILED RETRIEVED")
            }

            activity?.runOnUiThread {
                val parser = Jsoup.parse(it)
                val absUrl = parser.select("strong a").attr("href")
                Log.i("", absUrl)
                val id = absUrl.substring(33, absUrl.lastIndexOf(".html"))

                activity?.findViewById<TextView>(R.id.userName)?.text =
                    parser.select("h2.mbn")[0].childNodes()[0].outerHtml()
                Picasso.with(context)
                    .load(parser.select(".avt img").attr("src"))
                    .placeholder(R.mipmap.noavatar_middle_rounded)
                    .error(R.mipmap.noavatar_middle_rounded)
                    .transform(CircularCornersTransform())
                    .into(activity?.findViewById<ImageView>(R.id.avatar))
                activity?.findViewById<TextView>(R.id.value_level)?.text =
                    parser.select(".pbm span a")[0].text()
                activity?.findViewById<TextView>(R.id.value_friends)?.text =
                    parser.select(".pbm.mbm.bbda.cl:first-child ul > :nth-child(5) a")[0].text()
                        .trim()
                        .substring(4)
                activity?.findViewById<TextView>(R.id.value_replies)?.text =
                    parser.select(".pbm.mbm.bbda.cl:first-child ul > :nth-child(5) a")[1].text()
                        .trim()
                        .substring(4)
                activity?.findViewById<TextView>(R.id.value_threads)?.text =
                    parser.select(".pbm.mbm.bbda.cl:first-child ul > :nth-child(5) a")[2].text()
                        .trim()
                        .substring(4)
                activity?.findViewById<TextView>(R.id.userPoints)?.text =
                    resources.getString(
                        R.string.user_integral,
                        parser.select("#psts li")[0].textNodes()[0].text().trim().toInt()
                    )
                activity?.findViewById<TextView>(R.id.value_points)?.text =
                    parser.select("#psts li")[0].textNodes()[0].text().trim()
                activity?.findViewById<TextView>(R.id.value_prestige)?.text =
                    parser.select("#psts li")[1].textNodes()[0].text().trim()
                activity?.findViewById<TextView>(R.id.value_money)?.text =
                    parser.select("#psts li")[2].textNodes()[0].text().trim()
                activity?.findViewById<TextView>(R.id.value_m_score)?.text =
                    parser.select("#psts li")[3].textNodes()[0].text().trim()
                activity?.findViewById<TextView>(R.id.value_popularity)?.text =
                    parser.select("#psts li")[4].textNodes()[0].text().trim()

                val list = activity?.findViewById<ListView>(R.id.personal_pref_list)
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
                list?.adapter = context?.let { ct ->
                    PrefAdapter(
                        ct,
                        0,
                        prefItem
                    )
                }
                list?.setOnItemClickListener { _, _, position, _ ->
                    prefItem[position].execFunc()
                }
            }
        }
        return inflater.inflate(R.layout.fragment_account, container, false)
    }
}