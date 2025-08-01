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

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import ke.don.core_datasource.domain.models.SpotlightModel
import ke.don.feature_chat.models.ChatIntentHandler
import ke.don.feature_chat.models.ChatViewModel
import ke.don.feature_chat.screens.ChatScreenContent
import ke.don.feature_share.models.SharableScreenModel

class ChatScreen : Screen {
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        val viewModel: ChatViewModel = hiltViewModel()
        val state by viewModel.uiState.collectAsState()
        val handleIntent = viewModel::handleIntent
        val navigator = LocalNavigator.current

        val spotlightModel = SpotlightModel(
            profileUrl = state.profile.photoUrl,
            spotlightPair = state.spotlightPair,
        )

        LaunchedEffect(viewModel) {
            handleIntent(ChatIntentHandler.FetchSession)
        }
        DisposableEffect(Unit) {
            onDispose {
                handleIntent(ChatIntentHandler.ResetState)
            }
        }

        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text("Score: ${state.score}")
                    },
                    actions = {
                    },
                    navigationIcon = {
                        IconButton(
                            onClick = { navigator?.pop() },
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                                contentDescription = "More",
                            )
                        }
                    },
                )
            },
        ) { innerPadding ->
            ChatScreenContent(
                modifier = Modifier.padding(innerPadding),
                uiState = state,
                handleIntent = handleIntent,
                navigateToShare = { navigator?.push(ScreenshotScreen(SharableScreenModel.GameSpotlight(spotlightModel))) },
            )
        }
    }
}
