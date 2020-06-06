package com.passionpenguin.ditiezu.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.passionpenguin.ditiezu.ForumDisplay
import com.passionpenguin.ditiezu.R
import com.passionpenguin.ditiezu.helper.*
import org.jsoup.Jsoup

class CategoryFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_category, container, false)

        val categoryListView: ListView? = view?.findViewById(R.id.categoryList)
        val categoryContent =
            context?.let { CategoryContent(it) }
        val categoryList = categoryContent?.categoryList
        val categoryId = categoryContent?.categoryId

        activity?.findViewById<LinearLayout>(R.id.LoadingMaskContainer)?.visibility =
            View.VISIBLE

        categoryListView?.adapter =
            context?.let { ctx ->
                categoryList?.let {
                    CategoryAdapter(
                        ctx,
                        0,
                        categoryList
                    )
                }
            }
        categoryListView?.setOnItemClickListener { _, _, position, _ ->
            val i = Intent(context, ForumDisplay::class.java)
            i.putExtra(
                "fid",
                categoryId?.get(position)
            )
            startActivity(i)
        }
        HttpExt().retrievePage("http://www.ditiezu.com/") {
            if (it == "Failed Retrieved") {
                // Failed Retrieved
                Log.i("HTTPEXT", "FAILED RETRIEVED")

                activity?.runOnUiThread {
                    categoryListView?.adapter =
                        context?.let { ctx ->
                            categoryList?.let {
                                CategoryAdapter(
                                    ctx,
                                    0,
                                    categoryList
                                )
                            }
                        }
                }
            } else {
                val parser = Jsoup.parse(it)
                try {
                    parser.select("#category_2 td dd:nth-child(2), .fl_i")
                        .forEachIndexed { index, child ->
                            categoryList?.get(index)?.meta =
                                "主题: " + child.text().replace("主题: ", "").replace(" / ", ", 帖数: ")
                        }
                } catch (e: Exception) {
                    Log.i("", e.toString())
                }

                activity?.runOnUiThread {
                    categoryListView?.adapter =
                        context?.let { ctx ->
                            categoryList?.let {
                                CategoryAdapter(
                                    ctx,
                                    0,
                                    categoryList
                                )
                            }
                        }
                    categoryListView?.setOnItemClickListener { _, _, position, _ ->
                        val i = Intent(context, ForumDisplay::class.java)
                        i.putExtra(
                            "fid",
                            categoryId?.get(position)
                        )
                        startActivity(i)
                    }
                }
            }
            activity?.runOnUiThread {
                activity?.findViewById<LinearLayout>(R.id.LoadingMaskContainer)?.visibility =
                    View.GONE
            }
        }

        return view
    }
}