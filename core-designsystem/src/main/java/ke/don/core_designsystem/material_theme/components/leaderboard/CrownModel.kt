package ke.don.core_designsystem.material_theme.components.leaderboard

import androidx.compose.ui.graphics.Color

enum class CrownColor { GOLD, SILVER, BRONZE }

fun CrownColor.toColor(): Color = when (this) {
    CrownColor.GOLD -> Color(0xFFFFD700)
    CrownColor.SILVER -> Color(0xFFC0C0C0)
    CrownColor.BRONZE -> Color(0xFFCD7F32)
}
