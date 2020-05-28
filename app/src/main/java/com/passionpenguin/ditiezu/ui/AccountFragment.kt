package com.passionpenguin.ditiezu.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.*
import android.webkit.CookieManager
import android.widget.*
import com.passionpenguin.ditiezu.*
import com.passionpenguin.ditiezu.helper.*
import com.squareup.picasso.Picasso
import org.jsoup.Jsoup
import org.jsoup.nodes.Element
import org.jsoup.select.Elements

class AccountFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        activity?.findViewById<LinearLayout>(R.id.LoadingMaskContainer)?.visibility =
            View.VISIBLE
        if (HttpExt().checkLogin())
            HttpExt().retrievePage("http://www.ditiezu.com/home.php?mod=space") { it ->
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

                    var metaList =
                        parser.select(".pbm.mbm.bbda.cl:first-child ul > li:last-child")[0]
                    parser.select(".pbm.mbm.bbda.cl:first-child ul > li").forEach {
                        if (it.html().contains("统计信息"))
                            metaList = it
                    }

                    try {
                        activity?.findViewById<TextView>(R.id.value_friends)?.text =
                            metaList.select("a")[0].text()
                                .trim()
                                .substring(4)
                    } catch (e: Exception) {
                        activity?.findViewById<TextView>(R.id.value_friends)?.text = "N/A"
                    }
                    try {
                        activity?.findViewById<TextView>(R.id.value_replies)?.text =
                            metaList.select("a")[1].text()
                                .trim()
                                .substring(4)
                    } catch (e: Exception) {
                        activity?.findViewById<TextView>(R.id.value_replies)?.text = "N/A"
                    }
                    try {
                        activity?.findViewById<TextView>(R.id.value_threads)?.text =
                            metaList.select("a")[2].text()
                                .trim()
                                .substring(4)
                    } catch (e: Exception) {
                        activity?.findViewById<TextView>(R.id.value_threads)?.text = "N/A"
                    }

                    activity?.findViewById<TextView>(R.id.userIntegral)?.text =
                        resources.getString(R.string.user_integral, parser.select("#psts li")[0].textNodes()[0].text().trim().toInt())

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
                    prefItem.add(
                        PrefListItem(
                            resources.getString(R.string.logout),
                            resources.getString(
                                R.string.logout_description
                            ), "",
                            true
                        ) {
                            view?.let { v ->
                                context?.let { ctx ->
                                    Dialog().create(
                                        v,
                                        ctx,
                                        resources.getString(R.string.logout),
                                        resources.getString(
                                            R.string.logout_warning,
                                            parser.select("h2.mbn")[0].childNodes()[0].outerHtml()
                                        )
                                    ) {
                                        CookieManager.getInstance().removeAllCookies { _ ->
                                            activity?.recreate()
                                        }
                                    }
                                }
                            }
                        }
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
        else {
            val loginView = inflater.inflate(R.layout.fragment_login_tips, container, false)
            loginView?.findViewById<Button>(R.id.toggle_login_page_button)
                ?.setOnClickListener { _ ->
                    activity?.startActivity(Intent(context, LoginActivity::class.java))
                }
            activity?.findViewById<LinearLayout>(R.id.LoadingMaskContainer)?.visibility =
                View.GONE
            return loginView
        }
        activity?.findViewById<LinearLayout>(R.id.LoadingMaskContainer)?.visibility =
            View.GONE
        return inflater.inflate(R.layout.fragment_account, container, false)
    }
}