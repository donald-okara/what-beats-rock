package ke.don.what_beats_rock.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.MoreVert
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
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
import ke.don.feature_chat.models.ChatIntentHandler
import ke.don.feature_chat.models.ChatViewModel
import ke.don.feature_chat.screens.ChatScreenContent
import ke.don.feature_profile.model.ProfileIntentHandler

class ChatScreen : Screen {
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {

        val viewModel: ChatViewModel = hiltViewModel()
        val state by viewModel.uiState.collectAsState()
        val handleIntent = viewModel::handleIntent
        val navigator = LocalNavigator.current

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
                    }
                )
            },
        ) { innerPadding ->
            ChatScreenContent(
                modifier = Modifier.padding(innerPadding),
                uiState = state,
                handleIntent = handleIntent,
            )
        }

    }
}