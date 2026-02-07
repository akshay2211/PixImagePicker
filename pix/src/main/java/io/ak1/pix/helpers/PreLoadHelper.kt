/*
 * Copyright (C) 2026 Akshay Sharma
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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

internal fun Context.preLoader(adapter: MainImageAdapter): RecyclerViewPreloader<Img> = RecyclerViewPreloader(
    Glide.with(this),
    adapter,
    adapter.sizeProvider,
    30 /*maxPreload*/
)
