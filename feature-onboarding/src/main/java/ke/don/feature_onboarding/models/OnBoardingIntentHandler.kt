package ke.don.feature_onboarding.models

sealed class OnBoardingIntentHandler {
    data class Start(val steps: List<OnboardingStep>) : OnBoardingIntentHandler()
    data class ShowNextStep(val steps: List<OnboardingStep>) : OnBoardingIntentHandler()
    data class SkipToLast(val steps: List<OnboardingStep>) : OnBoardingIntentHandler()

}