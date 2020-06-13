package com.passionpenguin.ditiezu.helper

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.text.method.LinkMovementMethod
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import android.widget.AdapterView.OnItemSelectedListener
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.request.RequestOptions
import com.passionpenguin.ditiezu.PostActivity
import com.passionpenguin.ditiezu.R
import com.passionpenguin.htmltextview.HtmlHttpImageGetter
import com.passionpenguin.htmltextview.HtmlTextView
import org.jsoup.Jsoup
import java.net.URLEncoder


class CategoryItem(val title: String, val description: String, val icon: Int, var meta: String)

class CategoryAdapter(
    private var mCtx: Context,
    resource: Int,
    private var items: List<CategoryItem>
) : ArrayAdapter<CategoryItem>(mCtx, resource, items) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val layoutInflater: LayoutInflater = LayoutInflater.from(mCtx)
        val view: View = layoutInflater.inflate(R.layout.item_category, parent, false)
        val icon: ImageView = view.findViewById(R.id.categoryIcon)
        val title: TextView = view.findViewById(R.id.categoryName)
        val meta: TextView = view.findViewById(R.id.categoryMeta)
        val categoryItem = items[position]
        title.text = categoryItem.title
        meta.text = categoryItem.meta
        Glide.with(mCtx).load(
            mCtx.resources.getDrawable(
                categoryItem.icon,
                null
            )
        ).apply(RequestOptions.bitmapTransform(CircleCrop()))
            .into(icon)
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
        val view: View = layoutInflater.inflate(R.layout.item_thread_list_item, parent, false)
        val avatar: ImageView = view.findViewById(R.id.avatar)
        val title: TextView = view.findViewById(R.id.threadTitle)
        val authorName: TextView = view.findViewById(R.id.threadAuthorName)
        val meta: TextView = view.findViewById(R.id.threadMetaInfo)
        val threadItem = items[position]
        title.text = threadItem.title
        authorName.text = threadItem.authorName
        meta.text = threadItem.meta
        Glide.with(context)
            .load("http://www.ditiezu.com/uc_server/avatar.php?mod=avatar&uid=${threadItem.authorId}")
            .placeholder(R.mipmap.noavatar_middle_rounded)
            .error(R.mipmap.noavatar_middle_rounded)
            .apply(RequestOptions.bitmapTransform(CircleCrop()))
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
        val view: View = layoutInflater.inflate(R.layout.item_thread_item, parent, false)
        val searchItem = items[position]
        view.findViewById<TextView>(R.id.threadTitle).text = searchItem.title
        view.findViewById<TextView>(R.id.threadContent).text = searchItem.content
        view.findViewById<TextView>(R.id.threadPostTime).text = searchItem.time
        view.findViewById<TextView>(R.id.threadMetaInfo).text = searchItem.meta
        view.findViewById<TextView>(R.id.threadAuthorName).text = searchItem.authorName
        Glide.with(context)
            .load("http://www.ditiezu.com/uc_server/avatar.php?mod=avatar&uid=${searchItem.authorId}")
            .placeholder(R.mipmap.noavatar_middle_rounded)
            .error(R.mipmap.noavatar_middle_rounded)
            .apply(RequestOptions.bitmapTransform(CircleCrop()))
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

