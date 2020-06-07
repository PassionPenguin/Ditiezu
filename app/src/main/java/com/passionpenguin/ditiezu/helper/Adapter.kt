package com.passionpenguin.ditiezu.helper

import android.content.Context
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import androidx.core.graphics.drawable.toBitmap
import com.passionpenguin.ditiezu.HtmlTextView.HtmlHttpImageGetter
import com.passionpenguin.ditiezu.HtmlTextView.HtmlTextView
import com.passionpenguin.ditiezu.R
import com.passionpenguin.ditiezu.ReplyActivity
import com.passionpenguin.ditiezu.ViewThread
import com.squareup.picasso.Picasso
import java.net.URL


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

class ThreadItem(
    val authorId: Int,
    val title: String,
    val content: String,
    val authorName: String,
    val time: String,
    val meta: String,
    val target: Int
)

class ThreadItemListAdapter(
    private var mCtx: Context,
    resource: Int,
    private var items: List<ThreadItem>
) : ArrayAdapter<ThreadItem>(mCtx, resource, items) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val layoutInflater: LayoutInflater = LayoutInflater.from(mCtx)
        val view: View = layoutInflater.inflate(R.layout.item_thread_item, null)
        val searchItem = items[position]
        view.findViewById<TextView>(R.id.threadTitle).text = searchItem.title
        view.findViewById<TextView>(R.id.threadContent).text = searchItem.content
        view.findViewById<TextView>(R.id.threadPostTime).text = searchItem.time
        view.findViewById<TextView>(R.id.threadMetaInfo).text = searchItem.meta
        view.findViewById<TextView>(R.id.threadAuthorName).text = searchItem.authorName
        Picasso.with(context)
            .load("http://ditiezu.com/uc_server/avatar.php?mod=avatar&uid=${searchItem.authorId}")
            .placeholder(R.mipmap.noavatar_middle_rounded)
            .error(R.mipmap.noavatar_middle_rounded)
            .transform(CircularCornersTransform())
            .into(view.findViewById<ImageView>(R.id.avatar))
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
        if (items[position].description !== "")
            with(view.findViewById<TextView>(R.id.pref_item_description)) {
                this.text = items[position].description
                this.visibility = View.VISIBLE
            }
        view.findViewById<TextView>(R.id.pref_item_name).text = items[position].name
        return view
    }
}

class NotificationItem(
    val imageUrl: String,
    val value: String,
    val description: String? = null,
    val time: String,
    val tid: String? = "-1",
    val page: String? = "1"
)

class NotificationsAdapter(
    private var mCtx: Context,
    resource: Int,
    private var items: List<NotificationItem>
) : ArrayAdapter<NotificationItem>(mCtx, resource, items) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val layoutInflater: LayoutInflater = LayoutInflater.from(mCtx)
        val view: View = layoutInflater.inflate(R.layout.item_notification, null)
        val avatar: ImageView = view.findViewById(R.id.avatar)
        val value: TextView = view.findViewById(R.id.notification_value)
        val meta: TextView = view.findViewById(R.id.notification_meta)
        val extraInfo: TextView = view.findViewById(R.id.extraInfo)
        val notification = items[position]
        value.text = notification.value
        meta.text = notification.time
        if (notification.description != null)
            extraInfo.text = notification.description
        else extraInfo.visibility = View.GONE
        if (notification.imageUrl.isEmpty())
            Picasso.with(context)
                .load(R.mipmap.noavatar_middle_rounded)
                .transform(CircularCornersTransform())
                .into(avatar)
        else
            Picasso.with(context)
                .load(notification.imageUrl)
                .placeholder(R.mipmap.noavatar_middle_rounded)
                .error(R.mipmap.noavatar_middle_rounded)
                .transform(CircularCornersTransform())
                .into(avatar)
        return view
    }
}

class ReplyItem(
    val authorId: Int,
    val content: String,
    val authorName: String,
    val time: String,
    val editable: Boolean = false,
    val replyable: Boolean = false,
    val pid: Int,
    val tid: Int
)

class ReplyItemAdapter(
    private var mCtx: Context,
    resource: Int,
    private var items: List<ReplyItem>
) : ArrayAdapter<ReplyItem>(mCtx, resource, items) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val layoutInflater: LayoutInflater = LayoutInflater.from(mCtx)
        val view: View = layoutInflater.inflate(R.layout.item_reply_item, null)
        val replyItem = items[position]
        with(view.findViewById<HtmlTextView>(R.id.threadContent)) {
            this.setHtml(
                replyItem.content,
                HtmlHttpImageGetter(this)
            )
            this.setOnClickATagListener { _, href ->
                val url = URL(href)
                if (url.host.contains("ditiezu.com") && (href?.contains("?mod=viewthread")!! || href.contains(
                        "thread-"
                    ))
                ) {
                    val i = Intent(mCtx, ViewThread::class.java)
                    i.putExtra(
                        "tid", when {
                            href.contains("mod=viewthread") -> href.substring(
                                href.indexOf("tid=") + 4,
                                href.indexOf("&", href.indexOf("tid=") + 4)
                            )
                            href.contains("thread-") -> href.substring(
                                href.indexOf("thread-") + 7,
                                href.indexOf("-", href.indexOf("thread-") + 7)
                            )
                            else -> null
                        }
                    )
                    if (href.contains("page=") && href.contains("mod=viewthread"))
                        i.putExtra(
                            "page",
                            href.substring(
                                href.indexOf("page=") + 4,
                                href.indexOf("&", href.indexOf("page=") + 4)
                            )
                        )
                    else i.putExtra(
                        "page",
                        href.substring(
                            href.indexOf("-", href.indexOf("thread-") + 7),
                            href.indexOf("-", href.indexOf("-", href.indexOf("thread-") + 7))
                        )
                    )
                    i.flags = FLAG_ACTIVITY_NEW_TASK
                    mCtx.startActivity(i)
                }
            }
        }
        view.findViewById<TextView>(R.id.threadMetaInfo).text = replyItem.time
        view.findViewById<TextView>(R.id.threadAuthorName).text = replyItem.authorName

        if (replyItem.editable)
            with(view.findViewById<CheckBox>(R.id.edit)) {
                this.visibility = View.VISIBLE
                this.setOnClickListener {
                    val i = Intent(mCtx, ReplyActivity::class.java)
                    i.putExtra("type", "edit")
                    i.putExtra("pid", replyItem.pid)
                    i.putExtra("tid", replyItem.tid)
                    i.flags = FLAG_ACTIVITY_NEW_TASK
                    mCtx.startActivity(i)
                }
            }
        if (replyItem.replyable)
            with(view.findViewById<CheckBox>(R.id.reply)) {
                this.visibility = View.VISIBLE
                this.setOnClickListener {
                    val i = Intent(mCtx, ReplyActivity::class.java)
                    i.putExtra("type", "reply")
                    i.putExtra("pid", replyItem.pid)
                    i.putExtra("tid", replyItem.tid)
                    i.flags = FLAG_ACTIVITY_NEW_TASK
                    mCtx.startActivity(i)
                }
            }

        Picasso.with(context)
            .load("http://ditiezu.com/uc_server/avatar.php?mod=avatar&uid=${replyItem.authorId}")
            .placeholder(R.mipmap.noavatar_middle_rounded)
            .error(R.mipmap.noavatar_middle_rounded)
            .transform(CircularCornersTransform())
            .into(view.findViewById<ImageView>(R.id.avatar))
        return view
    }
}