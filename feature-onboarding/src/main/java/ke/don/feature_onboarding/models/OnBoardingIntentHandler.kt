package ke.don.feature_onboarding.models

import android.app.Activity
import android.content.Intent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest

sealed class OnBoardingIntentHandler {
    data class FetchActivity(val activity: Activity) : OnBoardingIntentHandler()
    data class Start(val steps: List<OnboardingStep>) : OnBoardingIntentHandler()
    data class LaunchSignIn(val launcher: ActivityResultLauncher<IntentSenderRequest>?) : OnBoardingIntentHandler()
    data class HandleActivityResult(val intent: Intent?, val navigateToMain:() -> Unit) : OnBoardingIntentHandler()
    data object ShowNextStep : OnBoardingIntentHandler()
    data object SkipToLast : OnBoardingIntentHandler()
    data object NavigateToMain : OnBoardingIntentHandler()

}