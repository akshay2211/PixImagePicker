package io.ak1.pix.adapters

import android.content.Context
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.ListPreloader
import com.bumptech.glide.RequestBuilder
import com.bumptech.glide.RequestManager
import com.bumptech.glide.load.DecodeFormat
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.util.FixedPreloadSizeProvider
import io.ak1.pix.R
import io.ak1.pix.databinding.HeaderRowBinding
import io.ak1.pix.databinding.MainImageBinding

import io.ak1.pix.helpers.hide
import io.ak1.pix.helpers.show
import io.ak1.pix.interfaces.OnSelectionListener
import io.ak1.pix.interfaces.SectionIndexer
import io.ak1.pix.interfaces.StickyHeaderInterface
import io.ak1.pix.models.Img
import io.ak1.pix.utility.WIDTH
import java.util.*

/**
 * Created By Akshay Sharma on 17,June,2021
 * https://ak1.io
 */

internal class MainImageAdapter(context: Context, internal val spanCount: Int) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>(),
    StickyHeaderInterface,
    SectionIndexer, ListPreloader.PreloadModelProvider<Img> {

    val itemList: ArrayList<Img> = ArrayList()
    private var onSelectionListener: OnSelectionListener? = null
    private val layoutParams: FrameLayout.LayoutParams
    private val glide: RequestManager
    private val options: RequestOptions
    internal var sizeProvider: ListPreloader.PreloadSizeProvider<Img>

    init {
        val size: Int = WIDTH / spanCount - MARGIN / 2
        layoutParams = FrameLayout.LayoutParams(size, size)
        layoutParams.setMargins(MARGIN, MARGIN - MARGIN / 2, MARGIN, MARGIN - MARGIN / 2)
        options = RequestOptions().override(size - 50)
            .format(DecodeFormat.PREFER_RGB_565)
            .centerCrop()
            .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
        glide = Glide.with(context)
        sizeProvider = FixedPreloadSizeProvider(size, size)
    }

    fun addOnSelectionListener(onSelectionListener: OnSelectionListener?) {
        this.onSelectionListener = onSelectionListener
    }

    fun addImageList(images: ArrayList<Img>) {
        itemList.addAll(images)
        notifyDataSetChanged()
    }

    override fun getItemViewType(position: Int): Int {
        if (itemList.size <= position) {
            return 0
        }
        val i = itemList[position]
        return if (i.contentUrl == Uri.EMPTY) HEADER else ITEM
    }

    fun clearList() {
        itemList.clear()
    }

    fun select(isSelected: Boolean, pos: Int) {
        itemList[pos].selected = isSelected
        Log.e("position ", "updating selection to $isSelected at $pos")
        notifyItemChanged(pos)
    }

    override fun getItemId(position: Int): Long {
        return itemList[position].contentUrl.hashCode().toLong()
    }

    // TODO: 18/06/21 first header blinks on image selection need to be resolved
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val layoutManager = LayoutInflater.from(parent.context)
        return if (viewType == HEADER)
            HeaderHolder(HeaderRowBinding.inflate(layoutManager, parent, false))
        else
            Holder(MainImageBinding.inflate(layoutManager, parent, false))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val image = itemList[position]
        if (holder is Holder) holder.bind(image)
        else if (holder is HeaderHolder) {
            holder.bind(image.headerDate)
            //Log.e("header date", "${image.headerDate} ${image.mediaType}")
        }
    }

    override fun getItemCount() = itemList.size

    override fun getHeaderPositionForItem(position: Int): Int {
        var tempPosition = position
        var headerPosition = 0
        do {
            if (isHeader(tempPosition)) {
                headerPosition = tempPosition
                break
            }
            tempPosition -= 1
        } while (tempPosition >= 0)
        return headerPosition
    }

    override fun getHeaderLayout(headerPosition: Int) = R.layout.header_row


    override fun bindHeaderData(header: View, headerPosition: Int) {
        val image = itemList[headerPosition]
        (header.findViewById<View>(R.id.header) as TextView).text = image.headerDate
    }

    override fun isHeader(itemPosition: Int) = getItemViewType(itemPosition) == 1


    override fun getSectionText(position: Int) = itemList[position].headerDate


    fun getSectionMonthYearText(position: Int) =
        if (itemList.size <= position) "" else itemList[position].headerDate


    inner class Holder(private val mainImageBinding: MainImageBinding) :
        RecyclerView.ViewHolder(mainImageBinding.root),
        View.OnClickListener, View.OnLongClickListener {
        override fun onClick(view: View) {
            val id = this.layoutPosition
            onSelectionListener!!.onClick(itemList[id], view, id)
        }

        override fun onLongClick(view: View): Boolean {
            val id = this.layoutPosition
            onSelectionListener!!.onLongClick(itemList[id], view, id)
            return true
        }

        fun bind(image: Img) {
            mainImageBinding.root.setOnClickListener(this)
            mainImageBinding.root.setOnLongClickListener(this)
            mainImageBinding.preview.layoutParams = layoutParams
            glide.asBitmap()
                .load(image.contentUrl)
                .apply(options)
                .into(mainImageBinding.preview)
            if (image.mediaType == 1) {
                mainImageBinding.isVideo.hide()
            } else if (image.mediaType == 3) {
                mainImageBinding.isVideo.show()
            }
            mainImageBinding.selection.apply {
                if (image.selected) show() else hide()
            }
        }
    }

    inner class HeaderHolder(private val headerRowBinding: HeaderRowBinding) :
        RecyclerView.ViewHolder(headerRowBinding.root) {
        fun bind(headerDate: String) {
            headerRowBinding.header.text = headerDate
        }
    }

    companion object {
        const val HEADER = 1
        const val ITEM = 2
        private const val MARGIN = 4
    }


    override fun getPreloadItems(position: Int): MutableList<Img> {
        return itemList.subList(position, position + 1)
    }

    override fun getPreloadRequestBuilder(image: Img): RequestBuilder<*> {
        // Log.e("image", "getPreloadRequestBuilder " + image.url + "   " + image.mediaType)
        return when (image.mediaType) {
            1 -> glide.load(image.contentUrl).apply(options)
            3 -> glide.asBitmap()
                .load(image.contentUrl)
                .apply(options)

            else -> glide.load(image.contentUrl).apply(options)
        }
    }
}