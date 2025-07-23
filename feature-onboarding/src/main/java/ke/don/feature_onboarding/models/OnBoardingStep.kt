package ke.don.feature_onboarding.models

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import com.google.firebase.auth.FirebaseUser
import java.util.UUID

data class OnBoardingUiState(
    val visibleSteps: List<OnboardingStep> = emptyList(),
    val skipRequested: Boolean = false,
    val authUiState: AuthUiState = AuthUiState.Idle,
    val currentStep: Int = 0
)

data class OnboardingStep(
    val id: String = UUID.randomUUID().toString(),
    val delayMillis: Long = 1000L,
    val isError: Boolean = false,
    val isFinal: Boolean = false,
    val isTypingIndicator: Boolean = false,
    val fullText: AnnotatedString = AnnotatedString(""),
    val currentWordIndex: Int = -1 // -1 means not started
){
    fun render(): AnnotatedString {
        val words = fullText.split(" ")
        return if (currentWordIndex in words.indices) {
            val text = words.take(currentWordIndex + 1).joinToString(" ")
            AnnotatedString(text)
        } else {
            fullText // show full
        }
    }

}

sealed class AuthUiState {
    object Idle : AuthUiState()
    object Loading : AuthUiState()
    data class Success(val user: FirebaseUser) : AuthUiState()
    data class Error(val message: String?) : AuthUiState()
    object Cancelled : AuthUiState()
}

val onboardingSteps = listOf(
    OnboardingStep(
        delayMillis = 0,
        fullText = buildAnnotatedString {
            append("Welcome to ")
            withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                append("\"What beats rock\"")
            }
        }
    ),
    OnboardingStep(
        delayMillis = 0,
        fullText = buildAnnotatedString {
            append("Before you sign up, here are the rules.")
        }
    ),
    OnboardingStep(
        delayMillis = 0,
        fullText = buildAnnotatedString {
            withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                append("Game Rules:\n\n")
            }
            append("✅ Only clever, hilarious, or laterally genius answers are accepted.\n")
            append("➤ Go beyond obvious size or power logic.\n")
            append("➤ Make us say 'aha!', not 'meh'.\n\n")

            append("⚠️ Rejected if:\n")
            append("➤ It repeats or rephrases an earlier response.\n")
            append("➤ It's too generic (e.g. 'air', 'everything', 'nothingness').\n")
            append("➤ It's lazy (e.g. 'stronger', 'hotter').\n")
            append("➤ We're unsure? REJECTED.\n\n")

            append("❌ Inappropriate? Game Over.\n")
            append("➤ You’ll get 0 points and the game ends.\n\n")

            append("🎯 Points are awarded 1–5 for creativity.\n")
            append("5 = pure brilliance, 1 = barely passed.\n\n")
        }
    ),

    OnboardingStep(
        delayMillis = 0,
        isFinal = true
    ),
)
