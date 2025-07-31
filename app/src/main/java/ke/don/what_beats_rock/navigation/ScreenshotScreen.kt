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
package ke.don.what_beats_rock.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import cafe.adriel.voyager.core.screen.Screen
import ke.don.feature_share.models.SharableScreenModel
import ke.don.feature_share.models.SharableViewModel
import ke.don.feature_share.screens.ScreenshotScreenContent

class ScreenshotScreen(
    val screenModel: SharableScreenModel,
) : Screen {
    @Composable
    override fun Content() {
        val viewModel: SharableViewModel = hiltViewModel()
        val handleIntent = viewModel::handleIntent
        val state by viewModel.uiState.collectAsState()

        ScreenshotScreenContent(
            handleIntent = handleIntent,
            state = state,
            screenModel = screenModel,
        )
    }
}
