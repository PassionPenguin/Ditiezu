package com.passionpenguin.ditiezu.ui

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import com.passionpenguin.ditiezu.*
import com.passionpenguin.ditiezu.helper.*
import kotlinx.android.synthetic.main.fragment_item_list.*
import org.jsoup.Jsoup

class ThreadItemFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        activity?.findViewById<LinearLayout>(R.id.tips)?.removeAllViews()
        return inflater.inflate(R.layout.fragment_item_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

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

            val list = threadItemList
            list?.adapter =
                context?.let {
                    ThreadItemListAdapter(
                        it,
                        0,
                        threadListContent
                    )
                }
            list?.addHeaderView(layoutInflater.inflate(R.layout.item_home_header, list, false))
            list?.setOnItemClickListener { _, _, position, _ ->
                if (position != 0) {
                    val i = Intent(context, ViewThread::class.java)
                    i.putExtra("tid", threadListContent[position - 1].target)
                    context?.startActivity(i)
                }
            }
        }

        activity?.findViewById<LinearLayout>(R.id.LoadingMaskContainer)?.visibility = View.VISIBLE
        HttpExt().retrievePage("http://www.ditiezu.com/") {
            activity?.runOnUiThread {
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
                        processResult(it)
                        null
                    }
                }
                if (v != null)
                    activity?.findViewById<LinearLayout>(R.id.tips)?.addView(v)
                activity?.findViewById<LinearLayout>(R.id.LoadingMaskContainer)?.visibility =
                    View.GONE
            }
        }
        activity?.findViewById<TextView>(R.id.title)?.text =
            resources.getString(R.string.home_title)
    }
}