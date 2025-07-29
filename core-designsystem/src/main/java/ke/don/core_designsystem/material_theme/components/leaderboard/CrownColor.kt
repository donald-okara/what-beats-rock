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
package ke.don.core_designsystem.material_theme.components.leaderboard

import androidx.compose.ui.graphics.Color

enum class CrownColor { GOLD, SILVER, BRONZE, BLACK }

fun CrownColor.toColor(): Color = when (this) {
    CrownColor.GOLD -> Color(0xFFFFD700)
    CrownColor.SILVER -> Color(0xFFC0C0C0)
    CrownColor.BRONZE -> Color(0xFFCD7F32)
    CrownColor.BLACK -> Color.Black
}

fun CrownColor.scale(): Float = when (this) {
    CrownColor.GOLD -> 1.2f
    CrownColor.SILVER -> 1.0f
    CrownColor.BRONZE -> 0.8f
    CrownColor.BLACK -> 0.8f
}

fun CrownColor.rank(): Int = when (this) {
    CrownColor.GOLD -> 1
    CrownColor.SILVER -> 2
    CrownColor.BRONZE -> 3
    CrownColor.BLACK -> 0
}
