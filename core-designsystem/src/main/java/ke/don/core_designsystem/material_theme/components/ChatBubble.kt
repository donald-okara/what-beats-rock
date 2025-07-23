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
package ke.don.core_designsystem.material_theme.components

import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import ke.don.core_designsystem.material_theme.ui.theme.ThemeModeProvider
import ke.don.core_designsystem.material_theme.ui.theme.ThemedPreviewTemplate

@Composable
fun ChatBubble(
    modifier: Modifier = Modifier,
    isSent: Boolean,
    isError: Boolean = false,
    timestamp: String? = null,
    pointsEarned: Int? = null,
    onClick: () -> Unit = {},
    bubbleColor: Color = if (isSent) MaterialTheme.colorScheme.surfaceVariant else if (isError) MaterialTheme.colorScheme.errorContainer else MaterialTheme.colorScheme.inverseSurface,
    content: @Composable () -> Unit,
) {
    Column(
        horizontalAlignment = if (isSent) Alignment.End else Alignment.Start,
        modifier = modifier.fillMaxWidth(),
    ) {
        Surface(
            onClick = onClick
        ) {
            Box(
                modifier = Modifier
                    .padding(8.dp)
                    .drawBehind {
                        val hornPath = Path().apply {
                            if (isSent) {
                                moveTo(size.width - 20f, 0f)
                                lineTo(size.width + 4f, -10f)
                                lineTo(size.width - 5f, 10f)
                                close()
                            } else {
                                moveTo(20f, 0f)
                                lineTo(-4f, -10f)
                                lineTo(5f, 10f)
                                close()
                            }
                        }
                        drawPath(path = hornPath, color = bubbleColor)
                    }
                    .background(bubbleColor, RoundedCornerShape(16.dp))
                    .padding(horizontal = 12.dp, vertical = 8.dp),
            ) {
                content()
            }
        }


        Text(
            text = buildAnnotatedString {
                if (timestamp != null) {
                    append(timestamp)
                }
                if (pointsEarned != null) {
                    if (timestamp != null) append(" • ")
                    withStyle(
                        SpanStyle(
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.ExtraBold,
                        ),
                    ) {
                        append("+$pointsEarned pts")
                    }
                }
            },
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
        )
    }

}

@Composable
fun TextBubble(
    modifier: Modifier = Modifier,
    isSent: Boolean,
    text: String,
    isError: Boolean = false,
    timestamp: String? = null,
    pointsEarned: Int? = null,
    textColor: Color = if (isSent) MaterialTheme.colorScheme.onSurface else if (isError) MaterialTheme.colorScheme.onErrorContainer else MaterialTheme.colorScheme.inverseOnSurface,
) {
    TextBubble(
        isSent = isSent,
        annotatedText = AnnotatedString(text),
        modifier = modifier,
        timestamp = timestamp,
        pointsEarned = pointsEarned,
        textColor = textColor,
        isError = isError,
    )
}

@Composable
fun TextBubble(
    modifier: Modifier = Modifier,
    isSent: Boolean,
    isError: Boolean = false,
    timestamp: String? = null,
    pointsEarned: Int? = null,
    annotatedText: AnnotatedString,
    textColor: Color = if (isSent) MaterialTheme.colorScheme.onSurface else if (isError) MaterialTheme.colorScheme.onErrorContainer else MaterialTheme.colorScheme.inverseOnSurface,
) {
    ChatBubble(
        modifier = modifier,
        isSent = isSent,
        timestamp = timestamp,
        pointsEarned = pointsEarned,
        isError = isError,
    ) {
        Text(
            text = annotatedText,
            color = textColor,
        )
    }
}

@Composable
fun TypingDots() {
    val transition = rememberInfiniteTransition(label = "typing-dots")
    val bubbleColor = MaterialTheme.colorScheme.inverseOnSurface

    Row(
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        repeat(3) { index ->
            val offsetY by transition.animateFloat(
                initialValue = 0f,
                targetValue = -6f, // bounce height
                animationSpec = infiniteRepeatable(
                    animation = tween(
                        durationMillis = 500,
                        delayMillis = index * 150,
                        easing = LinearOutSlowInEasing,
                    ),
                    repeatMode = RepeatMode.Reverse,
                ),
                label = "dot-offset-$index",
            )

            Box(
                modifier = Modifier
                    .offset(y = offsetY.dp)
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(bubbleColor),
            )
        }
    }
}

@Composable
fun TypingBubble(
    modifier: Modifier = Modifier,
) {
    ChatBubble(
        modifier = modifier,
        isSent = false,
    ) {
        TypingDots()
    }
}

@Preview
@Composable
fun ChatBubblePreview(
    @PreviewParameter(ThemeModeProvider::class) isDark: Boolean,
) {
    ThemedPreviewTemplate(isDark) {
        Column {
            TextBubble(text = "Hey! How's it going?", isSent = false, timestamp = "10:00 AM")
            TextBubble(text = "Doing great", isSent = true, timestamp = "10:30 AM", pointsEarned = 2)
            TypingBubble()
        }
    }
}
