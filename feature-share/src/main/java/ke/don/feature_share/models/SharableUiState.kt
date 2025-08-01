/*
 * Copyright © 2025 Donald O. Isoe (isoedonald@gmail.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package ke.don.feature_share.models

import android.net.Uri
import ke.don.core_designsystem.material_theme.WhatBeatsIcons

data class SharableUiState(
    val imageUri: Uri? = null,
    val isLoading: Boolean = false,
)

enum class Channel(val icon: Int? = null, val text: String = "") {
    Whatsapp(WhatBeatsIcons.whatsApp),
    Instagram(WhatBeatsIcons.instagram),
    Twitter(WhatBeatsIcons.twitter),
    More
}