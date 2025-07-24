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

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import ke.don.core_datasource.domain.ChatBotResponse
import ke.don.core_datasource.remote.ai.GeminiResult
import ke.don.core_datasource.remote.ai.VertexProvider
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val vertexProvider: VertexProvider,
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
            is ChatIntentHandler.UpdateAnswer -> {
                updateAnswer(intent.answer)
            }
            is ChatIntentHandler.SendAnswer -> {
                sendAnswer()
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
                val result = vertexProvider.generateChatResponse(userResponses, _uiState.value.answer)

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
            val currentMessages = uiState.value.messages

            // 1. Add bot answer
            val updatedMessages = currentMessages + ChatMessage.Bot(
                message = response.message,
                timestamp = System.currentTimeMillis(),
                awardedPoints = response.awardedPoints,
            )

            // If the response is invalid, show game over immediately
            if (!response.isValid || response.awardedPoints == 0) {
                updateUiState(
                    _uiState.value.copy(
                        messages = updatedMessages,
                        answer = "",
                        gameOver = true,
                    ),
                )
                return@launch
            }

            // Update with bot answer first
            updateUiState(
                _uiState.value.copy(
                    messages = updatedMessages,
                    answer = "",
                    score = _uiState.value.score + response.awardedPoints,
                ),
            )

            // 2. Delay before follow-up prompt
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
}
