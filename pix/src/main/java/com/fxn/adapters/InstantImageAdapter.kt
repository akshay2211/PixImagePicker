package com.fxn.adapters

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnLongClickListener
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.FitCenter
import com.bumptech.glide.request.RequestOptions
import com.fxn.interfaces.OnSelectionListener
import com.fxn.modals.Img
import com.fxn.pix.R
import com.fxn.utility.Utility.Companion.convertDpToPixel
import java.io.File
import java.util.*

/**
 * Created by akshay on 17/03/18.
 */
class InstantImageAdapter(context: Context) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    val itemList: ArrayList<Img> = ArrayList()
    private var onSelectionListener: OnSelectionListener? = null
    private val glide: RequestManager
    private val options: RequestOptions
    private val size: Float = convertDpToPixel(72f, context) - 2
    private val margin = 3
    private val padding: Int
    fun addOnSelectionListener(onSelectionListener: OnSelectionListener?) {
        this.onSelectionListener = onSelectionListener
    }

    fun addImage(image: Img): InstantImageAdapter {
        itemList.add(image)
        notifyDataSetChanged()
        return this
    }

    fun addImageList(images: ArrayList<Img>?) {
        itemList.addAll(images!!)
        notifyDataSetChanged()
    }

    fun clearList() {
        itemList.clear()
    }

    fun select(selection: Boolean, pos: Int) {
        if (pos < 100) {
            itemList[pos].selected = selection
            notifyItemChanged(pos)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == MainImageAdapter.Companion.HEADER) {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.inital_image, parent, false)
            HolderNone(view)
        } else {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.inital_image, parent, false)
            Holder(view)
        }
    }

    override fun getItemViewType(position: Int): Int {
        val image = itemList[position]
        return if (image.contentUrl.isEmpty()) MainImageAdapter.Companion.HEADER else MainImageAdapter.Companion.ITEM
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val image = itemList[position]
        if (holder is Holder) {
            val layoutParams = FrameLayout.LayoutParams(size.toInt(), size.toInt())
            if (position == 0) {
                layoutParams.setMargins(-(margin / 2), margin, margin, margin)
            } else {
                layoutParams.setMargins(margin, margin, margin, margin)
            }
            holder.itemView.layoutParams = layoutParams
            holder.selection.setPadding(padding, padding, padding, padding)
            holder.preview.layoutParams = layoutParams
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
        } else {
            val noneHolder = holder as HolderNone
            val layoutParams = FrameLayout.LayoutParams(0, 0)
            noneHolder.itemView.layoutParams = layoutParams
            noneHolder.itemView.visibility = View.GONE
        }
    }

    override fun getItemCount(): Int {
        return itemList.size
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
        }
    }

    inner class HolderNone internal constructor(itemView: View?) : RecyclerView.ViewHolder(itemView!!)

    init {
        padding = (size / 3.5).toInt()
        glide = Glide.with(context)
        options = RequestOptions().override(256).transform(CenterCrop()).transform(FitCenter())
    }
}