fun prefView(
    mCtx: Context,
    name: String = "",
    description: String = "",
    value: String = "",
    toggle: Boolean = false,
    execFunc: () -> Unit
): View {
    val layoutInflater: LayoutInflater = LayoutInflater.from(mCtx)
    val view: View = layoutInflater.inflate(R.layout.item_pref_item, null)
    val v = view.findViewById<TextView>(R.id.pref_item_value)
    v.text = value
    if (!toggle)
        v.setCompoundDrawables(null, null, null, null)
    if (description !== "")
        with(view.findViewById<TextView>(R.id.pref_item_description)) {
            this.text = description
            this.visibility = View.VISIBLE
        }
    view.findViewById<TextView>(R.id.pref_item_name).text = name
    view.setOnClickListener {
        execFunc()
    }
    return view
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
        val view: View = layoutInflater.inflate(R.layout.item_notification, parent, false)
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
            Glide.with(context)
                .load(R.mipmap.noavatar_middle_rounded)
                .apply(RequestOptions.bitmapTransform(CircleCrop()))
                .into(avatar)
        else
            Glide.with(context)
                .load(notification.imageUrl)
                .placeholder(R.mipmap.noavatar_middle_rounded)
                .error(R.mipmap.noavatar_middle_rounded)
                .apply(RequestOptions.bitmapTransform(CircleCrop()))
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
    val rateable: Boolean = false,
    val pid: Int,
    val tid: Int,
    var rateLog: List<RateItem>? = null,
    val withPopularity: Boolean = false,
    val withMoney: Boolean = false,
    val withPrestige: Boolean = false,
    val participantsNum: String = "",
    val rateContent: String = ""
)

