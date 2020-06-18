package com.passionpenguin.ditiezu.ui

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.CookieManager
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.passionpenguin.ditiezu.*
import com.passionpenguin.ditiezu.helper.Dialog
import com.passionpenguin.ditiezu.helper.HttpExt
import com.passionpenguin.ditiezu.helper.PrefListItem
import com.passionpenguin.ditiezu.helper.prefView
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

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        activity?.findViewById<LinearLayout>(R.id.LoadingMaskContainer)?.visibility =
            View.VISIBLE
        if (isLogin)
            HttpExt().retrievePage("http://www.ditiezu.com/home.php?mod=space") { it ->
                activity?.let { activity ->
                    when (it) {
                        "Failed Retrieved" -> {
                            Dialog().tip(
                                resources.getString(R.string.failed_retrieved),
                                R.drawable.ic_baseline_close_24,
                                R.color.danger,
                                activity,
                                activity.findViewById(R.id.MainActivity),
                                Dialog.TIME_SHORT
                            )
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
                                    resources.getString(R.string.signature),
                                    "",
                                    "",
                                    true
                                ) {
                                    val i = Intent(context, PostActivity::class.java)
                                    i.putExtra("type", "sightml")
                                    activity.startActivity(i)
                                }
                            )
                            prefItem.add(
                                PrefListItem(
                                    resources.getString(R.string.logout),
                                    resources.getString(
                                        R.string.logout_description
                                    ), "",
                                    true
                                ) {
                                    Dialog().create(
                                        activity,
                                        activity.findViewById(R.id.MainActivity),
                                        resources.getString(R.string.confirm),
                                        resources.getString(R.string.logout),
                                        resources.getString(
                                            R.string.logout_warning,
                                            parser.select("h2.mbn")[0].childNodes()[0].outerHtml()
                                        )
                                    ) { _, w ->
                                        CookieManager.getInstance().removeAllCookies {
                                            activity.recreate()
                                        }
                                        w.dismiss()
                                    }
                                }
                            )
                            prefItem.add(
                                PrefListItem(
                                    resources.getString(R.string.post_record),
                                    "", "",
                                    true
                                ) {
                                    val i = Intent(activity, PersonalHistory::class.java)
                                    i.putExtra("uid", id.toInt())
                                    startActivity(i)
                                }
                            )

                            activity.runOnUiThread {
                                userName.text =
                                    parser.select("h2.mbn")[0].childNodes()[0].outerHtml().trim()
                                context?.let { mCtx ->
                                    Glide.with(mCtx)
                                        .load(parser.select(".avt img").attr("src"))
                                        .placeholder(R.mipmap.noavatar_middle)
                                        .error(R.mipmap.noavatar_middle)
                                        .apply(RequestOptions.bitmapTransform(RoundedCorners(8)))
                                        .into(avatar)
                                }
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


                                val pts =
                                    parser.select("#psts li")[0].textNodes()[0].text().trim()
                                        .toInt()
                                val max = when (pts) {
                                    0 -> 0
                                    in 1..49 -> 50
                                    in 50..199 -> 200
                                    in 200..499 -> 500
                                    in 500..999 -> 1000
                                    in 1000..1999 -> 2000
                                    in 2000..2999 -> 3000
                                    in 3000..4999 -> 5000
                                    in 5000..9999 -> 10000
                                    in 10000..19999 -> 20000
                                    in 20000..50000 -> 50000
                                    else -> 0
                                }
                                val min = when (pts) {
                                    0 -> 0
                                    in 1..49 -> 0
                                    in 50..199 -> 50
                                    in 200..499 -> 200
                                    in 500..999 -> 500
                                    in 1000..1999 -> 1000
                                    in 2000..2999 -> 2000
                                    in 3000..4999 -> 3000
                                    in 5000..9999 -> 5000
                                    in 10000..19999 -> 10000
                                    in 20000..50000 -> 20000
                                    else -> 0
                                }
                                progressBar.max = max - min
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
                                    progressBar.postDelayed({
                                        progressBar.setProgress(
                                            pts - min, true
                                        )
                                    }, 1000)
                                else progressBar.progress = pts - min
                                level.text = parser.select(".pbm span a")[0].text()
                                points.text = "$pts / $max"

                                value_points.text = pts.toString()
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
                                        activity.startActivity(
                                            Intent(
                                                context,
                                                AboutDitiezu::class.java
                                            )
                                        )
                                    }
                                })
                            }
                        }
                    }
                }
                activity?.runOnUiThread {
                    activity?.findViewById<LinearLayout>(R.id.LoadingMaskContainer)?.visibility =
                        View.GONE

                    activity?.findViewById<TextView>(R.id.title)?.text =
                        resources.getString(R.string.settings_title)
                }
            }
    }
}