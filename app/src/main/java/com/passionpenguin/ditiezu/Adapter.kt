package com.passionpenguin.ditiezu

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView

class CategoryItem(val title: String, val photo: Int)

class CategoryAdapter(
    private var mCtx: Context,
    private var resource: Int,
    private var items: List<CategoryItem>
) : ArrayAdapter<CategoryItem>(mCtx, resource, items) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val layoutInflater: LayoutInflater = LayoutInflater.from(mCtx)
        val view: View = layoutInflater.inflate(resource, parent)
        val imageView: ImageView = view.findViewById(R.id.iconIv)
        val title: TextView = view.findViewById(R.id.titleTv)
        val category: CategoryItem = items[position]
        imageView.setImageDrawable(mCtx.resources.getDrawable(category.photo, null))
        title.text = category.title
        return view
    }
}

class ThreadListItem(val authorId: Int, val title: String, val meta: String, val target: Int)

class ThreadListAdapter(
    private var mCtx: Context,
    private var resource: Int,
    private var items: List<ThreadListItem>
) : ArrayAdapter<ThreadListItem>(mCtx, resource, items) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val layoutInflater: LayoutInflater = LayoutInflater.from(mCtx)
        val view: View = layoutInflater.inflate(resource, parent)
        val title: TextView = view.findViewById(R.id.threadTitle)
        val meta: TextView = view.findViewById(R.id.threadMetaInfo)
        val category = items[position]
        title.text = category.title
        meta.text = category.meta
        return view
    }
}