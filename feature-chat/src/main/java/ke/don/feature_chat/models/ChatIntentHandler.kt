package ke.don.feature_chat.models

sealed class ChatIntentHandler {
    data class UpdateAnswer(val answer: String): ChatIntentHandler()
    data object SendAnswer: ChatIntentHandler()
}