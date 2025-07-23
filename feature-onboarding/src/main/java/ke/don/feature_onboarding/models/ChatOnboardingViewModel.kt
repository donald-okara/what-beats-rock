package ke.don.feature_onboarding.models

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import ke.don.core_designsystem.material_theme.components.TypingDots
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatOnboardingViewModel @Inject constructor() : ViewModel() {
    private val _uiState = MutableStateFlow(OnBoardingUiState())
    val uiState: StateFlow<OnBoardingUiState> get() = _uiState

    private var steps: List<OnboardingStep> = emptyList()

    fun handleIntent(intent: OnBoardingIntentHandler) {
        when (intent) {
            is OnBoardingIntentHandler.Start -> {
                steps = intent.steps
                start()
            }
            is OnBoardingIntentHandler.ShowNextStep -> showNextStep()
            is OnBoardingIntentHandler.SkipToLast -> skipToFinal()
        }
    }

    private fun start() {
        viewModelScope.launch {
            steps.forEachIndexed { index, step ->
                if (shouldShowTypingDotsBefore(step)) {
                    showTypingDots()
                    delay(2000)
                }

                delay(step.delayMillis)
                showStep(step, index)
            }
        }
    }

    fun showNextStep() {
        val step = steps.getOrNull(_uiState.value.currentStep) ?: return

        showStep(step, _uiState.value.currentStep + 1)

        if (!step.isFinal) {
            viewModelScope.launch {
                delay(2000)
                showTypingDots()
            }
        }
    }

    fun skipToFinal() {
        updateState {
            it.copy(
                visibleSteps = listOf(steps.last()),
                skipRequested = true,
                currentStep = steps.size
            )
        }
    }

    private fun shouldShowTypingDotsBefore(step: OnboardingStep): Boolean {
        return !step.isFinal && _uiState.value.visibleSteps.lastOrNull()?.isTypingIndicator != true
    }

    private fun showTypingDots() {
        val dotsStep = OnboardingStep(
            delayMillis = 0,
            isTypingIndicator = true,
        )
        updateState {
            it.copy(visibleSteps = it.visibleSteps + dotsStep)
        }
    }

    private fun showStep(step: OnboardingStep, newIndex: Int) {
        val withoutDots = _uiState.value.visibleSteps.filterNot { it.isTypingIndicator }
        updateState {
            it.copy(
                visibleSteps = withoutDots + step,
                currentStep = newIndex
            )
        }
    }

    private fun updateState(transform: (OnBoardingUiState) -> OnBoardingUiState) {
        _uiState.update(transform)
    }

}
