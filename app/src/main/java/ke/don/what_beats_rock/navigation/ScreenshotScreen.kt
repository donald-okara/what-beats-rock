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
    val screenModel: SharableScreenModel
): Screen {
    @Composable
    override fun Content() {
        val viewModel: SharableViewModel = hiltViewModel()
        val handleIntent = viewModel::handleIntent
        val state by viewModel.uiState.collectAsState()


        ScreenshotScreenContent(
            handleIntent = handleIntent,
            state = state,
            screenModel = screenModel
        )
    }
}