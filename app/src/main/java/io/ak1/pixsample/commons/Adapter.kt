package io.ak1.pixsample.commons

import android.net.Uri
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import io.ak1.pix.utility.WIDTH

/**
 * Created By Akshay Sharma on 20,June,2021
 * https://ak1.io
 */

class Adapter : RecyclerView.Adapter<Adapter.ViewHolder>() {
    val list = ArrayList<Uri>()
    val options: RequestOptions =
        RequestOptions().override(350).transform(CenterCrop(), RoundedCorners(40))

    inner class ViewHolder(private val imageView: ImageView) :
        RecyclerView.ViewHolder(imageView) {
        fun bind() {
            imageView.apply {
                Glide.with(imageView.context).asBitmap()
                    .load(list[adapterPosition])
                    .apply(options)
                    .into(this)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ViewHolder(ImageView(parent.context).apply {
            scaleType = ImageView.ScaleType.CENTER_CROP
            layoutParams = RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                setMargins(10, 10, 10, 10)
                val size = (WIDTH / 3) - 20
                height = size
                width = size
            }
        })

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind()

    override fun getItemCount() = list.size
}