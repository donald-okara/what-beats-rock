package ke.don.feature_onboarding.models

import java.util.UUID

data class OnBoardingUiState(
    val visibleSteps: List<OnboardingStep> = emptyList(),
    val skipRequested: Boolean = false,
    val currentStep: Int = 0
)

data class OnboardingStep(
    val id: String = UUID.randomUUID().toString(),
    val delayMillis: Long = 1000L,
    val isFinal: Boolean = false,
    val isTypingIndicator: Boolean = false,
    val fullText: String = "",
    val currentWordIndex: Int = -1 // -1 means not started
){
    fun render(): String {
        val words = fullText.split(" ")
        return if (currentWordIndex in words.indices) {
            words.take(currentWordIndex + 1).joinToString(" ")
        } else {
            fullText // show full
        }
    }

}


