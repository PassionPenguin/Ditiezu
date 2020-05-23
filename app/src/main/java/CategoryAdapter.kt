package com.passionpenguin.ditiezu

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView

class Model(val title: String, val desc: String, val photo: Int)

class CategoryAdapter(
    private var mCtx: Context,
    private var resource: Int,
    private var items: List<Model>
) :
    ArrayAdapter<Model>(mCtx, resource, items) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val layoutInflater: LayoutInflater = LayoutInflater.from(mCtx)
        val view: View = layoutInflater.inflate(resource, null)
        val imageView: ImageView = view.findViewById(R.id.iconIv)
        val title: TextView = view.findViewById(R.id.titleTv)
        val description: TextView = view.findViewById(R.id.descTv)
        val category: Model = items[position]
        imageView.setImageDrawable(mCtx.resources.getDrawable(category.photo, null))
        title.text = category.title
        description.text = category.desc
        return view
    }
}