package io.ak1.pix.helpers

import android.content.Context
import com.bumptech.glide.Glide
import com.bumptech.glide.integration.recyclerview.RecyclerViewPreloader
import io.ak1.pix.adapters.MainImageAdapter
import io.ak1.pix.models.Img

/**
 * Created By Akshay Sharma on 17,June,2021
 * https://ak1.io
 */

internal fun Context.preLoader(adapter: MainImageAdapter): RecyclerViewPreloader<Img> =
    RecyclerViewPreloader(
        Glide.with(this), adapter, adapter.sizeProvider, 30 /*maxPreload*/
    )