package com.passionpenguin.ditiezu.helper

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import androidx.core.graphics.drawable.toBitmap
import com.passionpenguin.ditiezu.R
import com.squareup.picasso.Picasso


class CategoryItem(val title: String, val description: String, val icon: Int, var meta: String)

class CategoryAdapter(
    private var mCtx: Context,
    resource: Int,
    private var items: List<CategoryItem>
) : ArrayAdapter<CategoryItem>(mCtx, resource, items) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val layoutInflater: LayoutInflater = LayoutInflater.from(mCtx)
        val view: View = layoutInflater.inflate(R.layout.item_category, null)
        val icon: ImageView = view.findViewById(R.id.categoryIcon)
        val title: TextView = view.findViewById(R.id.categoryName)
        val meta: TextView = view.findViewById(R.id.categoryMeta)
        val categoryItem = items[position]
        title.text = categoryItem.title
        meta.text = categoryItem.meta
        icon.setImageBitmap(
            CircularCornersTransform().transform(
                mCtx.resources.getDrawable(
                    categoryItem.icon,
                    null
                ).toBitmap()
            )
        )
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
        val view: View = layoutInflater.inflate(R.layout.item_thread_list_item, null)
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
            .into(avatar)
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
        val view: View = layoutInflater.inflate(R.layout.item_pref_item, null)
        val value = view.findViewById<TextView>(R.id.pref_item_value)
        value.text = items[position].value
        if (!items[position].toggle)
            value.setCompoundDrawables(null, null, null, null)
        view.findViewById<TextView>(R.id.pref_item_description).text = items[position].description
        view.findViewById<TextView>(R.id.pref_item_name).text = items[position].name
        return view
    }
}