package com.passionpenguin.ditiezu.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.ListView
import com.passionpenguin.ditiezu.*
import com.passionpenguin.ditiezu.helper.HttpExt
import com.passionpenguin.ditiezu.helper.ThreadListAdapter
import com.passionpenguin.ditiezu.helper.ThreadListItem
import org.jsoup.Jsoup

class ThreadItemFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_item_list, container, false)

        fun processResult(result: String) {
            val parser = Jsoup.parse(result)
            val threadListContent = mutableListOf<ThreadListItem>()
            parser.select("ul.comiis_onemiddleulone li").forEach {
                val author = it.select("code a")
                val authorName = author.text()
                val category = it.select(".orgen").text()
                val title = it.select(".blackvs")
                threadListContent.add(
                    ThreadListItem(
                        author.attr("href").substring(
                            author.attr("href").indexOf("uid-") + 4,
                            author.attr("href").indexOf(".html")
                        ).toInt(),
                        title.text(),
                        authorName,
                        "来自头条推荐 · $category",
                        title.attr("href")
                            .substring(30, title.attr("href").lastIndexOf("-1-1")).toInt()
                    )
                )
            }

            val list = activity?.findViewById<ListView>(R.id.threadItemList)
            list?.adapter =
                context?.let {
                    ThreadListAdapter(
                        it,
                        0,
                        threadListContent
                    )
                }
            list?.addHeaderView(inflater.inflate(R.layout.item_home_header, container, false))
            activity?.findViewById<ListView>(R.id.threadItemList)
                ?.setOnItemClickListener { _, _, position, _ ->
                    if (position != 0) {
                        val i = Intent(context, ViewThread::class.java)
                        i.putExtra("tid", threadListContent[position - 1].target)
                        context?.startActivity(i)
                    }
                }
            activity?.findViewById<LinearLayout>(R.id.LoadingMaskContainer)?.visibility = View.GONE
        }

        activity?.findViewById<LinearLayout>(R.id.LoadingMaskContainer)?.visibility = View.VISIBLE
        HttpExt().retrievePage("http://www.ditiezu.com/") {
            activity?.runOnUiThread {
                if (it == "Failed Retrieved") {
                    // Failed Retrieved
                    Log.i("HTTPEXT", "FAILED RETRIEVED")
                }
                processResult(it)
            }
        }
        return view
    }
}