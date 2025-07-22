package ke.don.feature_chat.models

data class ChatUiState(
    val messages: List<ChatMessage> = emptyList(),
    val lastAnswer: String = "rock",
    val answer: String = "",
    val isGenerating: Boolean = false,
    val generateError: String? = null,
    val isGenetateError: Boolean = false,
    val score: Int = 0,
    val gameOver: Boolean = false
)

sealed class ChatMessage {
    data class Bot(
        val message: String,
        val timestamp: Long,
        val awardedPoints: Int? = null // null = regular message, else show score
    ) : ChatMessage()

    data class User(
        val answer: String,
        val timestamp: Long,
    ) : ChatMessage()
}

