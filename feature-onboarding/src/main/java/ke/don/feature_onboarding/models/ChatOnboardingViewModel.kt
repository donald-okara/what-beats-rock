package ke.don.feature_onboarding.models

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import ke.don.core_designsystem.material_theme.components.TypingDots
import kotlinx.coroutines.Job
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

    private var onboardingJob: Job? = null
    private var currentAutoStepIndex: Int = 0
    private var stepList: List<OnboardingStep> = emptyList()

    fun handleIntent(intent: OnBoardingIntentHandler) {
        when (intent) {
            is OnBoardingIntentHandler.Start -> {
                Log.d("ChatOnboardingViewModel", "Starting onboarding with ${intent.steps.size} steps")
                start(intent.steps)
            }
            is OnBoardingIntentHandler.ShowNextStep -> showNextStep()
            is OnBoardingIntentHandler.SkipToLast -> skipToFinal()
        }
    }

    private fun start(initialSteps: List<OnboardingStep>) {
        onboardingJob?.cancel()
        stepList = initialSteps
        currentAutoStepIndex = 0

        onboardingJob = viewModelScope.launch {
            updateState { it.copy(visibleSteps = emptyList()) }
            runStepSequence()
        }
    }

    private suspend fun runStepSequence() {
        while (currentAutoStepIndex < stepList.size) {
            val step = stepList[currentAutoStepIndex]
            if (shouldShowTypingBefore(step)) simulateTyping()
            delay(step.delayMillis)

            displayStep(step, currentAutoStepIndex + 1)
            currentAutoStepIndex++
        }
    }

    fun showNextStep() {
        onboardingJob?.cancel() // Pause the flow

        val current = _uiState.value.currentStep
        val allSteps = stepList.filterNot { it.isTypingIndicator }
        val step = allSteps.getOrNull(current) ?: return

        displayStep(step, current + 1)
        currentAutoStepIndex = stepList.indexOfFirst { it.id == step.id } + 1

        onboardingJob = viewModelScope.launch {
            runStepSequence() // Resume flow
        }
    }

    fun skipToFinal() {
        if (_uiState.value.skipRequested) return

        onboardingJob?.cancel() // Cancel the ongoing auto-sequence

        val allSteps = stepList.filterNot { it.isTypingIndicator }
        if (allSteps.isEmpty()) return

        val finalIndex = allSteps.lastIndex
        val finalSteps = allSteps.mapIndexed { index, step ->
            if (index == finalIndex) step.copy(isFinal = true) else step
        }

        currentAutoStepIndex = stepList.size // Ensure auto job doesn't resume

        updateState {
            it.copy(
                visibleSteps = finalSteps,
                skipRequested = true,
                currentStep = finalSteps.size
            )
        }
    }

    private suspend fun simulateTyping() {
        delay(500)
        addTypingIndicator()
        delay(2000)
    }

    private fun shouldShowTypingBefore(step: OnboardingStep): Boolean {
        val last = _uiState.value.visibleSteps.lastOrNull()
        return !step.isFinal && last?.isTypingIndicator != true
    }

    private fun addTypingIndicator() {
        val typingStep = OnboardingStep(delayMillis = 0, isTypingIndicator = true)
        updateState { it.copy(visibleSteps = it.visibleSteps + typingStep) }
    }

    private fun displayStep(step: OnboardingStep, nextIndex: Int) {
        updateState { state ->
            val cleanSteps = state.visibleSteps.filterNot { it.isTypingIndicator }
            state.copy(
                visibleSteps = cleanSteps + step,
                currentStep = nextIndex
            )
        }
    }

    private fun updateState(transform: (OnBoardingUiState) -> OnBoardingUiState) {
        _uiState.update(transform)
    }
}
