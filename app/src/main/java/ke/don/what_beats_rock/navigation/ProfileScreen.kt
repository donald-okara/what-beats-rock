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

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.outlined.MoreVert
import androidx.compose.material.icons.outlined.Warning
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import ke.don.core_designsystem.material_theme.components.EmptyScreen
import ke.don.core_designsystem.material_theme.components.SnackManager
import ke.don.feature_profile.model.ProfileIntentHandler
import ke.don.feature_profile.model.ProfileViewModel
import ke.don.feature_profile.screens.ProfileScreenContent
import ke.don.feature_share.models.SharableScreenModel

class ProfileScreen(
    private val id: String? = null,
) : Screen {
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        val viewModel: ProfileViewModel = hiltViewModel()
        val uiState by viewModel.uiState.collectAsState()
        val handleIntent = viewModel::handleIntent
        val navigator = LocalNavigator.current
        val snackbarHostState = remember { SnackbarHostState() }

        LaunchedEffect(Unit) {
            viewModel.eventFlow.collect { event ->
                when (event) {
                    is SnackManager.ShowSnackbar -> {
                        snackbarHostState.showSnackbar(message = event.message, withDismissAction = true)
                    }
                }
            }
        }

        LaunchedEffect(viewModel) {
            if (id == null) {
                handleIntent(ProfileIntentHandler.FetchMyProfile)
            } else {
                handleIntent(ProfileIntentHandler.FetchProfile(id))
            }
        }

        DisposableEffect(Unit) {
            onDispose {
                handleIntent(ProfileIntentHandler.ClearState)
            }
        }

        Scaffold(
            snackbarHost = { SnackbarHost(snackbarHostState) },
            topBar = {
                TopAppBar(
                    title = {
                        Text("Profile")
                    },
                    navigationIcon = {
                        IconButton(
                            onClick = { navigator?.pop() },
                        ) {
                            Icon(
                                Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Go back",
                            )
                        }
                    },
                    actions = {
                        IconButton(
                            onClick = { handleIntent(ProfileIntentHandler.ToggleBottomSheet) },
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.MoreVert,
                                contentDescription = "More",
                            )
                        }
                    },
                )
            },
        ) { innerPadding ->
            AnimatedContent(
                targetState = uiState.isError,
                label = "LoadingTransition",
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize(),
                transitionSpec = {
                    fadeIn(tween(300)) togetherWith fadeOut(tween(300))
                },
            ) { isError ->
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .fillMaxSize(),
                ) {
                    if (isError) {
                        EmptyScreen(
                            icon = Icons.Outlined.Warning,
                            title = "Something went wrong",
                            message = uiState.errorMessage ?: "Unknown error",
                            showRetry = true,
                            onRetry = { handleIntent(ProfileIntentHandler.FetchMyProfile) },
                        )
                    } else {
                        ProfileScreenContent(
                            uiState = uiState,
                            navigateToShare = {
                                navigator?.push(ScreenshotScreen(SharableScreenModel.Profile(uiState.profile)))
                            },
                            intentHandler = handleIntent,
                            navigateToSignin = { navigator?.replaceAll(OnboardingScreen()) },
                        )
                    }
                }
            }
        }
    }
}
