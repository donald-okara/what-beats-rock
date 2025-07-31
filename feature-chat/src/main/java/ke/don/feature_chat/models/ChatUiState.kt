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

import ke.don.core_datasource.domain.models.ChatMessage
import ke.don.core_datasource.domain.models.Profile
import ke.don.core_datasource.domain.models.Session
import ke.don.core_datasource.domain.models.SpotlightPair

data class ChatUiState(
    val messages: List<ChatMessage> = emptyList(),
    val session: Session = Session(id = null),
    val gamesPlayed: Int = 0,
    val profile: Profile = Profile(),
    val fetchIsError: Boolean = false,
    val highScoreMessageSent: Boolean = false,
    val highScoreError: Boolean = false,
    val isFetchingSession: Boolean = true,
    val lastAnswer: String = "rock",
    val answer: String = "",
    val isGenerating: Boolean = false,
    val startIsSuccessful: Boolean = false,
    val generateError: String? = null,
    val isGenetateError: Boolean = false,
    val score: Int = 0,
    val gameOver: Boolean = false,
    val showGameOver: Boolean = false,
    // 👇 new field

    val spotlightPair: SpotlightPair? = null,
)
