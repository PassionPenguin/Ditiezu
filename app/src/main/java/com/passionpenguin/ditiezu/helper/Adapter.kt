package com.passionpenguin.ditiezu.helper

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.text.method.LinkMovementMethod
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import android.widget.AdapterView.OnItemSelectedListener
import androidx.core.graphics.scale
import androidx.core.view.get
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.passionpenguin.ditiezu.*
import com.passionpenguin.htmltextview.HtmlHttpImageGetter
import com.passionpenguin.htmltextview.HtmlTextView
import org.jsoup.Jsoup
import java.io.IOException
import java.net.URLEncoder


class CategoryItem(
    val name: String,
    val description: String,
    val icon: Int,
    val meta: String,
    val id: Int
)

class CategoryItemAdapter(val activity: Activity, items: List<CategoryItem>) :
    RecyclerView.Adapter<CategoryItemAdapter.ViewHolder>() {

    private val mInflater: LayoutInflater = LayoutInflater.from(activity)
    var mItems: List<CategoryItem> = items

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            mInflater.inflate(
                R.layout.item_category,
                parent,
                false
            )
        )
    }


    override fun getItemCount(): Int {
        return mItems.size
    }

    class ViewHolder(view: View) :
        RecyclerView.ViewHolder(view) {
        var categoryIcon: ImageView = view.findViewById(R.id.categoryIcon)
        var categoryName: TextView = view.findViewById(R.id.categoryName)
    }

    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int
    ) {
        holder.categoryName.text = mItems[position].name
        Glide.with(activity)
            .load(activity.resources.getDrawable(mItems[position].icon, null))
            .apply(RequestOptions.bitmapTransform(RoundedCorners(8)))
            .into(holder.categoryIcon)
        holder.itemView.setOnClickListener {
            val i = Intent(activity, ForumDisplay::class.java)
            i.putExtra("fid", mItems[position].id)
            i.putExtra("id", position)
            i.flags = FLAG_ACTIVITY_NEW_TASK
            activity.startActivity(i)
        }
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
            .placeholder(R.mipmap.noavatar_middle)
            .error(R.mipmap.noavatar_middle)
            .apply(RequestOptions.bitmapTransform(RoundedCorners(8)))
            .into(view.findViewById(R.id.avatar))
        return view
    }
}

