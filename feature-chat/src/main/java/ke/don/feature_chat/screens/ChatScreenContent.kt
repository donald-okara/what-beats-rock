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
package ke.don.feature_chat.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Send
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import ke.don.core_datasource.domain.models.ChatMessage
import ke.don.core_datasource.domain.models.SpotlightModel
import ke.don.core_designsystem.material_theme.components.FormTextField
import ke.don.core_designsystem.material_theme.components.TextBubble
import ke.don.core_designsystem.material_theme.components.TypingBubble
import ke.don.core_designsystem.material_theme.components.toRelativeTime
import ke.don.core_designsystem.material_theme.ui.theme.ThemeModeProvider
import ke.don.core_designsystem.material_theme.ui.theme.ThemedPreviewTemplate
import ke.don.feature_chat.R
import ke.don.feature_chat.components.GameOverDialog
import ke.don.feature_chat.components.ShimmerChatPlaceholder
import ke.don.feature_chat.models.ChatIntentHandler
import ke.don.feature_chat.models.ChatUiState

@Composable
fun ChatScreenContent(
    modifier: Modifier = Modifier,
    uiState: ChatUiState,
    navigateToShare: (SpotlightModel) -> Unit,
    handleIntent: (ChatIntentHandler) -> Unit,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Bottom,
        modifier = modifier
            .padding(8.dp)
            .fillMaxSize(),
    ) {
        ChatList(
            modifier = Modifier
                .fillMaxWidth(),
            uiState = uiState,
            handleIntent = handleIntent,
            navigateToShare = navigateToShare,
        )
    }
}

