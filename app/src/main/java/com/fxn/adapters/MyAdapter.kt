package com.fxn.adapters

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.media.ThumbnailUtils
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Size
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory
import androidx.recyclerview.widget.RecyclerView
import com.fxn.pixsample.R
import java.io.File
import java.io.IOException
import java.nio.file.Files
import java.util.*

class MyAdapter(private val context: Context) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val list = ArrayList<String>()

    fun addImage(list: List<String>) {
        this.list.clear()
        this.list.addAll(list)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.image, parent, false)
        return Holder(v)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        //Uri imageUri = Uri.fromFile(new File(list.get(position)));// For files on device
        val f = File(list[position])
        var bitmap: Bitmap? = null
        if (f.absolutePath.endsWith("mp4")) {
            (holder as Holder).play.visibility = View.VISIBLE
            bitmap = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                ThumbnailUtils.createVideoThumbnail(f, Size(500, 500), null)
            } else {
                ThumbnailUtils.createVideoThumbnail(f.absolutePath, MediaStore.Video.Thumbnails.MINI_KIND)
            }
        } else {
            (holder as Holder).play.visibility = View.GONE
        }
        val roundedBitmapDrawable = RoundedBitmapDrawableFactory.create(context.resources, bitmap)
        val roundPx = bitmap!!.width.toFloat() * 0.06f
        roundedBitmapDrawable.cornerRadius = roundPx
        holder.iv.setImageDrawable(roundedBitmapDrawable)
        holder.iv.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(f.absolutePath))
            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    intent.setDataAndType(Uri.parse(f.absolutePath), Files.probeContentType(f.toPath()))
                } else {
                    intent.data = Uri.parse(f.absolutePath)
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
            context.startActivity(intent)
        }
        /*Bitmap scaled = com.fxn.utility.Utility.getScaledBitmap(
            500f, com.fxn.utility.Utility.rotate(d,list.get(position).getOrientation()));*/
    }

    override fun getItemCount() = list.size

    inner class Holder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var iv: ImageView = itemView.findViewById(R.id.iv)
        var play: ImageView = itemView.findViewById(R.id.play)
    }
}