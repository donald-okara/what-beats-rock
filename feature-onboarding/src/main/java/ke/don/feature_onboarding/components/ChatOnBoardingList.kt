/*
 * Copyright © 2025 Donald O. Isoe (isoedonald@gmail.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package ke.don.feature_onboarding.components

import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import ke.don.core_designsystem.material_theme.components.ChatBubble
import ke.don.core_designsystem.material_theme.components.LoadingOverlay
import ke.don.core_designsystem.material_theme.components.TextBubble
import ke.don.core_designsystem.material_theme.components.TypingBubble
import ke.don.core_designsystem.material_theme.components.toRelativeTime
import ke.don.core_designsystem.material_theme.ui.theme.ThemeModeProvider
import ke.don.core_designsystem.material_theme.ui.theme.ThemedPreviewTemplate
import ke.don.feature_onboarding.models.AuthUiState
import ke.don.feature_onboarding.models.OnBoardingIntentHandler
import ke.don.feature_onboarding.models.OnBoardingUiState
import ke.don.feature_onboarding.models.OnboardingStep

@Composable
fun ChatOnboardingList(
    modifier: Modifier = Modifier,
    uiState: OnBoardingUiState,
    launcher: ActivityResultLauncher<IntentSenderRequest>?,
    handleIntent: (OnBoardingIntentHandler) -> Unit,
) {
    val visibleSteps = uiState.visibleSteps

    Box(modifier = modifier.fillMaxSize()) {
        val listState = rememberLazyListState()

        LaunchedEffect(visibleSteps.size) {
            listState.animateScrollToItem(visibleSteps.size)
        }

        LazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp, alignment = Alignment.Bottom),
        ) {
            items(visibleSteps, key = { it.id }) { step ->
                ChatStepItem(
                    step = step,
                    uiState = uiState,
                    launcher = launcher,
                    handleIntent = handleIntent,
                    modifier = Modifier.animateItem(),
                )
            }

            item {
                ActionButtons(uiState = uiState, handleIntent = handleIntent)
            }
        }

        if (uiState.authUiState == AuthUiState.Loading) {
            LoadingOverlay()
        }
    }
}

@Composable
private fun ChatStepItem(
    modifier: Modifier = Modifier,
    step: OnboardingStep,
    uiState: OnBoardingUiState,
    launcher: ActivityResultLauncher<IntentSenderRequest>?,
    handleIntent: (OnBoardingIntentHandler) -> Unit,
) {
    AnimatedVisibility(
        visible = true,
        enter = fadeIn() + slideInVertically(initialOffsetY = { it / 2 }),
    ) {
        when {
            step.isTypingIndicator -> TypingBubble(modifier = modifier)

            step.isFinal -> ChatBubble(
                modifier = modifier,
                isSent = false,
                timestamp = System.currentTimeMillis().toRelativeTime(),
                onClick = {
                    if (uiState.authUiState !is AuthUiState.Loading) {
                        handleIntent(OnBoardingIntentHandler.LaunchSignIn(launcher))
                    }
                },
                bubbleColor = MaterialTheme.colorScheme.primaryContainer,
            ) {
                ContinueWithGoogle()
            }

            else -> TextBubble(
                modifier = modifier,
                timestamp = System.currentTimeMillis().toRelativeTime(),
                isSent = false,
                isError = step.isError,
                annotatedText = when (val state = uiState.authUiState) {
                    is AuthUiState.Error -> AnnotatedString(state.message ?: "Something went wrong")
                    else -> step.render()
                },
            )
        }
    }
}

@Composable
private fun ActionButtons(
    uiState: OnBoardingUiState,
    handleIntent: (OnBoardingIntentHandler) -> Unit,
) {
    val isLastAndFinal = uiState.visibleSteps.isNotEmpty() &&
        uiState.visibleSteps.last().isFinal

    val canClick = !uiState.skipRequested && !isLastAndFinal

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp),
        horizontalArrangement = Arrangement.End,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        TextButton(
            onClick = { handleIntent(OnBoardingIntentHandler.ShowNextStep) },
            enabled = canClick,
        ) {
            Text("Next")
        }

        Spacer(modifier = Modifier.width(8.dp))

        TextButton(
            onClick = { handleIntent(OnBoardingIntentHandler.SkipToLast) },
            enabled = canClick,
        ) {
            Text("Skip")
        }
    }
}

@Preview
@Composable
fun ChatOnboardingScreenPreview(
    @PreviewParameter(ThemeModeProvider::class) isDark: Boolean,
) {
    val sampleSteps = listOf(
        OnboardingStep(
            delayMillis = 0,
            fullText = AnnotatedString("Welcome to \"What beats rock\""),
        ),
        OnboardingStep(
            delayMillis = 0,
            fullText = AnnotatedString("Here's how to get started."),
        ),
        OnboardingStep(
            delayMillis = 0,
            isFinal = true,
        ),
    )

    val sampleState = OnBoardingUiState(
        visibleSteps = sampleSteps,
        skipRequested = false,
        currentStep = 1,
    )

    ThemedPreviewTemplate(isDark) {
        ChatOnboardingList(
            uiState = sampleState,
            handleIntent = {},
            launcher = null,
        )
    }
}
