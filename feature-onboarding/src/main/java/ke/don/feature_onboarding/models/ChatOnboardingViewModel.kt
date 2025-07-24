package ke.don.feature_onboarding.models

import android.app.Activity
import android.content.Intent
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import androidx.compose.ui.text.AnnotatedString
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import ke.don.core_datasource.remote.auth.GoogleAuthClient
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
    private lateinit var authClient: GoogleAuthClient

    fun handleIntent(intent: OnBoardingIntentHandler) {
        when (intent) {
            is OnBoardingIntentHandler.FetchActivity -> fetchAuthClient(intent.activity)
            is OnBoardingIntentHandler.Start -> {
                Log.d("ChatOnboardingViewModel", "Starting onboarding with ${intent.steps.size} steps")
                start(intent.steps)
            }
            is OnBoardingIntentHandler.HandleActivityResult -> handleActivityResult(intent.intent, intent.navigateToMain)
            is OnBoardingIntentHandler.LaunchSignIn -> launchSignInAndHandleResult(intent.launcher)
            is OnBoardingIntentHandler.ShowNextStep -> showNextStep()
            is OnBoardingIntentHandler.SkipToLast -> skipToFinal()
            is OnBoardingIntentHandler.NavigateToMain -> {}
        }
    }

    fun fetchAuthClient(context: Activity){
        authClient = GoogleAuthClient(context)
    }

    fun launchSignInAndHandleResult(
        launcher: ActivityResultLauncher<IntentSenderRequest>?
    ) {
        viewModelScope.launch {
            updateState { it.copy(authUiState = AuthUiState.Loading) }

            val signInIntentResult = authClient.signIn()
            signInIntentResult.fold(
                onSuccess = { intentSender ->
                    launcher?.launch(intentSender)
                },
                onFailure = { throwable ->
                    val isCancelled = throwable.message?.contains("13:", ignoreCase = true) == true ||
                            throwable.message?.contains("cancelled", ignoreCase = true) == true

                    val newState = if (isCancelled) {
                        showFailureAndRestoreFinal("Seems like you cancelled sign in")

                        AuthUiState.Cancelled
                    } else {
                        showFailureAndRestoreFinal("Something went wrong, please try again")

                        AuthUiState.Error(throwable.message)
                    }

                    updateState { it.copy(authUiState = newState) }
                }
            )
        }
    }

    fun handleActivityResult(intent: Intent?, navigateToMain:() -> Unit) {
        viewModelScope.launch {
            if (intent == null) {
                showFailureAndRestoreFinal("You dismissed the sign-in dialog")
                updateState { it.copy(authUiState = AuthUiState.Cancelled) }
                return@launch
            }

            val result = authClient.handleSignInResult(intent)

            result.fold(
                onSuccess = { user ->
                    showSuccessAndRestoreFinal()
                    updateState { it.copy(authUiState = AuthUiState.Success(user)) }
                    navigateToMain()
                },
                onFailure = { throwable ->
                    val isCancelled = throwable.message?.contains("13:", ignoreCase = true) == true ||
                            throwable.message?.contains("cancelled", ignoreCase = true) == true

                    val newState = if (isCancelled) {
                        showFailureAndRestoreFinal("Seems like you cancelled sign in")
                        AuthUiState.Cancelled
                    } else {
                        showFailureAndRestoreFinal("Something went wrong, please try again")
                        AuthUiState.Error(throwable.message)
                    }

                    updateState { it.copy(authUiState = newState) }
                }
            )
        }
    }

    fun showSuccessAndRestoreFinal(message: String = "Welcome!") {
        onboardingJob?.cancel()

        val nonTypingSteps = stepList.filterNot { it.isTypingIndicator }
        if (nonTypingSteps.isEmpty()) return

        val stepsWithoutFinal = nonTypingSteps.filterNot { it.isFinal }

        val successStep = OnboardingStep(
            id = "success-${System.currentTimeMillis()}",
            fullText = AnnotatedString(message),
            isFinal = false
        )

        val updatedSteps = stepsWithoutFinal + successStep

        currentAutoStepIndex = updatedSteps.size // Prevent resumption

        updateState {
            it.copy(
                visibleSteps = updatedSteps,
                skipRequested = true,
                currentStep = updatedSteps.size
            )
        }
    }


    fun showFailureAndRestoreFinal(message: String) {
        onboardingJob?.cancel()

        val nonTypingSteps = stepList.filterNot { it.isTypingIndicator }
        if (nonTypingSteps.isEmpty()) return

        val stepsWithoutFinal = nonTypingSteps.filterNot { it.isFinal }

        val failureStep = OnboardingStep(
            isError = true,
            id = "error-${System.currentTimeMillis()}",
            fullText = AnnotatedString(message),
            isFinal = false
        )

        val finalStep = nonTypingSteps.lastOrNull { it.isFinal }
            ?: return // If there's no final step, we can't restore it

        val updatedSteps = stepsWithoutFinal + failureStep + finalStep

        currentAutoStepIndex = updatedSteps.size // Prevent resumption

        updateState {
            it.copy(
                visibleSteps = updatedSteps,
                skipRequested = true,
                currentStep = updatedSteps.size
            )
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


