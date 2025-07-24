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
package ke.don.core_datasource.remote.ai

import android.util.Log
import com.google.firebase.Firebase
import com.google.firebase.ai.ai
import ke.don.core_datasource.domain.ChatBotResponse
import ke.don.core_datasource.remote.ai.Prompts.GEMINI_MODEL
import ke.don.core_datasource.remote.ai.Prompts.buildStrictChatAnswerPrompt
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.boolean
import kotlinx.serialization.json.booleanOrNull
import kotlinx.serialization.json.int
import kotlinx.serialization.json.intOrNull
import kotlinx.serialization.json.jsonObject

class VertexProviderImpl : VertexProvider {
    private val vertexAI = Firebase.ai
    private val model = vertexAI.generativeModel(GEMINI_MODEL)

    override suspend fun generateChatResponse(
        pastResponses: List<String>,
        newResponse: String,
    ): GeminiResult<ChatBotResponse> {
        val prompt = buildStrictChatAnswerPrompt(pastResponses, newResponse)

        repeat(5) { attempt ->
            try {
                val responseStream = model.generateContentStream(prompt)

                val jsonBuilder = StringBuilder()
                responseStream.collect { chunk ->
                    chunk.text?.let { jsonBuilder.append(it) }
                }

                Log.d("VertexAI", "✅ JSON generated: $jsonBuilder")

                val rawJson = jsonBuilder
                    .toString()
                    .replace("```json", "")
                    .replace("```", "")
                    .trim()

                return try {
                    val parsed = Json.decodeFromString<ChatBotResponse>(rawJson)
                    GeminiResult.Success(parsed)
                } catch (decodeError: Exception) {
                    Log.w("VertexAI", "🛠️ Attempting repair after decode failure: ${decodeError.message}")

                    try {
                        val fallbackJson = Json.parseToJsonElement(rawJson).jsonObject
                        val repaired = repairChatBotJson(fallbackJson)
                        GeminiResult.Success(repaired)
                    } catch (repairError: Exception) {
                        Log.w("VertexAI", "❌ Repair failed: ${repairError.message}")
                        throw repairError
                    }
                }
            } catch (e: Exception) {
                Log.w("VertexAI", "⚠️ Attempt ${attempt + 1} failed: ${e.message}")
                if (attempt == 4) {
                    Log.e("VertexAI", "🚨 Final failure to parse JSON", e)
                    return GeminiResult.Error("Failed to parse generated chat after 5 tries: ${e.message}")
                }
            }
        }

        return GeminiResult.Error("Unexpected error") // Defensive fallback
    }

    fun repairChatBotJson(json: JsonObject): ChatBotResponse {
        val stringValues = mutableListOf<String>()
        val intValues = mutableListOf<Int>()
        val boolValues = mutableListOf<Boolean>()

        Log.d("JsonRepair", "🔧 Starting repair process on: $json")

        for ((key, value) in json) {
            when {
                value is JsonPrimitive && value.isString -> {
                    stringValues += value.content
                    Log.d("JsonRepair", "📝 Mapped string key '$key' → '${value.content}'")
                }
                value is JsonPrimitive && value.intOrNull != null -> {
                    intValues += value.int
                    Log.d("JsonRepair", "🔢 Mapped int key '$key' → ${value.int}")
                }
                value is JsonPrimitive && value.booleanOrNull != null -> {
                    boolValues += value.boolean
                    Log.d("JsonRepair", "✅ Mapped bool key '$key' → ${value.boolean}")
                }
                else -> {
                    Log.w("JsonRepair", "⚠️ Unrecognized type or malformed value at key '$key': $value")
                }
            }
        }

        if (stringValues.size != 1 || intValues.size != 1 || boolValues.size != 1) {
            val errorMsg = buildString {
                append("Unable to repair JSON:\n")
                append(" - Strings: $stringValues\n")
                append(" - Ints: $intValues\n")
                append(" - Bools: $boolValues")
            }
            Log.e("JsonRepair", "❌ $errorMsg")
            throw IllegalArgumentException(errorMsg)
        }

        val result = ChatBotResponse(
            message = stringValues.first(),
            awardedPoints = intValues.first(),
            isValid = boolValues.first(),
        )

        Log.d("JsonRepair", "✅ Successfully repaired JSON into: $result")
        return result
    }
}

interface VertexProvider {
    suspend fun generateChatResponse(
        pastResponses: List<String>,
        newResponse: String,
    ): GeminiResult<ChatBotResponse>
}

sealed class GeminiResult<out T> {
    data object Loading : GeminiResult<Nothing>()
    data class Success<T>(val data: T) : GeminiResult<T>()
    data class Error(val message: String) : GeminiResult<Nothing>()
}
