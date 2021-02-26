package io.hoarfroster.ditiezu.adapters

import android.app.Activity
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import io.hoarfroster.ditiezu.R
import io.hoarfroster.ditiezu.models.ThreadListItem

class MainFragmentThreadListAdapter(private val activity: Activity, items: List<ThreadListItem>) :
    RecyclerView.Adapter<MainFragmentThreadListAdapter.ViewHolder>() {

    private val mInflater: LayoutInflater = LayoutInflater.from(activity)
    private var mItems: List<ThreadListItem> = items

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            mInflater.inflate(R.layout.layout_thread_list_item, parent, false)
        )
    }

    override fun getItemCount(): Int {
        Log.i("-", mItems.size.toString())
        return mItems.size
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var title: TextView = view.findViewById(R.id.threadTitle)
        var authorName: TextView = view.findViewById(R.id.userName)
        var categoryName: TextView = view.findViewById(R.id.categoryName)
        var authorAvatar: ImageView = view.findViewById(R.id.userAvatar)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = mItems[position]
        holder.title.text = item.title
        holder.categoryName.text = item.categoryName
        holder.authorName.text = item.userName

        Glide.with(activity)
            .load("http://www.ditiezu.com/uc_server/avatar.php?mod=avatar&uid=${item.uid}")
            .placeholder(R.mipmap.noavatar_middle)
            .error(R.mipmap.noavatar_middle)
            .apply(RequestOptions.bitmapTransform(RoundedCorners(8)))
            .into(holder.authorAvatar)

        holder.itemView.setOnClickListener {
//            val i = Intent(activity, ViewThread::class.java)
//            i.putExtra("tid", item.target)
//            i.flags = FLAG_ACTIVITY_NEW_TASK
//            activity.startActivity(i)
        }
    }
}