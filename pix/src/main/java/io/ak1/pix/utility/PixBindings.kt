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
package io.ak1.pix.utility

import io.ak1.pix.databinding.ControlsLayoutBinding
import io.ak1.pix.databinding.FragmentPixBinding
import io.ak1.pix.databinding.GridLayoutBinding
import io.ak1.pix.databinding.PermissionsLayoutBinding
import io.ak1.pix.databinding.VideoCounterLayoutBinding

class PixBindings(
    val fragmentPix: FragmentPixBinding,
    val videoCounterLayout: VideoCounterLayoutBinding,
    val permissionsLayout: PermissionsLayoutBinding,
    val gridLayout: GridLayoutBinding,
    val controlsLayout: ControlsLayoutBinding
)