@Composable
fun ChatList(
    uiState: ChatUiState,
    modifier: Modifier = Modifier,
    navigateToShare: (SpotlightModel) -> Unit,
    handleIntent: (ChatIntentHandler) -> Unit,
) {
    val enabled = !uiState.isGenerating && !uiState.gameOver && uiState.gamesPlayed < 5
    LazyColumn(
        modifier = modifier,
        reverseLayout = true,
        contentPadding = PaddingValues(bottom = 16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        item {
            AnimatedVisibility(visible = uiState.gamesPlayed < 5 && !uiState.gameOver && !uiState.isFetchingSession) {
                UserInputBar(
                    onMessageSent = { handleIntent(ChatIntentHandler.SendAnswer) },
                    value = uiState.answer,
                    onValueChange = { handleIntent(ChatIntentHandler.UpdateAnswer(it)) },
                    enabled = enabled,
                    lastPrompt = uiState.lastAnswer,
                    modifier = Modifier
                        .fillMaxWidth(),
                )
            }
        }
        item {
            AnimatedVisibility(visible = uiState.isGenerating) {
                TypingBubble(
                    modifier = Modifier.animateItem(),
                )
            }
        }

        item {
            AnimatedVisibility(visible = uiState.gameOver) {
                Column(modifier = Modifier.animateItem()) {
                    TextBubble(
                        isSent = false,
                        annotatedText = buildAnnotatedString {
                            append(stringResource(R.string.and_that_s_a_wrap))
                            withStyle(style = SpanStyle(fontWeight = FontWeight.ExtraBold)) {
                                append(" ${uiState.score}")
                            }
                            append(stringResource(R.string.fancy_another_round))
                        },
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    TextBubble(
                        isSent = false,
                        annotatedText = buildAnnotatedString {
                            append(stringResource(R.string.tap_me_to_start_again))
                        },
                        onClick = {
                            handleIntent(ChatIntentHandler.ResetState)
                            handleIntent(ChatIntentHandler.FetchSession)
                        },
                    )
                }
            }
        }

        item {
            AnimatedVisibility(visible = uiState.highScoreError) {
                TextBubble(
                    isSent = false,
                    text = stringResource(R.string.we_had_trouble_saving_your_highscore),
                    isError = true,
                    onClick = { handleIntent(ChatIntentHandler.SaveHighScore) },
                )
            }
        }
        item {
            AnimatedVisibility(visible = uiState.isGenetateError) {
                uiState.generateError?.let {
                    TextBubble(
                        isSent = false,
                        text = it,
                        isError = true,
                    )
                }
            }
        }

        itemsIndexed(
            uiState.messages.reversed(),
            key = { _, message ->
                when (message) {
                    is ChatMessage.User -> "user:${message.answer}_${message.timestamp}"
                    is ChatMessage.Bot -> "bot:${message.message}_${message.timestamp}"
                }
            },
        ) { _, message ->
            AnimatedVisibility(visible = true) {
                when (message) {
                    is ChatMessage.User -> {
                        TextBubble(
                            profileUrl = uiState.profile.photoUrl,
                            isSent = true,
                            text = message.answer,
                            timestamp = message.timestamp.toRelativeTime(),
                            modifier = Modifier.animateItem(),
                        )
                    }

                    is ChatMessage.Bot -> {
                        TextBubble(
                            isSent = false,
                            text = message.message,
                            timestamp = message.timestamp.toRelativeTime(),
                            pointsEarned = message.awardedPoints,
                            modifier = Modifier.animateItem(),
                        )
                    }
                }
            }
        }

        if (uiState.isFetchingSession) {
            item {
                ShimmerChatPlaceholder(
                    modifier = modifier.animateItem(),
                )
            }
        } else if (uiState.fetchIsError) {
            item {
                TextBubble(
                    modifier = modifier.animateItem(),
                    isSent = false,
                    text = stringResource(R.string.something_went_wrong),
                    isError = true,
                    onClick = { handleIntent(ChatIntentHandler.FetchSession) },
                )
            }
        } else {
            item(key = "intro") {
                if (uiState.gamesPlayed < 5) {
                    Column(modifier = Modifier.animateItem()) {
                        TextBubble(
                            isSent = false,
                            text = stringResource(
                                R.string.you_have_games_left_today,
                                5 - uiState.gamesPlayed,
                            ),
                        )
                        Spacer(modifier = Modifier.height(4.dp))

                        TextBubble(isSent = false, text = stringResource(R.string.nothing_beats_rock))
                        Spacer(modifier = Modifier.height(4.dp))
                        TextBubble(isSent = false, text = stringResource(R.string.unless))
                        Spacer(modifier = Modifier.height(4.dp))
                        TextBubble(isSent = false, text = stringResource(R.string.what_beats_rock))
                    }
                } else {
                    TextBubble(
                        isSent = false,
                        text = stringResource(R.string.you_ve_played_all_your_games_for_today),
                    )
                }
            }
        }
    }

    if (uiState.showGameOver) {
        GameOverDialog(
            uiState = uiState,
            handleIntent = handleIntent,
            navigateToShare = navigateToShare,
        )
    }
}

@Composable
fun UserInputBar(
    modifier: Modifier = Modifier,
    onMessageSent: () -> Unit,
    value: String,
    lastPrompt: String,
    enabled: Boolean = true,
    onValueChange: (String) -> Unit,
) {
    val length = value.length
    val limit = 40
    val isError = length > limit
    val errorMessage = if (isError) "Message too long" else null

    FormTextField(
        modifier = modifier,
        text = value,
        onValueChange = onValueChange,
        trailingIcon = Icons.AutoMirrored.Outlined.Send,
        onClick = {
            if (!isError) onMessageSent()
        },
        label = stringResource(R.string.what_beats, lastPrompt),
        placeholder = stringResource(R.string.a_hammer),
        maxLength = 15,
        nameLength = value.length,
        isError = value.length > 15,
        enabled = enabled,
        showLength = true,
        errorMessage = errorMessage,
    )
}

@Preview
@Composable
fun ChatScreenPreview(
    @PreviewParameter(ThemeModeProvider::class) isDark: Boolean,
) {
    val previewUiState = ChatUiState(
        showGameOver = true,
        messages = listOf(
            ChatMessage.Bot(
                message = "What beats rock? 🤔",
                timestamp = System.currentTimeMillis(),
            ),
            ChatMessage.User(
                answer = "Paper",
                timestamp = System.currentTimeMillis(),
            ),
            ChatMessage.Bot(
                message = "Correct! Paper can wrap around rock.",
                timestamp = System.currentTimeMillis(),
                awardedPoints = 3,
            ),
            ChatMessage.Bot(
                message = "What beats paper? 🤔",
                timestamp = System.currentTimeMillis(),
            ),
        ),
        lastAnswer = "paper",
        score = 3,
        isGenerating = false,
        isGenetateError = true,
        generateError = "Something went wrong",
        gameOver = false,
    )

    ThemedPreviewTemplate(isDark) {
        ChatScreenContent(uiState = previewUiState, navigateToShare = {}, handleIntent = {})
    }
}
