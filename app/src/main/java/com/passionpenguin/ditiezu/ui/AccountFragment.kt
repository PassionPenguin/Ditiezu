package com.passionpenguin.ditiezu.ui

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.CookieManager
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.passionpenguin.ditiezu.AboutDitiezu
import com.passionpenguin.ditiezu.LoginActivity
import com.passionpenguin.ditiezu.R
import com.passionpenguin.ditiezu.helper.*
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_account.*
import org.jsoup.Jsoup
import kotlin.properties.Delegates

class AccountFragment : Fragment() {
    private var isLogin by Delegates.notNull<Boolean>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        isLogin = HttpExt().checkLogin()
        activity?.findViewById<LinearLayout>(R.id.tips)?.removeAllViews()
        return when (isLogin) {
            true ->
                inflater.inflate(R.layout.fragment_account, container, false)
            false -> {
                val loginView =
                    layoutInflater.inflate(R.layout.fragment_login_tips, container, false)
                loginView?.findViewById<Button>(R.id.toggle_login_page_button)
                    ?.setOnClickListener { _ ->
                        activity?.startActivity(Intent(context, LoginActivity::class.java))
                    }
                activity?.findViewById<LinearLayout>(R.id.LoadingMaskContainer)?.visibility =
                    View.GONE
                loginView
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        activity?.findViewById<LinearLayout>(R.id.LoadingMaskContainer)?.visibility =
            View.VISIBLE
        if (isLogin)
            HttpExt().retrievePage("http://www.ditiezu.com/home.php?mod=space") { it ->
                val v = when (it) {
                    "Failed Retrieved" -> {
                        val v = LayoutInflater.from(context).inflate(
                            R.layout.tip_access_denied,
                            activity?.findViewById<LinearLayout>(R.id.tips),
                            false
                        )
                        v.findViewById<TextView>(R.id.text).text =
                            resources.getString(R.string.failed_retrieved)
                        v
                    }
                    else -> {
                        val parser = Jsoup.parse(it)
                        val absUrl = parser.select("strong a").attr("href")
                        val id = absUrl.substring(33, absUrl.lastIndexOf(".html"))

                        var metaList =
                            parser.select(".pbm.mbm.bbda.cl:first-child ul > li:last-child")[0]
                        parser.select(".pbm.mbm.bbda.cl:first-child ul > li").forEach {
                            if (it.html().contains("统计信息"))
                                metaList = it
                        }
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
                                view.let { v ->
                                    context?.let { ctx ->
                                        activity?.let { activity ->
                                            Dialog().create(
                                                activity,
                                                v,
                                                ctx,
                                                resources.getString(R.string.logout),
                                                resources.getString(
                                                    R.string.logout_warning,
                                                    parser.select("h2.mbn")[0].childNodes()[0].outerHtml()
                                                )
                                            ) {
                                                CookieManager.getInstance().removeAllCookies {
                                                    activity.recreate()
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        )

                        activity?.runOnUiThread {
                            userName.text =
                                parser.select("h2.mbn")[0].childNodes()[0].outerHtml()
                            Picasso.with(context)
                                .load(parser.select(".avt img").attr("src"))
                                .placeholder(R.mipmap.noavatar_middle_rounded)
                                .error(R.mipmap.noavatar_middle_rounded)
                                .transform(CircularCornersTransform())
                                .into(avatar)
                            value_level.text = parser.select(".pbm span a")[0].text()

                            try {
                                value_friends.text =
                                    metaList.select("a")[0].text().trim().substring(4)
                            } catch (e: Exception) {
                                value_friends.text = "N/A"
                            }
                            try {
                                value_replies.text =
                                    metaList.select("a")[1].text().trim().substring(4)
                            } catch (e: Exception) {
                                value_replies.text = "N/A"
                            }
                            try {
                                value_threads.text =
                                    metaList.select("a")[2].text().trim().substring(4)
                            } catch (e: Exception) {
                                value_threads.text = "N/A"
                            }

                            userIntegral.text = resources.getString(
                                R.string.user_integral,
                                parser.select("#psts li")[0].textNodes()[0].text().trim()
                                    .toInt()
                            )

                            value_points.text =
                                parser.select("#psts li")[0].textNodes()[0].text().trim()
                            value_prestige.text =
                                parser.select("#psts li")[1].textNodes()[0].text().trim()
                            value_money.text =
                                parser.select("#psts li")[2].textNodes()[0].text().trim()
                            value_m_score.text =
                                parser.select("#psts li")[3].textNodes()[0].text().trim()
                            value_popularity.text =
                                parser.select("#psts li")[4].textNodes()[0].text().trim()

                            prefItem.forEach { item ->
                                personal_pref_list.addView(context?.let { ctx ->
                                    prefView(
                                        ctx,
                                        item.name,
                                        item.description,
                                        item.value,
                                        item.toggle,
                                        item.execFunc
                                    )
                                })
                            }
                            application_pref_list.addView(context?.let { ctx ->
                                prefView(
                                    ctx,
                                    resources.getString(R.string.about),
                                    "",
                                    "",
                                    true
                                ) {
                                    activity?.startActivity(
                                        Intent(
                                            context,
                                            AboutDitiezu::class.java
                                        )
                                    )
                                }
                            })
                        }
                        null
                    }
                }
                if (v != null)
                    activity?.runOnUiThread {
                        activity?.findViewById<LinearLayout>(R.id.tips)?.addView(v)
                    }
            }
        activity?.findViewById<LinearLayout>(R.id.LoadingMaskContainer)?.visibility =
            View.GONE

        activity?.findViewById<TextView>(R.id.title)?.text =
            resources.getString(R.string.settings_title)
    }
}