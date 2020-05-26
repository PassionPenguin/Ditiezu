package com.passionpenguin.ditiezu

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import com.squareup.picasso.Picasso


class CategoryItem(val title: String, val description: String, val icon: Int)

class CategoryAdapter(
    private var mCtx: Context,
    private var resource: Int,
    private var items: List<CategoryItem>
) : ArrayAdapter<CategoryItem>(mCtx, resource, items) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val layoutInflater: LayoutInflater = LayoutInflater.from(mCtx)
        val view: View = layoutInflater.inflate(resource, null)
        val imageView: ImageView = view.findViewById(R.id.iconIv)
        val title: TextView = view.findViewById(R.id.titleTv)
        val category: CategoryItem = items[position]
        imageView.setImageDrawable(mCtx.resources.getDrawable(category.icon, null))
        title.text = category.title
        return view
    }
}

class ThreadListItem(
    val authorId: Int,
    val title: String,
    val authorName: String,
    val meta: String,
    val target: Int
)

class ThreadListAdapter(
    private var mCtx: Context,
    resource: Int,
    private var items: List<ThreadListItem>
) : ArrayAdapter<ThreadListItem>(mCtx, resource, items) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val layoutInflater: LayoutInflater = LayoutInflater.from(mCtx)
        val view: View = layoutInflater.inflate(R.layout.thread_list_item, null)
        val avatar: ImageView = view.findViewById(R.id.avatar)
        val title: TextView = view.findViewById(R.id.threadTitle)
        val authorName: TextView = view.findViewById(R.id.threadAuthorName)
        val meta: TextView = view.findViewById(R.id.threadMetaInfo)
        val threadItem = items[position]
        title.text = threadItem.title
        authorName.text = threadItem.authorName
        meta.text = threadItem.meta
        Picasso.with(context)
            .load("http://ditiezu.com/uc_server/avatar.php?mod=avatar&uid=${threadItem.authorId}")
            .placeholder(R.mipmap.noavatar_middle_rounded)
            .error(R.mipmap.noavatar_middle_rounded)
            .transform(CircularCornersTransform())
            .into(avatar);
        return view
    }
}

class PrefListItem(
    val name: String = "",
    val description: String = "",
    val value: String = "",
    val toggle: Boolean = false,
    val execFunc: () -> Unit
)

class PrefAdapter(
    private var mCtx: Context,
    resource: Int,
    private var items: List<PrefListItem>
) : ArrayAdapter<PrefListItem>(mCtx, resource, items) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val layoutInflater: LayoutInflater = LayoutInflater.from(mCtx)
        val view: View = layoutInflater.inflate(R.layout.pref_item, null)
        val value = view.findViewById<TextView>(R.id.pref_item_value)
        value.text = items[position].value
        if (!items[position].toggle)
            value.setCompoundDrawables(null, null, null, null)
        view.findViewById<TextView>(R.id.pref_item_description).text = items[position].description
        view.findViewById<TextView>(R.id.pref_item_name).text = items[position].name
        return view
    }
}