package com.passionpenguin.ditiezu.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.ListView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayout
import com.passionpenguin.ditiezu.LoginActivity
import com.passionpenguin.ditiezu.R
import com.passionpenguin.ditiezu.ViewThread
import com.passionpenguin.ditiezu.helper.HttpExt
import com.passionpenguin.ditiezu.helper.NotificationItem
import com.passionpenguin.ditiezu.helper.NotificationsAdapter
import org.jsoup.Jsoup
import kotlin.properties.Delegates

class NotificationsFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        activity?.findViewById<LinearLayout>(R.id.tips)?.removeAllViews()
        return inflater.inflate(R.layout.fragment_notifications, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fun retriever(url: String) {
            activity?.runOnUiThread {
                activity?.findViewById<LinearLayout>(R.id.tips)?.removeAllViews()
            }
            activity?.findViewById<LinearLayout>(R.id.LoadingMaskContainer)?.visibility =
                View.VISIBLE
            HttpExt().retrievePage(url) { s ->
                val v = when {
                    s == "Failed Retrieved" -> {
                        val v = LayoutInflater.from(context).inflate(
                            R.layout.tip_access_denied,
                            activity?.findViewById<LinearLayout>(R.id.tips),
                            false
                        )
                        v.findViewById<TextView>(R.id.text).text =
                            resources.getString(R.string.failed_retrieved)
                        v
                    }
                    s.contains("用户登录") -> {
                        LayoutInflater.from(context).inflate(
                            R.layout.tip_login_required,
                            activity?.findViewById<LinearLayout>(R.id.tips),
                            false
                        )
                    }
                    s.contains("暂时没有新提醒") -> {
                        val v = LayoutInflater.from(context).inflate(
                            R.layout.tip_not_applicable,
                            activity?.findViewById<LinearLayout>(R.id.tips),
                            false
                        )
                        v.findViewById<TextView>(R.id.text).text =
                            resources.getString(R.string.no_notification)
                        v
                    }
                    else -> {
                        val parser = Jsoup.parse(s)
                        val list = mutableListOf<NotificationItem>()
                        parser.select("[notice]").forEach {
                            try {
                                var quote: String? = null
                                if (!it.select(".quote").isEmpty()) {
                                    quote = it.select(".quote").text()
                                    it.select(".quote").remove()
                                }
                                var page = "1"
                                var tid: String
                                with(it.select(".ntc_body a:last-child")) {
                                    if (this.isEmpty())
                                        tid = "-1"
                                    else if (this.attr("href").contains("findpost")) {
                                        val result = HttpExt().retrieveRedirect(this.attr("href"))
                                        tid = result?.get(0) ?: "1"
                                        page = result?.get(1) ?: "1"
                                    } else if (this.attr("href").contains("thread-")) {
                                        tid = this.attr("href").substring(
                                            this.attr("href").indexOf("thread-") + 7,
                                            this.attr("href")
                                                .indexOf(
                                                    "-",
                                                    this.attr("href").indexOf("thread-") + 7
                                                )
                                        )
                                        page = "1"
                                    } else {
                                        tid = "-1"
                                    }
                                }

                                list.add(
                                    NotificationItem(
                                        if (it.select("img").attr("src").contains("systempm")) {
                                            "http://www.ditiezu.com/" + it.select("img").attr("src")
                                        } else it.select("img").attr("src"),
                                        it.select(".ntc_body").text(),
                                        quote,
                                        it.select("dt span").text(),
                                        tid,
                                        page
                                    )
                                )
                            } catch (exception: Exception) {
                                Log.e("[Notification]", exception.toString())
                            }
                        }
                        activity?.runOnUiThread {
                            val listView = activity?.findViewById<ListView>(R.id.NotificationList)
                            listView?.adapter =
                                context?.let { ctx -> NotificationsAdapter(ctx, 0, list) }
                            listView?.setOnItemClickListener { _, _, position, _ ->
                                if (list[position].tid != "-1") {
                                    val i = Intent(context, ViewThread::class.java)
                                    i.putExtra("tid", list[position].tid?.toInt())
                                    i.putExtra("page", list[position].page?.toInt())
                                    activity?.startActivity(i)
                                }
                            }
                        }
                        activity?.runOnUiThread {
                            activity?.findViewById<LinearLayout>(R.id.LoadingMaskContainer)?.visibility =
                                View.GONE
                        }
                        null
                    }
                }
                if (v != null)
                    activity?.runOnUiThread {
                        activity?.findViewById<LinearLayout>(R.id.tips)?.addView(v)
                    }
            }
        }

        val tabs: TabLayout = view.findViewById(R.id.tabs)
        tabs.addTab(tabs.newTab().setText(resources.getString(R.string.unread_notifications)))
        tabs.addTab(tabs.newTab().setText(resources.getString(R.string.read_notifications)))
        tabs.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                when (tab.position) {
                    0 -> retriever("http://ditiezu.com/home.php?mod=space&do=notice")
                    1 -> retriever("http://ditiezu.com/home.php?mod=space&do=notice&isread=1")
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {}
            override fun onTabReselected(tab: TabLayout.Tab) {}
        })

        retriever("http://ditiezu.com/home.php?mod=space&do=notice")

        activity?.findViewById<TextView>(R.id.title)?.text =
            resources.getString(R.string.notifications_title)
    }
}