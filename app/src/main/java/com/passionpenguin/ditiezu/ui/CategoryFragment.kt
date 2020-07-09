package com.passionpenguin.ditiezu.ui

import android.content.Intent
import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.passionpenguin.ditiezu.R
import com.passionpenguin.ditiezu.SearchResultActivity
import com.passionpenguin.ditiezu.helper.CategoryContent
import com.passionpenguin.ditiezu.helper.CategoryItemAdapter
import kotlinx.android.synthetic.main.fragment_action_bar.*
import kotlinx.android.synthetic.main.fragment_category.*

class CategoryFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_category, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        activity?.let { activity ->
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
            actionBar.setBackgroundColor(resources.getColor(R.color.surface, null))
            actionBarLayout.findViewById<TextView>(R.id.appName).setTextColor(resources.getColor(R.color.black, null))
            val categoryContent = CategoryContent(activity)
            val list = categoryContent.categoryList
            val adapter = CategoryItemAdapter(activity, list)
            categoryList.layoutManager = GridLayoutManager(activity, 2)
            categoryList.adapter = adapter
        }
    }
}