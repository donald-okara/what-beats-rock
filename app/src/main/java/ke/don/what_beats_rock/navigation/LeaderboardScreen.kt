package ke.don.what_beats_rock.navigation

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.MoreVert
import androidx.compose.material.icons.outlined.Warning
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.google.firebase.auth.FirebaseAuth
import ke.don.core_designsystem.material_theme.components.EmptyScreen
import ke.don.core_designsystem.material_theme.components.Images
import ke.don.feature_leaderboard.models.LeaderboardIntentHandler
import ke.don.feature_leaderboard.models.LeaderboardViewModel
import ke.don.feature_leaderboard.screens.LeaderboardScreenContent
import ke.don.feature_profile.model.ProfileIntentHandler


@OptIn(ExperimentalMaterial3Api::class)
class LeaderboardScreen (): Screen {
    @Composable
    override fun Content() {
        val auth = FirebaseAuth.getInstance().currentUser
        val viewModel: LeaderboardViewModel = hiltViewModel()
        val state by viewModel.uiState.collectAsState()
        val handleIntent = viewModel::handleIntent

        val navigator = LocalNavigator.current

        fun handleIntentLocal(intent: LeaderboardIntentHandler){
            when (intent) {
                is LeaderboardIntentHandler.NavigateToChat -> {
                    navigator?.push(ChatScreen())
                }
                is LeaderboardIntentHandler.NavigateToProfile -> {
                    navigator?.push(ProfileScreen(intent.id))
                }
                is LeaderboardIntentHandler.NavigateMyProfile -> {
                    navigator?.push(ProfileScreen())
                }
                else -> handleIntent(intent)
            }
        }

        LaunchedEffect(viewModel) {
            handleIntent(LeaderboardIntentHandler.FetchLeaderboard)
        }
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text("Leaderboard")
                    },
                    actions = {
                        if (auth != null) {
                            IconButton(
                                onClick = { handleIntentLocal(LeaderboardIntentHandler.NavigateMyProfile) },
                            ) {
                                AsyncImage(
                                    model = ImageRequest.Builder(LocalContext.current)
                                        .data(auth.photoUrl)
                                        .crossfade(true)
                                        .build(),
                                    contentDescription = "Profile Photo",
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier
                                        .clip(CircleShape)
                                        .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f))
                                        .border(2.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.5f), CircleShape)
                                )
                            }
                        }
                    },
                )
            },
            floatingActionButton = {
                FloatingActionButton(
                    onClick = { handleIntentLocal(LeaderboardIntentHandler.NavigateToChat) },
                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                    shape = MaterialTheme.shapes.medium,
                    modifier = Modifier
                        .padding(16.dp) // keep padding outside the FAB
                ) {
                    Image(
                        painter = painterResource(Images.appLogo),
                        contentDescription = "Go to chat",
                        modifier = Modifier.size(48.dp) // recommended size for FAB icons
                    )
                }
            }
        ) { innerPadding ->
            PullToRefreshBox(
                contentAlignment = Alignment.Center,
                isRefreshing = state.isRefreshing,
                onRefresh = { handleIntent(LeaderboardIntentHandler.RefreshLeaderboard) },
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize(),
            ) {
                if (state.isError){
                    EmptyScreen(
                        icon = Icons.Outlined.Warning,
                        title = "Something went wrong",
                        message = state.errorMessage ?: "Unknown error",
                        showRetry = true,
                        onRetry = { handleIntentLocal(LeaderboardIntentHandler.FetchLeaderboard) },
                    )
                }else {
                    LeaderboardScreenContent(
                        uiState = state,
                        handleIntent = ::handleIntentLocal,
                    )
                }
            }
        }
    }

}