package ke.don.feature_onboarding.models

sealed class OnBoardingIntentHandler {
    data class Start(val steps: List<OnboardingStep>) : OnBoardingIntentHandler()
    data object ShowNextStep : OnBoardingIntentHandler()
    data object SkipToLast : OnBoardingIntentHandler()

}