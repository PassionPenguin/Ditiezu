package com.passionpenguin.ditiezu.ui

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.microsoft.appcenter.utils.AppCenterLog
import com.microsoft.appcenter.utils.HandlerUtils.runOnUiThread
import com.passionpenguin.ditiezu.R
import com.passionpenguin.ditiezu.SearchResultActivity
import com.passionpenguin.ditiezu.helper.Dialog
import com.passionpenguin.ditiezu.helper.HttpExt
import com.passionpenguin.ditiezu.helper.ThreadItem
import com.passionpenguin.ditiezu.helper.ThreadItemAdapter
import kotlinx.android.synthetic.main.fragment_item_list.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.jsoup.Jsoup
import org.jsoup.parser.Parser

class ThreadItemFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_item_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        actionBar.elevation = resources.getDimension(R.dimen._16)
        with(actionBar.findViewById<EditText>(R.id.app_search_input)) {
            this?.setOnKeyListener(object : View.OnKeyListener {
                override fun onKey(v: View?, keyCode: Int, event: KeyEvent): Boolean {
                    val t = v as EditText
                    if (event.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER && t.text.toString().trim().isNotEmpty()) {
                        val i = Intent(context, SearchResultActivity::class.java)
                        i.putExtra("kw", t.text.toString())
                        context.startActivity(i)
                        return true
                    }
                    return false
                }
            })
        }
        GlobalScope.launch {
            if (activity?.let { HttpExt.checkLogin(it) }!!)
                activity?.let { activity ->
                    val s = HttpExt.retrievePage("http://www.ditiezu.com/search.php?mod=forum&srchfrom=31536000&searchsubmit=yes") // 有点离谱哦～一年分量～
                    runOnUiThread {
                        when {
                            s == "Failed Retrieved" -> {
                                Dialog.tip(
                                    resources.getString(R.string.failed_retrieved),
                                    R.drawable.ic_baseline_close_24,
                                    R.color.danger,
                                    activity,
                                    threadItemList,
                                    Dialog.TIME_SHORT
                                )
                            }
                            s.contains("用户登录") -> {
                                Dialog.tip(
                                    resources.getString(R.string.login_tips),
                                    R.drawable.ic_baseline_close_24,
                                    R.color.danger,
                                    activity,
                                    threadItemList,
                                    Dialog.TIME_SHORT
                                )
                            }
                            s.contains("只能进行一次搜索") -> {
                                Dialog.tip(
                                    resources.getString(R.string.search_15s),
                                    R.drawable.ic_baseline_close_24,
                                    R.color.danger,
                                    activity,
                                    threadItemList,
                                    Dialog.TIME_SHORT
                                )
                            }
                            s.contains("站点设置每分钟系统最多") -> {
                                Dialog.tip(
                                    resources.getString(R.string.search_system_restriction),
                                    R.drawable.ic_baseline_close_24,
                                    R.color.danger,
                                    activity,
                                    threadItemList,
                                    Dialog.TIME_SHORT
                                )
                            }
                            else -> {
                                val parser = Jsoup.parse(s)
                                val threadListContent = mutableListOf<ThreadItem>()
                                parser.select("#threadlist .pbw").forEach {
                                    val title = it.select("h3 a")
                                    val author = it.select("p")[2].select("span a")[0]
                                    val meta = it.select(".xg1").text()
                                    var targetTid = ""
                                    with(title.attr("href")) {
                                        try {
                                            targetTid = if (this.contains("highlight")) this.substring(this.indexOf("tid=") + 4, this.indexOf("&", this.indexOf("tid=") + 4))
                                            else this.substring(this.indexOf("tid=") + 4)
                                        } catch (ignored: Exception) {
                                        }
                                    }
                                    threadListContent.add(
                                        ThreadItem(
                                            author.attr("href").substring(
                                                author.attr("href").indexOf("uid-") + 4,
                                                author.attr("href").indexOf(".html")
                                            ).toInt(),
                                            title.text().trim(),
                                            it.select("p:nth-child(3)").text().trim(),
                                            author.text(),
                                            it.select("span:first-child").text(),
                                            it.select(".xi1").text(),
                                            meta.substring(meta.indexOf(" - ") + 3, meta.length - 3),
                                            meta.substring(0, meta.indexOf("个回复")),
                                            targetTid.toInt()
                                        )
                                    )
                                }

                                runOnUiThread {
                                    threadItemList.adapter = ThreadItemAdapter(
                                        activity,
                                        threadListContent,
                                        isHome = true,
                                        withHeader = false,
                                        withNavigation = false,
                                        curCategoryItem = null,
                                        curPage = 0,
                                        lastPage = 0,
                                        disabledCurPage = false,
                                        enabledPrev = false,
                                        enabledNext = false
                                    ) {}
                                    threadItemList.layoutManager = LinearLayoutManager(activity)
                                    var mDistance = 0
                                    threadItemList.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                                        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                                            mDistance += dy
                                            try {
                                                when {
                                                    mDistance == 0 -> {
                                                        actionBar.setBackgroundColor(Color.argb(0, 255, 255, 255))
                                                        actionBar.findViewById<TextView>(R.id.appName).setTextColor(Color.rgb(255, 255, 255))
                                                    }
                                                    mDistance <= 204 -> {
                                                        actionBar.setBackgroundColor(Color.argb((mDistance * 1f / 204 * 255).toInt(), 255, 255, 255))
                                                        actionBar.findViewById<TextView>(R.id.appName)
                                                            .setTextColor(Color.rgb(((204 - mDistance * 1f) / 204 * 255).toInt(), ((204 - mDistance * 1f) / 204 * 255).toInt(), ((204 - mDistance * 1f) / 204 * 255).toInt()))
                                                    }
                                                    else -> {
                                                        actionBar.setBackgroundColor(Color.rgb(255, 255, 255))
                                                        actionBar.findViewById<TextView>(R.id.appName).setTextColor(Color.rgb(0, 0, 0))
                                                    }
                                                }
                                            } catch (exp: Exception) {
                                                AppCenterLog.error("[TIF]", exp.toString())
                                            }
                                        }
                                    })
                                    activity.findViewById<LinearLayout>(R.id.LoadingAnimation)?.visibility = View.GONE
                                }
                            }
                        }
                    }
                }
            else try {
                activity?.let { activity ->
                    fun processResult(result: String) {
                        val parser = Jsoup.parse(result, "", Parser.xmlParser())
                        val threadListContent = mutableListOf<ThreadItem>()
                        parser.select("item").forEach {
                            if (it.select("link").html().isNotEmpty()) {
                                threadListContent.add(
                                    ThreadItem(
                                        -1,
                                        it.select("title").text(),
                                        it.select("description").text().trim(),
                                        it.select("author").text(),
                                        it.select("pubDate").text(),
                                        it.select("category").text(),
                                        null,
                                        null,
                                        with(it.select("link").html()) {
                                            (this.substring(this.indexOf("tid=") + 4)).toInt()
                                        }
                                    )
                                )
                            }
                        }

                        val layoutManager = LinearLayoutManager(context)
                        layoutManager.orientation = LinearLayoutManager.VERTICAL
                        threadItemList.layoutManager = layoutManager

                        val adapter = ThreadItemAdapter(
                            activity,
                            threadListContent,
                            isHome = true,
                            withHeader = false,
                            withNavigation = false,
                            curCategoryItem = null,
                            curPage = 0,
                            lastPage = 0,
                            disabledCurPage = false,
                            enabledPrev = false,
                            enabledNext = false
                        ) {}
                        runOnUiThread {
                            threadItemList.adapter = adapter
                            var mDistance = 0
                            threadItemList.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                                    mDistance += dy
                                    try {
                                        when {
                                            mDistance == 0 -> {
                                                actionBar.setBackgroundColor(Color.argb(0, 255, 255, 255))
                                                actionBar.findViewById<TextView>(R.id.appName).setTextColor(Color.rgb(255, 255, 255))
                                            }
                                            mDistance <= 204 -> {
                                                actionBar.setBackgroundColor(Color.argb((mDistance * 1f / 204 * 255).toInt(), 255, 255, 255))
                                                actionBar.findViewById<TextView>(R.id.appName)
                                                    .setTextColor(Color.rgb(((204 - mDistance * 1f) / 204 * 255).toInt(), ((204 - mDistance * 1f) / 204 * 255).toInt(), ((204 - mDistance * 1f) / 204 * 255).toInt()))
                                            }
                                            else -> {
                                                actionBar.setBackgroundColor(Color.rgb(255, 255, 255))
                                                actionBar.findViewById<TextView>(R.id.appName).setTextColor(Color.rgb(0, 0, 0))
                                            }
                                        }
                                    } catch (exp: Exception) {
                                        AppCenterLog.error("[TIF]", exp.toString())
                                    }
                                }
                            })
                            activity.findViewById<LinearLayout>(R.id.LoadingAnimation)?.visibility = View.GONE
                        }
                    }

                    val it = HttpExt.retrievePage("http://www.ditiezu.com/?mod=rss")
                    activity.runOnUiThread {
                        activity.findViewById<ViewGroup>(R.id.MainActivity).postDelayed({
                            when (it) {
                                "Failed Retrieved" -> {
                                    Dialog.tip(
                                        resources.getString(R.string.login_tips),
                                        R.drawable.ic_baseline_close_24,
                                        R.color.danger,
                                        activity,
                                        activity.findViewById(R.id.MainActivity),
                                        Dialog.TIME_SHORT
                                    )
                                }
                                else -> {
                                    processResult(it)
                                }
                            }
                        }, 0)
                    }
                }
            } catch (ignored: Exception) {
            }
        }
    }
}