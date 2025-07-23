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

object Prompts {

    fun buildStrictChatAnswerPrompt(pastResponses: List<String>, newResponse: String): String {
        return buildString {
            appendLine("Hey Gemini 👋, we're playing a ruthless game of 'What beats what?'.")
            appendLine("The past user responses are (in order): ${pastResponses.joinToString()}.")
            appendLine("The new user response is: \"$newResponse\".")
            appendLine()
            appendLine("Your job is to judge the new response with zero mercy.")
            appendLine("Rules:")
            appendLine("- ✅ Accept only if the response is genuinely clever, hilarious, or shows strong lateral thinking.")
            appendLine("    ➤ It must go beyond obvious size/power comparisons.")
            appendLine("    ➤ It should feel like a witty 'aha!' moment, not a lazy guess.")
            appendLine("    ➤ If it's accepted, respond with a short, clever justification (emojis allowed, no markdown).")
            appendLine()
            appendLine("- ⚠️ Reject if:")
            appendLine("    ➤ It's a repeat or paraphrase of any earlier response.")
            appendLine("    ➤ It's too generic (e.g. 'air', 'universe', 'everything', 'nothingness') unless done with a clever twist.")
            appendLine("    ➤ It's conceptually weak (e.g. just 'stronger', 'bigger', 'hotter').")
            appendLine("    ➤ It lacks imagination or feels phoned-in.")
            appendLine("    ➤ If you're on the fence, reject it. We're not here to coddle mediocrity.")
            appendLine()
            appendLine("- ❌ If the response is vulgar, offensive, or inappropriate in any way, end the game immediately.")
            appendLine("    ➤ In this case, return a JSON response with:")
            appendLine("        - message: 'Game over' or similar explanation")
            appendLine("        - awardedPoints: 0")
            appendLine("        - isValid: false")
            appendLine()
            appendLine("When valid, award 1 to 5 points based on creativity. 5 is for chef's-kiss brilliance, 1 is 'barely passed'.")
            appendLine()
            appendLine("Now respond ONLY with a valid JSON object.")
            appendLine("❗ Do NOT include any extra text, disclaimers, or formatting outside the JSON.")
        }
    }

    fun buildMediumStrictPrompt(
        pastResponses: List<String>,
        newResponse: String,
    ): String {
        return buildString {
            appendLine("You're an evaluator in the game 'What beats what?'.")
            appendLine("Here are the past answers: ${pastResponses.joinToString()}")
            appendLine("Current user answer: \"$newResponse\"")
            appendLine()
            appendLine("Evaluate the new answer and return a valid JSON object **only**, no extra text.")
            appendLine("Use this exact schema:")
            appendLine(
                """
{
  "message": String,         // short playful justification (or "Game over" if offensive)
  "awardedPoints": Integer,  // 1 to 5 (or 0 if rejected or game ends)
  "isValid": Boolean         // true if accepted, false if rejected
}
                """.trimIndent(),
            )
            appendLine()
            appendLine("✅ Accept if:")
            appendLine(" - It's clever, funny, or adds novelty")
            appendLine(" - It's not a duplicate or rephrasing of a past answer")
            appendLine()
            appendLine("❌ Reject if:")
            appendLine(" - It's repeated, dull, or boring")
            appendLine(" - It's nonsensical or unrelated")
            appendLine()
            appendLine("If the response is offensive or inappropriate, return:")
            appendLine(
                """
{
  "message": "Game over",
  "awardedPoints": 0,
  "isValid": false
}
                """.trimIndent(),
            )
            appendLine()
            appendLine("🔒 Always return valid JSON. Do not include markdown, explanations, or commentary. No text before or after the JSON.")
        }
    }

    fun buildCasualChatAnswerPrompt(pastResponses: List<String>, newResponse: String): String {
        return buildString {
            appendLine("Hey Gemini 👋, we're playing a fun game of 'What beats what?'.")
            appendLine("The past user responses are (in order): ${pastResponses.joinToString()}.")
            appendLine("The new user response is: \"$newResponse\".")
            appendLine()
            appendLine("Your job is to judge the new response fairly but playfully.")
            appendLine("Rules:")
            appendLine("- ✅ Accept if the response is clever, funny, unexpected, or makes some creative sense.")
            appendLine("    ➤ It can be conceptual, metaphorical, or even silly — as long as it's interesting.")
            appendLine("    ➤ Justify accepted answers with a short, witty explanation (emojis welcome).")
            appendLine()
            appendLine("- ⚠️ Gently reject if:")
            appendLine("    ➤ It's a repeat of a previous response.")
            appendLine("    ➤ It's too generic without any twist (e.g. 'everything', 'bigger', 'time').")
            appendLine("    ➤ It feels lazy or unoriginal.")
            appendLine()
            appendLine("- ❌ If the response is inappropriate or offensive, end the game immediately.")
            appendLine("    ➤ In this case, return a JSON response with:")
            appendLine("        - message: 'Game over' or similar explanation")
            appendLine("        - awardedPoints: 0")
            appendLine("        - isValid: false")
            appendLine()
            appendLine("For valid responses, award 1 to 5 points based on creativity. 5 = brilliant, 1 = decent effort.")
            appendLine()
            appendLine("Now respond ONLY with a valid JSON object.")
            appendLine("❗ Do NOT include any extra text, disclaimers, or formatting outside the JSON.")
        }
    }

    const val GEMINI_MODEL = "gemini-2.5-flash"
}
