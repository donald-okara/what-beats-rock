package ke.don.core_datasource.domain

import kotlinx.serialization.Serializable
import java.time.Instant
import java.time.Duration
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@Serializable
data class ChatBotResponse(
    val message: String,
    val awardedPoints: Int,
    val isValid: Boolean
)



fun Long.toRelativeTime(): String {
    val messageTime = Instant.ofEpochMilli(this)
    val now = Instant.now()
    val duration = Duration.between(messageTime, now)

    return when {
        duration.toMinutes() < 1 -> "just now"
        duration.toMinutes() < 60 -> "${duration.toMinutes()} minutes ago"
        duration.toHours() < 24 -> "${duration.toHours()} hours ago"
        duration.toDays() < 7 -> "${duration.toDays()} days ago"
        else -> {
            val formatter = DateTimeFormatter.ofPattern("MMM d").withZone(ZoneId.systemDefault())
            formatter.format(messageTime)
        }
    }
}
