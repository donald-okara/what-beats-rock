package ke.don.core_datasource.domain.models

data class SpotlightPair(
    val prompt: String = "",
    val score: Int = 0,
    val isHighScore: Boolean = false,
    val userMessage: ChatMessage.User,
    val botMessage: ChatMessage.Bot
)

data class SpotlightModel(
    val profileUrl: String? = null,
    val spotlightPair: SpotlightPair? = null
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