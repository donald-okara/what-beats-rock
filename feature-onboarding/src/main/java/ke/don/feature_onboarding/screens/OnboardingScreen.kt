package ke.don.feature_onboarding.screens

import android.app.Activity
import androidx.activity.compose.LocalActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import cafe.adriel.voyager.core.screen.Screen
import ke.don.feature_onboarding.components.ChatOnboardingList
import ke.don.feature_onboarding.models.ChatOnboardingViewModel
import ke.don.feature_onboarding.models.OnBoardingIntentHandler
import ke.don.feature_onboarding.models.onboardingSteps

class OnboardingScreen: Screen{
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        val viewModel: ChatOnboardingViewModel = hiltViewModel()
        val uiState by viewModel.uiState.collectAsState()
        val handleIntent = viewModel::handleIntent
        val activity = LocalActivity.current

        LaunchedEffect(viewModel, activity) {
            if(activity != null){
                handleIntent(OnBoardingIntentHandler.Start(onboardingSteps))
                handleIntent(OnBoardingIntentHandler.FetchActivity(activity))
            }
        }

        val launcher = rememberLauncherForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                viewModel.handleActivityResult(result.data)
            } else {
                viewModel.handleActivityResult(null)
            }
        }

        Scaffold(
            modifier = Modifier.fillMaxSize()
        ) { innerPadding ->
            ChatOnboardingList(
                modifier = Modifier.padding(innerPadding),
                uiState = uiState,
                handleIntent = handleIntent,
                launcher = launcher
            )
        }


    }
}
