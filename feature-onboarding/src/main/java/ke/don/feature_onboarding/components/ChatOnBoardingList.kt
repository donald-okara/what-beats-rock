package ke.don.feature_onboarding.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import ke.don.core_designsystem.material_theme.components.ChatBubble
import ke.don.core_designsystem.material_theme.components.TextBubble
import ke.don.core_designsystem.material_theme.components.TypingBubble
import ke.don.core_designsystem.material_theme.ui.theme.ThemeModeProvider
import ke.don.core_designsystem.material_theme.ui.theme.ThemedPreviewTemplate
import ke.don.feature_onboarding.models.OnBoardingIntentHandler
import ke.don.feature_onboarding.models.OnBoardingUiState
import ke.don.feature_onboarding.models.OnboardingStep

@Composable
fun ChatOnboardingList(
    uiState: OnBoardingUiState,
    handleIntent: (OnBoardingIntentHandler) -> Unit,
) {
    val visibleSteps = uiState.visibleSteps

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Bottom
    ) {
        items(visibleSteps, key = { it.id }) { step ->
            AnimatedVisibility(
                visible = true,
                enter = fadeIn() + slideInVertically(initialOffsetY = { it / 2 }),
            ) {
                if (step.isTypingIndicator) {
                    TypingBubble()
                } else if (step.isFinal){
                    ChatBubble(
                        isSent = false,
                        onClick = {},
                        bubbleColor = MaterialTheme.colorScheme.primaryContainer
                    ) {
                        ContinueWithGoogle()
                    }
                } else {
                    TextBubble(
                        isSent = false,
                        annotatedText = step.render()
                    )
                }
            }
        }
        item {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(12.dp),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(
                    onClick = { handleIntent(OnBoardingIntentHandler.ShowNextStep) },
                    enabled = !uiState.skipRequested
                ) {
                    Text("Next")
                }

                Spacer(modifier = Modifier.width(8.dp))

                TextButton(
                    enabled = !uiState.skipRequested,
                    onClick = { handleIntent(OnBoardingIntentHandler.SkipToLast) }
                ) {
                    Text("Skip")
                }
            }
        }
    }
}

@Preview
@Composable
fun ChatOnboardingScreenPreview(
    @PreviewParameter(ThemeModeProvider::class) isDark: Boolean
) {
    val sampleSteps = listOf(
        OnboardingStep(
            delayMillis = 0,
            fullText = AnnotatedString("Welcome to \"What beats rock\"")
        ),
        OnboardingStep(
            delayMillis = 0,
            fullText = AnnotatedString("Here's how to get started.")
        ),
        OnboardingStep(
            delayMillis = 0,
            isFinal = true,
        )
    )

    val sampleState = OnBoardingUiState(
        visibleSteps = sampleSteps,
        skipRequested = false,
        currentStep = 1
    )

    ThemedPreviewTemplate(isDark){
        ChatOnboardingList(
            uiState = sampleState,
            handleIntent = {}
        )
    }
}