class ReplyItemAdapter(
    private var mCtx: Context,
    resource: Int,
    private var items: List<ReplyItem>,
    private val activity: Activity,
    private val formhash: String
) : ArrayAdapter<ReplyItem>(mCtx, resource, items) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val layoutInflater: LayoutInflater = LayoutInflater.from(mCtx)
        val view: View = layoutInflater.inflate(R.layout.item_reply_item, parent, false)
        val replyItem = items[position]
        with(view.findViewById<HtmlTextView>(R.id.threadContent)) {
            this.setHtml(
                replyItem.content,
                HtmlHttpImageGetter(this)
            )
            this.movementMethod = LinkMovementMethod.getInstance()
        }
        view.findViewById<TextView>(R.id.threadMetaInfo).text = replyItem.time
        view.findViewById<TextView>(R.id.threadAuthorName).text = replyItem.authorName

        if (replyItem.editable)
            with(view.findViewById<CheckBox>(R.id.edit)) {
                this.visibility = View.VISIBLE
                this.setOnClickListener {
                    val i = Intent(mCtx, PostActivity::class.java)
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
                    val i = Intent(mCtx, PostActivity::class.java)
                    i.putExtra("type", "reply")
                    i.putExtra("tid", replyItem.tid)
                    i.putExtra("pid", replyItem.pid)
                    i.putExtra("reppid", replyItem.pid)
                    i.putExtra("reppost", replyItem.pid)
                    i.flags = FLAG_ACTIVITY_NEW_TASK
                    mCtx.startActivity(i)
                }
            }
        if (replyItem.rateable)
            with(view.findViewById<CheckBox>(R.id.rate)) {
                this.visibility = View.VISIBLE
                this.setOnClickListener {
                    val viewThread = activity.findViewById<ViewGroup>(R.id.viewThread)
                    var s =
                        HttpExt().asyncRetrievePage("http://www.ditiezu.com/forum.php?mod=misc&action=rate&tid=${replyItem.tid}&pid=${replyItem.pid}&infloat=yes&handlekey=rate&t=&inajax=1&ajaxtarget=fwin_content_rate")
                    when (s) {
                        "Failed Retrieved" -> {
                            Dialog().tip(
                                resources.getString(R.string.failed_retrieved),
                                R.drawable.ic_baseline_close_24,
                                R.color.danger,
                                activity,
                                viewThread,
                                Dialog.TIME_SHORT
                            )
                        }
                        else -> {
                            s = s.substring(53, s.length - 10)
                            val p = Jsoup.parse(s)
                            when {
                                p.select(".alert_error").isNotEmpty() -> {
                                    Dialog().tip(
                                        p.select(".alert_error").text(),
                                        R.drawable.ic_baseline_close_24,
                                        R.color.danger,
                                        activity,
                                        viewThread,
                                        Dialog.TIME_SHORT
                                    )
                                }
                                else -> {
                                    Dialog().create(
                                        activity,
                                        activity.findViewById(R.id.viewThread),
                                        resources.getString(R.string.rate),
                                        resources.getString(R.string.rate_title),
                                        resources.getString(R.string.rate_description),
                                        { v, _ ->
                                            if (v.findViewById<TextView>(R.id.reason).text == "") {
                                                Dialog().tip(
                                                    resources.getString(R.string.require_reason),
                                                    R.drawable.ic_baseline_close_24,
                                                    R.color.danger,
                                                    activity,
                                                    viewThread,
                                                    Dialog.TIME_SHORT
                                                )
                                            } else {
                                                val str = HttpExt().asyncPostPage(
                                                    "http://www.ditiezu.com/forum.php?mod=misc&action=rate&ratesubmit=yes&infloat=yes&inajax=1",
                                                    "formhash=$formhash&tid=${replyItem.tid}&pid=${replyItem.pid}&handlekey=rate&reason=${URLEncoder.encode(
                                                        v.findViewById<EditText>(R.id.reason).text.toString(),
                                                        "GBK"
                                                    )}&score4=${v.findViewById<Spinner>(R.id.score).selectedItem}"
                                                )

                                                val response = str.substring(
                                                    str.indexOf("_rate('") + 33,
                                                    str.indexOf(
                                                        "'", str.indexOf("_rate('") + 34
                                                    )
                                                )
                                                when {
                                                    str == "Failed Retrieved" -> {
                                                        Dialog().tip(
                                                            resources.getString(R.string.failed_retrieved),
                                                            R.drawable.ic_baseline_close_24,
                                                            R.color.danger,
                                                            activity,
                                                            viewThread,
                                                            Dialog.TIME_SHORT
                                                        )
                                                    }
                                                    str.contains("succeed") -> {
                                                        Dialog().tip(
                                                            response,
                                                            R.drawable.ic_baseline_check_24,
                                                            R.color.primary500,
                                                            activity,
                                                            viewThread,
                                                            Dialog.TIME_SHORT
                                                        )
                                                    }
                                                    str.contains("error") -> {
                                                        Dialog().tip(
                                                            response,
                                                            R.drawable.ic_baseline_close_24,
                                                            R.color.danger,
                                                            activity,
                                                            viewThread,
                                                            Dialog.TIME_SHORT
                                                        )
                                                    }
                                                }
                                            }
                                        }) { v, w ->
                                        v.addView(
                                            LayoutInflater.from(mCtx)
                                                .inflate(R.layout.fragment_rate, v, false)
                                        )
                                        with(v.findViewById<Spinner>(R.id.reasonList)) {
                                            createFromResource(
                                                activity,
                                                R.array.rate_reason,
                                                android.R.layout.simple_spinner_dropdown_item
                                            ).also { adapter ->
                                                // Specify the layout to use when the list of choices appears
                                                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                                                // Apply the adapter to the spinner
                                                this.adapter = adapter
                                            }
                                            this.onItemSelectedListener =
                                                object : OnItemSelectedListener {
                                                    override fun onItemSelected(
                                                        parent: AdapterView<*>?,
                                                        view: View,
                                                        position: Int,
                                                        id: Long
                                                    ) {
                                                        v.findViewById<EditText>(R.id.reason)
                                                            .setText(resources.getStringArray(R.array.rate_reason)[position])
                                                    }

                                                    override fun onNothingSelected(parent: AdapterView<*>?) {}
                                                }
                                        }
                                        with(v.findViewById<Spinner>(R.id.score)) {
                                            createFromResource(
                                                activity,
                                                R.array.popularity_score,
                                                android.R.layout.simple_spinner_dropdown_item
                                            ).also { adapter ->
                                                // Specify the layout to use when the list of choices appears
                                                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                                                // Apply the adapter to the spinner
                                                this.adapter = adapter
                                            }
                                        }
                                        with(p.select("td:last-child")[0].text().toInt()) {
                                            val restScore = v.findViewById<TextView>(R.id.rest)
                                            restScore.text =
                                                resources.getString(
                                                    R.string.rest_score, this
                                                )
                                            if (this < 3)
                                                restScore.setTextColor(
                                                    resources.getColor(
                                                        R.color.danger,
                                                        null
                                                    )
                                                )
                                        }
                                        w.update()
                                    }
                                }
                            }
                        }
                    }
                }
            }
        with(replyItem.rateLog) {
            if (this != null && this.isNotEmpty()) {
                val log = LinearLayout(mCtx)
                log.orientation = LinearLayout.VERTICAL
                val v = layoutInflater.inflate(R.layout.item_rate_log_header, log, false)
                v.findViewById<TextView>(R.id.participantsNum).text =
                    mCtx.resources.getString(
                        R.string.number_of_participants,
                        replyItem.participantsNum
                    )
                if (replyItem.withPopularity) v.findViewById<TextView>(R.id.popularity).visibility =
                    View.VISIBLE
                if (replyItem.withMoney) v.findViewById<TextView>(R.id.money).visibility =
                    View.VISIBLE
                if (replyItem.withPrestige) v.findViewById<TextView>(R.id.prestige).visibility =
                    View.VISIBLE
                log.addView(v)
                replyItem.rateLog?.forEach {
                    log.addView(
                        rateView(
                            mCtx,
                            it,
                            replyItem.withPopularity,
                            replyItem.withMoney,
                            replyItem.withPrestige
                        )
                    )
                }
                val f = HtmlTextView(mCtx)
                f.setHtml(replyItem.rateContent)
                f.height = mCtx.resources.getDimension(R.dimen._32).toInt()
                f.gravity = Gravity.CENTER_VERTICAL
                f.textSize = 12F
                log.addView(f)
                view.findViewById<HorizontalScrollView>(R.id.Container).addView(log)
                log.layoutParams = FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.MATCH_PARENT,
                    FrameLayout.LayoutParams.WRAP_CONTENT
                )
            }
        }

        Glide.with(context)
            .load("http://ditiezu.com/uc_server/avatar.php?mod=avatar&uid=${replyItem.authorId}")
            .placeholder(R.mipmap.noavatar_middle_rounded)
            .error(R.mipmap.noavatar_middle_rounded)
            .apply(RequestOptions.bitmapTransform(CircleCrop()))
            .into(view.findViewById<ImageView>(R.id.avatar))
        return view
    }
}