class ThreadItemAdapter(val activity: Activity, items: List<ThreadItem>) :
    RecyclerView.Adapter<ThreadItemAdapter.ViewHolder>() {

    private val mInflater: LayoutInflater = LayoutInflater.from(activity)
    var mItems: List<ThreadItem> = items

    override fun getItemViewType(position: Int): Int {
        return if (position == 0) 0 else 1
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return if (viewType == 0) {
            val vh = ViewHolder(
                mInflater.inflate(
                    R.layout.item_home_header,
                    parent,
                    false
                )
            )
            vh.init(viewType)
            vh
        } else {
            val vh = ViewHolder(
                mInflater.inflate(
                    R.layout.item_thread_item,
                    parent,
                    false
                )
            )
            vh.init(viewType)
            vh
        }
    }


    override fun getItemCount(): Int {
        return mItems.size + 1
    }

    class ViewHolder(private val view: View) :
        RecyclerView.ViewHolder(view) {
        lateinit var recentView: LinearLayout
        lateinit var discoveryNew: LinearLayout
        lateinit var categoryList: LinearLayout
        lateinit var accountView: LinearLayout
        lateinit var itemThreadTitle: TextView
        lateinit var itemThreadContent: TextView
        lateinit var itemThreadPostTime: TextView
        lateinit var itemThreadMetaInfo: TextView
        lateinit var itemThreadAuthorName: TextView
        lateinit var itemAvatar: ImageView

        fun init(viewType: Int) {
            if (viewType == 0) {
                recentView = view.findViewById(R.id.recentView)
                discoveryNew = view.findViewById(R.id.discoveryNew)
                categoryList = view.findViewById(R.id.categoryList)
                accountView = view.findViewById(R.id.accountView)
            } else {
                itemThreadTitle = view.findViewById(R.id.threadTitle)
                itemThreadContent = view.findViewById(R.id.threadContent)
                itemThreadPostTime = view.findViewById(R.id.threadPostTime)
                itemThreadMetaInfo = view.findViewById(R.id.threadMetaInfo)
                itemThreadAuthorName = view.findViewById(R.id.threadAuthorName)
                itemAvatar = view.findViewById(R.id.avatar)
            }
        }
    }

    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int
    ) {
        if (position == 0) {
            holder.recentView.setOnClickListener {
            }
            holder.discoveryNew.setOnClickListener {
                val i = Intent(activity, SearchResultActivity::class.java)
                i.flags = FLAG_ACTIVITY_NEW_TASK
                i.putExtra("kw", "")
                activity.startActivity(i)
            }
            holder.categoryList.setOnClickListener {
                (activity.findViewById<BottomNavigationView>(R.id.nav_view)[0] as ViewGroup)[1].performClick()
            }
            holder.accountView.setOnClickListener {
                (activity.findViewById<BottomNavigationView>(R.id.nav_view)[0] as ViewGroup)[3].performClick()
            }
        } else {
            val item = mItems[position - 1]
            holder.itemThreadTitle.text = item.title
            holder.itemThreadContent.text = item.content
            holder.itemThreadPostTime.text = item.time
            holder.itemThreadMetaInfo.text = item.meta
            holder.itemThreadAuthorName.text = item.authorName
            Glide.with(activity)
                .load("http://www.ditiezu.com/uc_server/avatar.php?mod=avatar&uid=${item.authorId}")
                .placeholder(R.mipmap.noavatar_middle)
                .error(R.mipmap.noavatar_middle)
                .apply(RequestOptions.bitmapTransform(RoundedCorners(8)))
                .into(holder.itemAvatar)

            holder.itemView.setOnClickListener {
                if (position != 0) {
                    val i = Intent(activity, ViewThread::class.java)
                    i.putExtra("tid", mItems[position - 1].target)
                    i.flags = FLAG_ACTIVITY_NEW_TASK
                    activity.startActivity(i)
                }
            }

            holder.itemView.setOnLongClickListener {
                if (position != 0) {
                    TODO("MENU")
                }
                true
            }
        }
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

class NotificationItemAdapter(val activity: Activity, items: List<NotificationItem>) :
    RecyclerView.Adapter<NotificationItemAdapter.ViewHolder>() {

    private val mInflater: LayoutInflater = LayoutInflater.from(activity)
    var mItems: List<NotificationItem> = items

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            mInflater.inflate(
                R.layout.item_notification,
                parent,
                false
            )
        )
    }


    override fun getItemCount(): Int {
        return mItems.size
    }

    class ViewHolder(view: View) :
        RecyclerView.ViewHolder(view) {
        val avatar: ImageView = view.findViewById(R.id.avatar)
        val value: TextView = view.findViewById(R.id.notification_value)
        val meta: TextView = view.findViewById(R.id.notification_meta)
        val extraInfo: TextView = view.findViewById(R.id.extraInfo)
    }

    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int
    ) {
        val notification = mItems[position]
        holder.value.text = notification.value
        holder.meta.text = notification.time
        if (notification.description != null)
            holder.extraInfo.text = notification.description
        else holder.extraInfo.visibility = View.GONE
        if (notification.imageUrl.isEmpty())
            Glide.with(activity)
                .load(R.mipmap.noavatar_middle)
                .apply(RequestOptions.bitmapTransform(RoundedCorners(8)))
                .into(holder.avatar)
        else Glide.with(activity)
            .load(notification.imageUrl)
            .placeholder(R.mipmap.noavatar_middle)
            .error(R.mipmap.noavatar_middle)
            .apply(RequestOptions.bitmapTransform(RoundedCorners(8)))
            .into(holder.avatar)

        holder.itemView.setOnClickListener {
            if (notification.tid != "-1") {
                val i = Intent(activity, ViewThread::class.java)
                i.putExtra("tid", notification.tid?.toInt())
                i.putExtra("page", notification.page?.toInt())
                activity.startActivity(i)
            }
        }
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
            try {
                this.setHtml(
                    replyItem.content,
                    HtmlHttpImageGetter(this)
                )
            } catch (ignored: Exception) {

            }
            this.movementMethod = LinkMovementMethod.getInstance()
        }
        view.findViewById<TextView>(R.id.threadMetaInfo).text = replyItem.time
        view.findViewById<TextView>(R.id.threadAuthorName).text = replyItem.authorName

        if (replyItem.editable)
            with(view.findViewById<LinearLayout>(R.id.edit)) {
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
            with(view.findViewById<LinearLayout>(R.id.reply)) {
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
            with(view.findViewById<LinearLayout>(R.id.rate)) {
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
            .placeholder(R.mipmap.noavatar_middle)
            .error(R.mipmap.noavatar_middle)
            .apply(RequestOptions.bitmapTransform(RoundedCorners(8)))
            .into(view.findViewById(R.id.avatar))
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
        .placeholder(R.mipmap.noavatar_middle)
        .error(R.mipmap.noavatar_middle)
        .apply(RequestOptions.bitmapTransform(RoundedCorners(8)))
        .into(view.findViewById(R.id.avatar))
    return view
}

fun attachImageView(
    mCtx: Activity,
    url: String
): View {
    val layoutInflater: LayoutInflater = LayoutInflater.from(mCtx)
    val view: View = layoutInflater.inflate(R.layout.item_image, null)

    fun loadBitmap(URL: String, returnVal: (bitmap: Bitmap?) -> Unit) {
        var bitmap: Bitmap?
        Thread {
            try {
                HttpExt().openHttpUrlConn(URL) {
                    bitmap = BitmapFactory.decodeStream(it)
                    val height = bitmap!!.height
                    val width = bitmap!!.width
                    val size = mCtx.resources.getDimension(R.dimen._360)
                    if (height > width)
                        bitmap!!.scale((size * width / height).toInt(), size.toInt())
                    else bitmap!!.scale(size.toInt(), (size * height / width).toInt())
                    returnVal(bitmap)
                }
            } catch (ignored: IOException) {
            }
        }.start()
    }
    Log.i("", url)
    loadBitmap("http://www.ditiezu.com/$url") {
        mCtx.runOnUiThread {
            Glide.with(mCtx)
                .load(it)
                .into(view.findViewById(R.id.image))
        }
    }
    return view
}