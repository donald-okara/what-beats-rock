package ke.don.core_designsystem.material_theme.components.leaderboard

import androidx.compose.ui.graphics.Color

enum class CrownColor { GOLD, SILVER, BRONZE, BLACK }

fun CrownColor.toColor(): Color = when (this) {
    CrownColor.GOLD -> Color(0xFFFFD700)
    CrownColor.SILVER -> Color(0xFFC0C0C0)
    CrownColor.BRONZE -> Color(0xFFCD7F32)
    CrownColor.BLACK-> Color.Black
}

fun CrownColor.scale(): Float = when(this){
    CrownColor.GOLD -> 1.2f
    CrownColor.SILVER -> 1.0f
    CrownColor.BRONZE -> 0.8f
    CrownColor.BLACK -> 0.8f
}

fun CrownColor.rank(): Int = when(this){
    CrownColor.GOLD -> 1
    CrownColor.SILVER -> 2
    CrownColor.BRONZE -> 3
    CrownColor.BLACK -> 0
}
