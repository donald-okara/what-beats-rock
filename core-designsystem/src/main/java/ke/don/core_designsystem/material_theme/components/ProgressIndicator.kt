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
package ke.don.core_designsystem.material_theme.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
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
    logo: Painter = painterResource(id = R.drawable.rock_svgrepo_com),
) {
    Box(
        modifier = modifier
            .aspectRatio(1f) // force equal width and height
            .size((64 * scale).dp),
        contentAlignment = Alignment.Center,
    ) {
        // Outer layer ensures a square boundary
        Box(
            modifier = Modifier
                .fillMaxSize(),
            contentAlignment = Alignment.Center,
        ) {
            CircularProgressIndicator(
                strokeWidth = (4 * scale).dp,
                modifier = Modifier.fillMaxSize(),
            )
            Image(
                painter = logo,
                contentDescription = "App Logo",
                modifier = Modifier.size((32 * scale).dp),
            )
        }
    }
}

@Preview
@Composable
fun StarLoadingPreview(
    @PreviewParameter(ThemeModeProvider::class) isDark: Boolean,
) {
    ThemedPreviewTemplate(isDark) {
        StarLoadingIndicator(
            scale = 3f,
        )
    }
}
