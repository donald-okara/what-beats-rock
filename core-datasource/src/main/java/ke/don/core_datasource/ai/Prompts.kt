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

import ke.don.core_datasource.domain.ItineraryItem
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

object Prompts {
    fun buildItineraryPrompt(title: String, description: String): String {
        return """
        I am planning an itinerary titled "$title". Here's the description: "$description".

        Generate a list of up to 4 itinerary items for this trip. Respond ONLY with a valid JSON array matching the following structure:

        [
          {
            "id": "a1",
            "title": "Visit Nairobi National Park",
            "isLocked": false,
            "isGenerated": true
          },
          ...
        ]

        Use short titles. Use unique string IDs like "a1", "b2", etc. 
        Set isGenerated to true and isLocked to false.
        Do NOT include markdown (no ```json), no explanations, and no prose — ONLY the JSON array. 
        """.trimIndent()
    }

    fun buildDescriptionPrompt(title: String): String {
        return "Hi Gemini, I'm planning an itinerary titled '$title'." +
            "Could you help me write a description for it? Please keep it stictly within $MAX_TOKENS tokens." +
            "        Do not make it markdown or give me options, just a sentence, keep it first person and acknowledge if plural"
    }

    fun buildInsertPrompt(title: String, description: String, currentList: List<ItineraryItem>): String {
        val listJson = Json.encodeToString(currentList)
        return """
        I have an itinerary titled "$title": $description.

        Here is the current list of itinerary steps:
        $listJson

       
        Please suggest up to 2 new itinerary items to insert into this list. 
        For each suggestion, include a `position` field that indicates the 0-based index **where the new item should be inserted**.
        Also return fields `id`, `title`, `isGenerated`, and `isLocked` as per this structure:

        {
          "id": "z1",
          "title": "Stop at Lake Elementaita",
          "isGenerated": true,
          "isLocked": false,
          "position": 2
        }
         IMPORTANT:
                - Do **not** repeat or reword any items already in the list above.
                - Only suggest **new, unique** items that complement the itinerary.
                - Avoid duplicates in meaning, location, or activity.
                - Do **not** include any markdown (e.g., ```json), prose, or explanations. Just return a raw JSON array.

        Respond with a JSON array of such objects only. No markdown or prose.
        
        This means your string starts with backticks and not the `[` character expected by the JSON parser.
        """.trimIndent()
    }

    const val GEMINI_MODEL = "gemini-2.5-flash"
    const val MAX_TOKENS = 100
}
