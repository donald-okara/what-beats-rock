package ke.don.core_designsystem.material_theme.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import ke.don.core_designsystem.R
import ke.don.core_designsystem.material_theme.ui.theme.ThemeModeProvider
import ke.don.core_designsystem.material_theme.ui.theme.ThemedPreviewTemplate

@Composable
fun StarLoadingIndicator(
    modifier: Modifier = Modifier,
    scale: Float = 1f,
    logo: Painter = painterResource(id = R.drawable.rock_svgrepo_com)
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .size((64 * scale).dp)
    ) {
        CircularProgressIndicator(
            modifier = Modifier.fillMaxSize(),
            strokeWidth = (4 * scale).dp
        )
        Image(
            painter = logo,
            contentDescription = "App Logo",
            modifier = Modifier.size((32 * scale).dp)
        )
    }
}


@Preview
@Composable
fun StarLoadingPreview(
    @PreviewParameter(ThemeModeProvider::class) isDark: Boolean
){
    ThemedPreviewTemplate(isDark) {
        StarLoadingIndicator(
            scale = 3f
        )
    }
}
