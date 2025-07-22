package ke.don.core_designsystem.material_theme.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.PreviewParameterProvider

class ThemeModeProvider : PreviewParameterProvider<Boolean> {
    override val values: Sequence<Boolean> = sequenceOf(false, true) // false = light, true = dark
}

@Composable
fun ThemedPreviewTemplate(
    isDarkTheme: Boolean = false,
    content: @Composable () -> Unit,
) {
    AppTheme(isDarkTheme) {
        Surface(
            color = MaterialTheme.colorScheme.surface,
        ) {
            content()
        }
    }
}