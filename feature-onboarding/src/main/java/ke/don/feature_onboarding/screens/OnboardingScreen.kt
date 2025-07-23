package ke.don.feature_onboarding.screens

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import cafe.adriel.voyager.core.screen.Screen
import ke.don.feature_onboarding.components.ChatOnboardingList
import ke.don.feature_onboarding.models.ChatOnboardingViewModel
import ke.don.feature_onboarding.models.OnBoardingIntentHandler
import ke.don.feature_onboarding.models.onboardingSteps

class OnboardingScreen: Screen{
    @Composable
    override fun Content() {
        val viewModel: ChatOnboardingViewModel = hiltViewModel()
        val uiState by viewModel.uiState.collectAsState()
        val handleIntent = viewModel::handleIntent

        LaunchedEffect(viewModel) {
            handleIntent(OnBoardingIntentHandler.Start(onboardingSteps))
        }

        ChatOnboardingList(
            uiState = uiState,
            handleIntent = handleIntent
        )
    }
}
