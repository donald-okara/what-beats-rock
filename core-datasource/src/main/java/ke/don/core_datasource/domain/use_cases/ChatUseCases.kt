package ke.don.core_datasource.domain.use_cases

import android.util.Log
import ke.don.core_datasource.domain.ChatBotResponse
import ke.don.core_datasource.domain.models.Profile
import ke.don.core_datasource.domain.models.Session
import ke.don.core_datasource.remote.FirebaseApi
import ke.don.core_datasource.remote.ai.GeminiResult
import ke.don.core_datasource.remote.ai.VertexProvider
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

class ChatUseCaseImpl(
    private val api: FirebaseApi,
    private val ai: VertexProvider
): ChatUseCase{
    override suspend fun generateChatResponse(
        pastResponses: List<String>,
        newResponse: String,
    ): GeminiResult<ChatBotResponse> = ai.generateChatResponse(pastResponses, newResponse)

    override suspend fun fetchMyProfile(): Result<Profile> = api.fetchMyProfile()

    override suspend fun updateHighScore(newScore: Int): Result<Unit> = api.updateHighScore(newScore)

    override suspend fun fetchChatSession(): Result<Pair<Session, Int>> {
        val sessionsResult = api.fetchOrInitializeUserSessions()

        if (sessionsResult.isFailure) {
            return Result.failure(sessionsResult.exceptionOrNull() ?: Exception("Failed to fetch chat session"))
        }

        val sessions = sessionsResult.getOrThrow()

        val today = LocalDate.now()

        // Get the most recent session based on time
        val lastSession = sessions.maxByOrNull { it.time }
        val lastSessionDate = lastSession?.let {
            Instant.ofEpochMilli(it.time)
                .atZone(ZoneId.systemDefault())
                .toLocalDate()
        }


        if (lastSessionDate != today) {
            val resetResult = api.resetUserSessions()
            return if (resetResult.isSuccess) {
                val newSession = resetResult.getOrThrow().firstOrNull()
                if (newSession != null) {
                    Result.success(newSession to 0)
                } else {
                    Result.failure(Exception("No new session created after reset"))
                }
            } else {
                Result.failure(resetResult.exceptionOrNull() ?: Exception("Failed to reset sessions"))
            }
        }

        val session = sessions.firstOrNull { !it.started }
            ?: return Result.failure(Exception("🚫 No active chat session found"))

        val completedCount = sessions.count { it.started }
        return Result.success(session to completedCount)
    }

    override suspend fun saveChatSession(session: Session): Result<Unit> =
        session.id?.let { api.updateUserSession(it, session) } ?: Result.failure(Exception("Session Id is null"))


    override suspend fun startSession(session: Session): Result<Unit> {
        val updatedSession = session.copy(started = true)
        return updatedSession.id?.let { api.updateUserSession(it, updatedSession) } ?: Result.failure(Exception("Session Id is null"))
    }
}

interface ChatUseCase {
    suspend fun generateChatResponse(
        pastResponses: List<String>,
        newResponse: String,
    ): GeminiResult<ChatBotResponse>

    suspend fun fetchMyProfile(): Result<Profile>
    suspend fun updateHighScore(newScore: Int): Result<Unit>
    suspend fun fetchChatSession(): Result<Pair<Session, Int>>
    suspend fun saveChatSession(session: Session): Result<Unit>
    suspend fun startSession(session: Session): Result<Unit>
}