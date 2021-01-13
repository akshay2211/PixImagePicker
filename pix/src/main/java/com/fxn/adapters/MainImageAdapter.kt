package com.fxn.adapters

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnLongClickListener
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import com.bumptech.glide.load.DecodeFormat
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.fxn.interfaces.OnSelectionListener
import com.fxn.interfaces.SectionIndexer
import com.fxn.modals.Img
import com.fxn.pix.R
import com.fxn.utility.HeaderItemDecoration.StickyHeaderInterface
import com.fxn.utility.Utility
import java.io.File
import java.util.*

/**
 * Created by akshay on 17/03/18.
 */
class MainImageAdapter(context: Context, spanCount: Int) : RecyclerView.Adapter<RecyclerView.ViewHolder>(), StickyHeaderInterface, SectionIndexer {
    val itemList: ArrayList<Img> = ArrayList()
    private var onSelectionListener: OnSelectionListener? = null
    private val layoutParams: FrameLayout.LayoutParams
    private val glide: RequestManager
    private val options: RequestOptions

    fun addImage(image: Img): MainImageAdapter {
        itemList.add(image)
        notifyDataSetChanged()
        return this
    }

    fun addOnSelectionListener(onSelectionListener: OnSelectionListener?) {
        this.onSelectionListener = onSelectionListener
    }

    fun addImageList(images: ArrayList<Img>?) {
        itemList.addAll(images!!)
        notifyDataSetChanged()
    }

    override fun getItemViewType(position: Int): Int {
        if (itemList.size <= position) {
            return 0
        }
        val i = itemList[position]
        return if (i.contentUrl.equals("", ignoreCase = true)) HEADER else ITEM
    }

    fun clearList() {
        itemList.clear()
    }

    fun select(selection: Boolean, pos: Int) {
        itemList[pos].selected = selection
        notifyItemChanged(pos)
    }

    override fun getItemId(position: Int): Long {
        return itemList[position].contentUrl.hashCode().toLong()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == HEADER) {
            HeaderHolder(LayoutInflater.from(parent.context).inflate(R.layout.header_row, parent, false))
        } else {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.main_image, parent, false)
            Holder(view)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val image = itemList[position]
        if (holder is Holder) {
            if (image.media_type == 1) {
                glide.load(image.contentUrl).apply(options).into(holder.preview)
                holder.isVideo.visibility = View.GONE
            } else if (image.media_type == 3) {
                glide.asBitmap()
                        .load(Uri.fromFile(File(image.url)))
                        .apply(options)
                        .into(holder.preview)
                holder.isVideo.visibility = View.VISIBLE
            }
            holder.selection.visibility = if (image.selected) View.VISIBLE else View.GONE
        } else if (holder is HeaderHolder) {
            holder.header.text = image.headerDate
        }
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    override fun getHeaderPositionForItem(itemPosition: Int): Int {
        var position = itemPosition
        var headerPosition = 0
        do {
            if (isHeader(position)) {
                headerPosition = position
                break
            }
            position -= 1
        } while (position >= 0)
        return headerPosition
    }

    override fun getHeaderLayout(headerPosition: Int): Int {
        return R.layout.header_row
    }

    override fun bindHeaderData(header: View?, headerPosition: Int) {
        val image = itemList[headerPosition]
        (header!!.findViewById<View>(R.id.header) as TextView).text = image.headerDate
    }

    override fun isHeader(itemPosition: Int): Boolean {
        return getItemViewType(itemPosition) == 1
    }

    override fun getSectionText(position: Int): String {
        return itemList[position].headerDate
    }

    fun getSectionMonthYearText(position: Int): String {
        return if (itemList.size <= position) {
            ""
        } else itemList[position].headerDate
    }

    inner class Holder internal constructor(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener, OnLongClickListener {
        val preview: ImageView = itemView.findViewById(R.id.preview)
        val selection: ImageView = itemView.findViewById(R.id.selection)
        val isVideo: ImageView = itemView.findViewById(R.id.isVideo)
        override fun onClick(view: View) {
            val id = this.layoutPosition
            onSelectionListener!!.onClick(itemList[id], view, id)
        }

        override fun onLongClick(view: View): Boolean {
            val id = this.layoutPosition
            onSelectionListener!!.onLongClick(itemList[id], view, id)
            return true
        }

        init {
            itemView.setOnClickListener(this)
            itemView.setOnLongClickListener(this)
            preview.layoutParams = layoutParams
        }
    }

    inner class HeaderHolder internal constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val header: TextView = itemView.findViewById(R.id.header)
    }

    companion object {
        const val HEADER = 1
        const val ITEM = 2
        var SPAN_COUNT = 0
        private const val MARGIN = 4
    }

    init {
        SPAN_COUNT = spanCount
        val size = Utility.WIDTH / SPAN_COUNT - MARGIN / 2
        layoutParams = FrameLayout.LayoutParams(size, size)
        layoutParams.setMargins(MARGIN, MARGIN - MARGIN / 2, MARGIN, MARGIN - MARGIN / 2)
        options = RequestOptions().override(size - 50)
                .format(DecodeFormat.PREFER_RGB_565)
                .centerCrop()
                .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
        glide = Glide.with(context)
    }
}