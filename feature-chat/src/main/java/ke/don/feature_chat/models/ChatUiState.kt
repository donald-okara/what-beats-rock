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

data class ChatUiState(
    val messages: List<ChatMessage> = emptyList(),
    val lastAnswer: String = "rock",
    val answer: String = "",
    val isGenerating: Boolean = false,
    val generateError: String? = null,
    val isGenetateError: Boolean = false,
    val score: Int = 0,
    val gameOver: Boolean = false,
)

sealed class ChatMessage {
    data class Bot(
        val message: String,
        val timestamp: Long,
        val awardedPoints: Int? = null, // null = regular message, else show score
    ) : ChatMessage()

    data class User(
        val answer: String,
        val timestamp: Long,
    ) : ChatMessage()
}
