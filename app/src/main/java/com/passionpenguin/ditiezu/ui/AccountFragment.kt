package com.passionpenguin.ditiezu.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.*
import android.webkit.CookieManager
import android.widget.*
import com.github.salomonbrys.kotson.obj
import com.google.gson.JsonParser
import com.passionpenguin.ditiezu.*
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
                        Log.i("", absUrl)
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
                                        Dialog().create(
                                            v,
                                            ctx,
                                            resources.getString(R.string.logout),
                                            resources.getString(
                                                R.string.logout_warning,
                                                parser.select("h2.mbn")[0].childNodes()[0].outerHtml()
                                            )
                                        ) {
                                            CookieManager.getInstance().removeAllCookies {
                                                activity?.recreate()
                                            }
                                        }
                                    }
                                }
                            }
                        )

                        val appPref = mutableListOf<PrefListItem>()
                        appPref.add(
                            PrefListItem(
                                resources.getString(R.string.version),
                                BuildConfig.VERSION_NAME,
                                ""
                            ) {})

                        val value =
                            HttpExt().asyncRetrieveNonForumPage("https://gitee.com/PassionPenguin/Ditiezu/raw/v2/CUR_VERSION.json")
                        if (value != "Failed Retrieved") {
                            val latestVersion = JsonParser().parse(value).obj
                            if (latestVersion.get("versionCode").asInt > BuildConfig.VERSION_CODE)
                                appPref.add(
                                    PrefListItem(
                                        resources.getString(R.string.new_version_detected),
                                        latestVersion.get("versionLog").asString,
                                        latestVersion.get("versionCode").asString,
                                        true
                                    ) {
                                        context?.let { ctx ->
                                            Dialog().create(
                                                fragment_account,
                                                ctx,
                                                resources.getString(R.string.confirmUpdating),
                                                resources.getString(R.string.confirmUpdating_description)
                                            ) {
                                                HttpExt().downloadUtils(
                                                    context,
                                                    "https://passionpenguin.coding.net/api/share/download/0fa9eb8c-6255-4a97-b7cb-41c64e5b1699",
                                                    "dtz_${latestVersion.get("versionCode").asString}.apk"
                                                )
                                            }
                                        }
                                    })
                        }

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

                            personal_pref_list.adapter = context?.let { ct ->
                                PrefAdapter(
                                    ct,
                                    0,
                                    prefItem
                                )
                            }
                            personal_pref_list.setOnItemClickListener { _, _, position, _ ->
                                prefItem[position].execFunc()
                            }
                            application_pref_list.adapter = context?.let { ct ->
                                PrefAdapter(
                                    ct,
                                    0,
                                    appPref
                                )
                            }
                            application_pref_list.setOnItemClickListener { _, _, position, _ ->
                                appPref[position].execFunc()
                            }
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