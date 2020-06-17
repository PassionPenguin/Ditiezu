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
import com.passionpenguin.ditiezu.R
import com.passionpenguin.ditiezu.SearchResultActivity
import com.passionpenguin.ditiezu.helper.Dialog
import com.passionpenguin.ditiezu.helper.HttpExt
import com.passionpenguin.ditiezu.helper.ThreadItem
import com.passionpenguin.ditiezu.helper.ThreadItemAdapter
import kotlinx.android.synthetic.main.fragment_item_list.*
import org.jsoup.Jsoup


class ThreadItemFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_item_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(actionBar.findViewById<EditText>(R.id.app_search_input)) {
            this?.setOnKeyListener(object : View.OnKeyListener {
                override fun onKey(
                    v: View?,
                    keyCode: Int,
                    event: KeyEvent
                ): Boolean {
                    val t = v as EditText
                    if (event.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER && t.text.toString()
                            .trim().isNotEmpty()
                    ) {
                        val i = Intent(context, SearchResultActivity::class.java)
                        i.putExtra("kw", t.text.toString())
                        context.startActivity(i)
                        return true
                    }
                    return false
                }
            })
        }

        activity?.let { activity ->
            fun processResult(result: String) {
                val parser = Jsoup.parse(result)
                val threadListContent = mutableListOf<ThreadItem>()
                parser.select("ul.comiis_onemiddleulone li").forEach {
                    val author = it.select("code a")
                    val authorName = author.text()
                    val category = it.select(".orgen").text()
                    val title = it.select(".blackvs")
                    threadListContent.add(
                        ThreadItem(
                            author.attr("href").substring(
                                author.attr("href").indexOf("uid-") + 4,
                                author.attr("href").indexOf(".html")
                            ).toInt(),
                            title.text(),
                            "",
                            authorName,
                            "[$category]",
                            "来自头条推荐",
                            title.attr("href")
                                .substring(30, title.attr("href").lastIndexOf("-1-1")).toInt()
                        )
                    )
                }

                val layoutManager = LinearLayoutManager(context)
                layoutManager.orientation = LinearLayoutManager.VERTICAL
                threadItemList.layoutManager = layoutManager

                val adapter =
                    ThreadItemAdapter(activity, threadListContent)
                threadItemList.adapter = adapter
                var mDistance = 0
                threadItemList.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                        mDistance += dy
                        when {
                            mDistance == 0 -> {
                                actionBar.setBackgroundColor(
                                    Color.argb(
                                        0,
                                        255,
                                        255,
                                        255
                                    )
                                )
                                actionBar.findViewById<TextView>(R.id.appName)
                                    .setTextColor(
                                        Color.rgb(
                                            255,
                                            255,
                                            255
                                        )
                                    )
                            }
                            mDistance <= 204 -> {
                                actionBar.setBackgroundColor(
                                    Color.argb(
                                        (mDistance * 1f / 204 * 255).toInt(),
                                        255,
                                        255,
                                        255
                                    )
                                )
                                actionBar.findViewById<TextView>(R.id.appName)
                                    .setTextColor(
                                        Color.rgb(
                                            ((204 - mDistance * 1f) / 204 * 255).toInt(),
                                            ((204 - mDistance * 1f) / 204 * 255).toInt(),
                                            ((204 - mDistance * 1f) / 204 * 255).toInt()
                                        )
                                    )
                            }
                            else -> {
                                actionBar.setBackgroundColor(
                                    Color.rgb(255, 255, 255)
                                )
                                actionBar.findViewById<TextView>(R.id.appName)
                                    .setTextColor(Color.rgb(0, 0, 0))
                            }
                        }
                    }
                })
            }

            activity.findViewById<LinearLayout>(R.id.LoadingAnimation)?.visibility =
                View.VISIBLE
            HttpExt().retrievePage("http://www.ditiezu.com/") {
                activity.runOnUiThread {
                    activity.findViewById<ViewGroup>(R.id.MainActivity).postDelayed({
                        when (it) {
                            "Failed Retrieved" -> {
                                Dialog().tip(
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
                        activity.findViewById<LinearLayout>(R.id.LoadingAnimation)?.visibility =
                            View.GONE
                    }, 0)
                }
            }
        }
    }
}