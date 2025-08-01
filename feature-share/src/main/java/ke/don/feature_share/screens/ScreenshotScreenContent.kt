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
package ke.don.feature_share.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import ke.don.core_designsystem.material_theme.components.LoadingOverlay
import ke.don.feature_share.components.ShareFrameLayout
import ke.don.feature_share.models.SharableIntentHandler
import ke.don.feature_share.models.SharableScreenModel
import ke.don.feature_share.models.SharableUiState

@OptIn(ExperimentalPermissionsApi::class, ExperimentalMaterial3Api::class)
@Composable
fun ScreenshotScreenContent(
    modifier: Modifier = Modifier,
    screenModel: SharableScreenModel,
    handleIntent: (SharableIntentHandler) -> Unit,
    navigateBack: () -> Unit,
    state: SharableUiState,
) {
    val snackbarHostState = remember { SnackbarHostState() }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            CenterAlignedTopAppBar(
                title = {},
                navigationIcon = { IconButton({ navigateBack() }) { Icon(imageVector = Icons.AutoMirrored.Default.ArrowBack, contentDescription = "Back") } },
            )
        },
    ) { padding ->
        Box(
            modifier = modifier
                .fillMaxSize()
                .padding(padding),
        ) {
            ShareFrameLayout(
                state = state,
                handleIntent = handleIntent,
                snackbarHostState = snackbarHostState,
            ) {
                when (screenModel) {
                    is SharableScreenModel.Profile -> ShareProfileScreen(profile = screenModel.profile)
                    is SharableScreenModel.GameSpotlight -> ShareSpotlightScreen(spotlightModel = screenModel.spotlight)
                }

                if (state.isLoading) {
                    LoadingOverlay()
                }
            }
        }
    }
}
