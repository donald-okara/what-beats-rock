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
import androidx.compose.material.icons.outlined.MoreVert
import androidx.compose.material.icons.outlined.Warning
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import ke.don.core_designsystem.material_theme.components.EmptyScreen
import ke.don.core_designsystem.material_theme.components.SnackManager
import ke.don.core_designsystem.material_theme.components.StarLoadingIndicator
import ke.don.feature_profile.model.ProfileIntentHandler
import ke.don.feature_profile.model.ProfileUiState
import ke.don.feature_profile.model.ProfileViewModel
import ke.don.feature_profile.screens.ProfileScreenContent

class ProfileScreen() :Screen{
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
            handleIntent(ProfileIntentHandler.FetchMyProfile)
        }

        Scaffold(
            snackbarHost = { SnackbarHost(snackbarHostState) },
            topBar = {
                TopAppBar(
                    title = {
                        Text("Profile")
                    },
                    actions = {
                        if (uiState.isMyProfile)
                            IconButton(
                                onClick = { handleIntent(ProfileIntentHandler.ToggleBottomSheet) }
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.MoreVert,
                                    contentDescription = "More"
                                )
                            }
                    }
                )
            },
        ) { innerPadding ->
            AnimatedContent(
                targetState = uiState.isLoading,
                label = "LoadingTransition",
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize(),
                transitionSpec = {
                    fadeIn(tween(300)) togetherWith  fadeOut(tween(300))
                }
            ) { isLoading ->
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .fillMaxSize()
                ){
                    if (isLoading) {
                        StarLoadingIndicator(scale = 3f)
                    } else if (uiState.isError) {
                        EmptyScreen(
                            icon = Icons.Outlined.Warning,
                            title = "Something went wrong",
                            message = uiState.errorMessage ?: "Unknown error",
                            showRetry = true,
                            onRetry = { handleIntent(ProfileIntentHandler.FetchMyProfile) }
                        )
                    } else {
                        ProfileScreenContent(
                            uiState = uiState,
                            intentHandler = handleIntent,
                            navigateToSignin = {navigator?.replaceAll(OnboardingScreen())}
                        )
                    }
                }

            }


        }


    }
}

