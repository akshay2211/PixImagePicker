package io.ak1.pix.adapters

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.FitCenter
import com.bumptech.glide.request.RequestOptions
import io.ak1.pix.databinding.InitalImageBinding
import io.ak1.pix.helpers.hide
import io.ak1.pix.helpers.show
import io.ak1.pix.helpers.toPx
import io.ak1.pix.interfaces.OnSelectionListener
import io.ak1.pix.models.Img
import java.util.*

/**
 * Created By Akshay Sharma on 17,June,2021
 * https://ak1.io
 */


class InstantImageAdapter(context: Context) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    val itemList: ArrayList<Img> = ArrayList()
    private var onSelectionListener: OnSelectionListener? = null
    private val glide: RequestManager = Glide.with(context)
    private val options: RequestOptions =
        RequestOptions().override(256).transform(CenterCrop()).transform(FitCenter())
    private val size: Float = context.toPx(72f) - 2
    private val margin = 3
    private val padding: Int = (size / 3.5).toInt()
    fun addOnSelectionListener(onSelectionListener: OnSelectionListener?) {
        this.onSelectionListener = onSelectionListener
    }

    fun addImage(image: Img): InstantImageAdapter {
        itemList.add(0, image)
        notifyDataSetChanged()
        return this
    }

    fun unselected(position: Int) {
        itemList[position].selected = false
        notifyItemChanged(position)
    }

    fun addImageList(images: ArrayList<Img>) {
        itemList.addAll(images)
        notifyDataSetChanged()
    }

    fun clearList() {
        itemList.clear()
    }

    fun select(selection: Boolean, pos: Int) {
        if (pos < 100) {
            itemList[pos].selected = (selection)
            notifyItemChanged(pos)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        if (viewType == MainImageAdapter.HEADER) {
            HolderNone(
                InitalImageBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
        } else {
            Holder(
                InitalImageBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
        }

    override fun getItemCount() = itemList.size

    override fun getItemViewType(position: Int) =
        if (itemList[position].contentUrl == Uri.EMPTY) MainImageAdapter.HEADER else MainImageAdapter.ITEM

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) =
        if (holder is Holder) {
            holder.bind()
        } else {
            (holder as HolderNone).bind()
        }


    inner class Holder(private val initialImageBinding: InitalImageBinding) :
        RecyclerView.ViewHolder(initialImageBinding.root),
        View.OnClickListener, View.OnLongClickListener {
        fun bind() {
            val image = itemList[adapterPosition]
            val layoutParams = FrameLayout.LayoutParams(size.toInt(), size.toInt())
            if (adapterPosition == 0) {
                layoutParams.setMargins(-(margin / 2), margin, margin, margin)
            } else {
                layoutParams.setMargins(margin, margin, margin, margin)
            }
            itemView.layoutParams = layoutParams
            initialImageBinding.selection.setPadding(padding, padding, padding, padding)
            initialImageBinding.preview.layoutParams = layoutParams
            glide.asBitmap()
                .load(image.contentUrl)
                .apply(options)
                .into(initialImageBinding.preview)
            if (image.mediaType == 1) {
                initialImageBinding.isVideo.hide()
            } else if (image.mediaType == 3) {
                initialImageBinding.isVideo.show()
            }
            initialImageBinding.selection.visibility =
                if (image.selected) View.VISIBLE else View.GONE
        }

        override fun onClick(view: View) {
            val id = this.layoutPosition
            onSelectionListener?.onClick(itemList[id], view, id)
        }

        override fun onLongClick(view: View): Boolean {
            val id = this.layoutPosition
            onSelectionListener?.onLongClick(itemList[id], view, id)
            return true
        }

        init {
            itemView.setOnClickListener(this)
            itemView.setOnLongClickListener(this)
        }
    }

    inner class HolderNone(private val initialImageBinding: InitalImageBinding) :
        RecyclerView.ViewHolder(initialImageBinding.root) {
        fun bind() {
            initialImageBinding.frameLayout.apply {
                layoutParams = FrameLayout.LayoutParams(0, 0)
                hide()
            }
        }
    }

}