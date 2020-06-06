package com.passionpenguin.ditiezu.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import com.passionpenguin.ditiezu.*
import com.passionpenguin.ditiezu.helper.*
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
            list?.addHeaderView(layoutInflater.inflate(R.layout.item_home_header, null, false))
            list?.setOnItemClickListener { _, _, position, _ ->
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
    }
}