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
package ke.don.feature_chat.models

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import ke.don.core_datasource.domain.ChatBotResponse
import ke.don.core_datasource.domain.models.ChatMessage
import ke.don.core_datasource.domain.models.SpotlightPair
import ke.don.core_datasource.domain.use_cases.ChatUseCase
import ke.don.core_datasource.remote.ai.GeminiResult
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val useCase: ChatUseCase,
) : ViewModel() {
    private val _uiState = MutableStateFlow(ChatUiState())
    val uiState: StateFlow<ChatUiState> = _uiState

    fun updateUiState(newUiState: ChatUiState) {
        _uiState.update {
            newUiState
        }
    }

    fun handleIntent(intent: ChatIntentHandler) {
        when (intent) {
            is ChatIntentHandler.FetchSession -> {
                fetchSessionAndProfile()
            }
            is ChatIntentHandler.UpdateAnswer -> {
                updateAnswer(intent.answer)
            }
            is ChatIntentHandler.SendAnswer -> {
                sendAnswer()
            }
            is ChatIntentHandler.ResetState -> {
                resetState()
            }
            is ChatIntentHandler.SaveHighScore -> {
                updateHighScore(uiState.value.score)
            }
            is ChatIntentHandler.ToggleGameOverDialog -> updateUiState(
                _uiState.value.copy(
                    showGameOver = !_uiState.value.showGameOver,
                ),
            )
        }
    }

    fun fetchSessionAndProfile() {
        viewModelScope.launch {
            val sessionDeferred = async { fetchSession() }
            val profileDeferred = async { fetchProfile() }

            val sessionResult = sessionDeferred.await()
            val profileResult = profileDeferred.await()
            val isError = !sessionResult || !profileResult

            updateUiState(
                _uiState.value.copy(
                    isFetchingSession = false,
                    fetchIsError = isError,
                ),
            )
        }
    }

    suspend fun fetchSession(): Boolean {
        val result = useCase.fetchChatSession()
        return if (result.isSuccess) {
            val (session, completedCount) = result.getOrThrow()
            updateUiState(
                _uiState.value.copy(
                    session = session,
                    gamesPlayed = completedCount,
                ),
            )
            true
        } else {
            updateUiState(
                _uiState.value.copy(
                    fetchIsError = true,
                ),
            )
            false
        }
    }

    suspend fun fetchProfile(): Boolean {
        val result = useCase.fetchMyProfile()
        Log.d("ChatViewModel", "fetchSessionAndProfile: ${result.getOrNull()}")

        return if (result.isSuccess) {
            updateUiState(
                _uiState.value.copy(
                    profile = result.getOrThrow(),
                ),
            )
            true
        } else {
            updateUiState(
                _uiState.value.copy(
                    fetchIsError = true,
                ),
            )
            false
        }
    }

    fun updateHighScore(highScore: Int) {
        viewModelScope.launch {
            val result = useCase.updateHighScore(highScore)

            when {
                result.isFailure -> {
                    updateUiState(
                        _uiState.value.copy(
                            highScoreError = true,
                        ),
                    )
                }
            }
        }
    }

    fun updateAnswer(answer: String) {
        updateUiState(_uiState.value.copy(answer = answer))
    }

    fun sendAnswer() {
        viewModelScope.launch {
            val userResponses = listOf("rock") + uiState.value.messages
                .filterIsInstance<ChatMessage.User>()
                .map { it.answer }

            val answer = _uiState.value.answer

            val newMessage = ChatMessage.User(
                answer = answer,
                timestamp = System.currentTimeMillis(),
            )

            updateUiState(
                _uiState.value.copy(
                    isGenerating = true,
                    isGenetateError = false,
                    generateError = null,
                    lastAnswer = answer,
                    messages = _uiState.value.messages + newMessage,
                ),
            )

            if (uiState.value.answer.isNotBlank()) {
                val result = useCase.generateChatResponse(userResponses, _uiState.value.answer)

                when (result) {
                    is GeminiResult.Loading -> {
                        // Optional: Show a loading spinner if you want
                    }
                    is GeminiResult.Success -> {
                        updateUiState(
                            _uiState.value.copy(
                                isGenerating = false,
                            ),
                        )
                        updateBotMessages(result.data)
                    }
                    is GeminiResult.Error -> {
                        updateUiState(
                            _uiState.value.copy(
                                isGenerating = false,
                                isGenetateError = true,
                                messages = _uiState.value.messages - newMessage,
                                generateError = "🔥 We had trouble generating that one, please retry",
                            ),
                        )
                    }
                }
            }
        }
    }

    fun updateBotMessages(response: ChatBotResponse) {
        viewModelScope.launch {
            val currentState = _uiState.value
            val currentMessages = currentState.messages
            val currentScore = currentState.score
            val newScore = currentScore + response.awardedPoints
            val isHighScore = currentState.profile.highScore!! < newScore
            val alreadySent = currentState.highScoreMessageSent

            // Start session if first bot message
            if (!currentState.startIsSuccessful) {
                val result = currentState.session.id?.let {
                    useCase.startSession(
                        currentState.session.copy(
                            started = true,
                            score = newScore,
                        ),
                    )
                }
                Log.d("ChatViewModel", "fetchSessionAndProfile: ${currentState.profile}")

                Log.d("ChatViewModel", "updateBotMessages: $result")
                Log.d("ChatViewModel", "session: ${currentState.session}")

                if (result?.isSuccess == true) {
                    updateUiState(
                        currentState.copy(
                            startIsSuccessful = true,
                            session = currentState.session.copy(started = true),
                        ),
                    )
                }
            }

            if (isHighScore && !alreadySent) {
                updateUiState(
                    _uiState.value.copy(
                        highScoreMessageSent = true,
                    ),
                )
            }

            val messageText = buildString {
                append(response.message)
                if (isHighScore && !alreadySent) {
                    append("\n\nWhoa! You just hit a new high score: $newScore 🎉🔥")
                }
            }

            // Add bot response
            val botMessage = ChatMessage.Bot(
                message = messageText,
                timestamp = System.currentTimeMillis(),
                awardedPoints = response.awardedPoints,
            )

            val updatedMessages = currentMessages + botMessage

            // End game if invalid
            if (!response.isValid || response.awardedPoints == 0) {
                val spotlight = findSpotlightPair(prompt = uiState.value.lastAnswer, score = uiState.value.score, isHighScore = uiState.value.highScoreMessageSent, updatedMessages)

                updateUiState(
                    currentState.copy(
                        messages = updatedMessages,
                        answer = "",
                        gameOver = true,
                        showGameOver = true,
                        spotlightPair = spotlight,
                        highScoreMessageSent = isHighScore,
                    ),
                )
                if (isHighScore) updateHighScore(newScore)
                useCase.saveChatSession(currentState.session.copy(score = newScore, started = true))
                return@launch
            }

            // Update state with bot message and score
            updateUiState(
                currentState.copy(
                    messages = updatedMessages,
                    answer = "",
                    session = currentState.session.copy(score = newScore),
                    score = newScore,
                ),
            )

            // Delay then send follow-up
            delay(500L)
            val followUpMessage = ChatMessage.Bot(
                message = "What beats ${_uiState.value.lastAnswer}? 🤔",
                timestamp = System.currentTimeMillis(),
            )
            updateUiState(
                _uiState.value.copy(
                    messages = _uiState.value.messages + followUpMessage,
                ),
            )
        }
    }

    fun findSpotlightPair(
        prompt: String,
        score: Int,
        isHighScore: Boolean,
        messages: List<ChatMessage>,
    ): SpotlightPair? {
        val botWithPoints = messages
            .mapIndexedNotNull { index, message ->
                if (message is ChatMessage.Bot && message.awardedPoints != null) {
                    Pair(index, message)
                } else {
                    null
                }
            }
            .maxByOrNull { it.second.awardedPoints ?: 0 } // first with highest score

        return botWithPoints?.let { (botIndex, botMessage) ->
            val userMessage = messages.subList(0, botIndex)
                .lastOrNull { it is ChatMessage.User } as? ChatMessage.User

            if (userMessage != null) {
                SpotlightPair(prompt = prompt, isHighScore = isHighScore, score = score, userMessage = userMessage, botMessage = botMessage)
            } else {
                null
            }
        }
    }

    fun resetState() {
        updateUiState(ChatUiState())
    }
}
