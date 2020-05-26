package com.passionpenguin.ditiezu.ui

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import com.passionpenguin.ditiezu.ForumDisplay
import com.passionpenguin.ditiezu.R
import com.passionpenguin.ditiezu.helper.CategoryAdapter
import com.passionpenguin.ditiezu.helper.CategoryContent

class CategoryFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        val categoryListView: ListView? = view?.findViewById(R.id.CategoryList)
        val categoryContent =
            context?.let { CategoryContent(it) }
        val categoryList = categoryContent?.categoryList
        val categoryId = categoryContent?.categoryId

        categoryListView?.adapter =
            context?.let { ctx ->
                categoryList?.let {
                    CategoryAdapter(
                        ctx,
                        R.layout.category_popup,
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

        return inflater.inflate(R.layout.fragment_category, container, false)
    }
}