class RateItem(
    val authorId: Int,
    val authorName: String,
    val popularity: String,
    val money: String,
    val prestige: String,
    val reason: String
)

fun rateView(
    mCtx: Context,
    rateLog: RateItem,
    withPopularity: Boolean = false,
    withMoney: Boolean = false,
    withPrestige: Boolean = false
): View {
    val layoutInflater: LayoutInflater = LayoutInflater.from(mCtx)
    val view: View = layoutInflater.inflate(R.layout.item_rate_log, null)
    val popularity: TextView = view.findViewById(R.id.popularity)
    val money: TextView = view.findViewById(R.id.money)
    val prestige: TextView = view.findViewById(R.id.prestige)
    view.findViewById<TextView>(R.id.reason).text = rateLog.reason
    view.findViewById<TextView>(R.id.authorName).text = rateLog.authorName
    popularity.text = rateLog.popularity
    money.text = rateLog.money
    prestige.text = rateLog.prestige
    if (withPopularity) popularity.visibility = View.VISIBLE
    if (withMoney) money.visibility = View.VISIBLE
    if (withPrestige) prestige.visibility = View.VISIBLE
    Glide.with(mCtx)
        .load("http://www.ditiezu.com/uc_server/avatar.php?mod=avatar&uid=${rateLog.authorId}")
        .placeholder(R.mipmap.noavatar_middle_rounded)
        .error(R.mipmap.noavatar_middle_rounded)
        .apply(RequestOptions.bitmapTransform(CircleCrop()))
        .into(view.findViewById(R.id.avatar))
    return view
}