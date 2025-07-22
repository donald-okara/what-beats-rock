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
package ke.don.core_datasource.ai

import android.util.Log
import com.google.firebase.Firebase
import com.google.firebase.ai.ai
import ke.don.core_datasource.ai.Prompts.GEMINI_MODEL
import ke.don.core_datasource.ai.Prompts.buildDescriptionPrompt
import ke.don.core_datasource.ai.Prompts.buildInsertPrompt
import ke.don.core_datasource.ai.Prompts.buildItineraryPrompt
import ke.don.core_datasource.domain.InsertionSuggestion
import ke.don.core_datasource.domain.ItineraryItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.serialization.json.Json

class VertexProviderImpl : VertexProvider {
    private val vertexAI = Firebase.ai
    private val model = vertexAI.generativeModel(GEMINI_MODEL)

    override fun generateDescription(title: String): Flow<GeminiResult<String>> = flow {
        emit(GeminiResult.Loading)

        val responseStream = model.generateContentStream(buildDescriptionPrompt(title))

        responseStream.collect { chunk ->
            emit(GeminiResult.Success(chunk.text.orEmpty()))
        }
    }.catch { e ->
        Log.e("VertexAI", "🔥 Failed to generate content", e)
        emit(GeminiResult.Error(e.message ?: "Unknown error"))
    }

    override fun generateItineraryItems(
        title: String,
        description: String,
    ): Flow<GeminiResult<List<ItineraryItem>>> = flow {
        emit(GeminiResult.Loading)

        val responseStream = model.generateContentStream(buildItineraryPrompt(title, description))

        val jsonBuilder = StringBuilder()

        responseStream.collect { chunk ->
            chunk.text?.let { jsonBuilder.append(it) }
        }
        Log.d("VertexAI", "✅ JSON generated: $jsonBuilder")

        try {
            val parsed = Json.decodeFromString<List<ItineraryItem>>(jsonBuilder.toString())
            val capped = parsed.take(4)
            emit(GeminiResult.Success(capped))
        } catch (e: Exception) {
            Log.e("VertexAI", "🚨 JSON parsing failed", e)
            emit(GeminiResult.Error("Failed to parse generated itinerary: ${e.message}"))
        }
    }.catch { e ->
        Log.e("VertexAI", "🔥 Failed to generate itinerary items", e)
        emit(GeminiResult.Error(e.message ?: "Unknown error"))
    }

    override fun insertItineraryItems(
        title: String,
        description: String,
        itineraryList: List<ItineraryItem>,
    ): Flow<GeminiResult<List<InsertionSuggestion>>> = flow {
        emit(GeminiResult.Loading)

        val responseStream = model.generateContentStream(buildInsertPrompt(title, description, itineraryList))

        val jsonBuilder = StringBuilder()

        responseStream.collect { chunk ->
            chunk.text?.let { jsonBuilder.append(it) }
        }
        Log.d("VertexAI", "✅ JSON generated: $jsonBuilder")

        try {
            val rawJson = jsonBuilder
                .toString()
                .replace("```json", "")
                .replace("```", "")
                .trim()

            val parsed = Json.decodeFromString<List<InsertionSuggestion>>(rawJson)
            val capped = parsed.take(4)
            emit(GeminiResult.Success(capped))
        } catch (e: Exception) {
            Log.e("VertexAI", "🚨 JSON parsing failed", e)
            emit(GeminiResult.Error("Failed to parse generated itinerary: ${e.message}"))
        }
    }.catch { e ->
        Log.e("VertexAI", "🔥 Failed to generate itinerary items", e)
        emit(GeminiResult.Error(e.message ?: "Unknown error"))
    }
}

interface VertexProvider {
    fun generateDescription(title: String): Flow<GeminiResult<String>>
    fun generateItineraryItems(title: String, description: String): Flow<GeminiResult<List<ItineraryItem>>>
    fun insertItineraryItems(title: String, description: String, itineraryList: List<ItineraryItem>): Flow<GeminiResult<List<InsertionSuggestion>>>
}

sealed class GeminiResult<out T> {
    data object Loading : GeminiResult<Nothing>()
    data class Success<T>(val data: T) : GeminiResult<T>()
    data class Error(val message: String) : GeminiResult<Nothing>()
}
