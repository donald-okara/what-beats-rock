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

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import ke.don.core_designsystem.R

@Composable
fun ConfirmationDialog(
    modifier: Modifier = Modifier,
    onDismissRequest: () -> Unit,
    onConfirmation: () -> Unit,
    dialogTitle: String,
    dialogText: String,
    dialogType: DialogType = DialogType.NEUTRAL,
    icon: ImageVector,
    enabled: Boolean = true,
) {
    val onContainerColor = when (dialogType) {
        DialogType.WARNING -> MaterialTheme.colorScheme.primary
        DialogType.DANGER -> MaterialTheme.colorScheme.error
        else -> MaterialTheme.colorScheme.secondary
    }

    val disabledColor = onContainerColor.copy(alpha = 0.6f)

    AlertDialog(
        shape = MaterialTheme.shapes.medium,
        containerColor = MaterialTheme.colorScheme.surface,
        icon = {
            Icon(
                imageVector = icon,
                contentDescription = dialogText,
                tint = onContainerColor,
            )
        },
        title = { Text(text = dialogTitle) },
        text = { Text(text = dialogText) },
        onDismissRequest = onDismissRequest,
        confirmButton = {
            TextButton(onClick = onConfirmation, enabled = enabled) {
                Text(
                    text = stringResource(R.string.i_understand),
                    color = if (enabled) onContainerColor else disabledColor,
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismissRequest) {
                Text(
                    text = stringResource(R.string.dismiss),
                    color = onContainerColor,
                )
            }
        },
        modifier = modifier,
    )
}

@Composable
fun ConfirmationDialogWithChecklist(
    modifier: Modifier = Modifier,
    onDismissRequest: () -> Unit,
    onConfirmation: () -> Unit,
    dialogTitle: String,
    dialogText: String,
    checklistItems: List<String>,
    dialogType: DialogType = DialogType.NEUTRAL,
    icon: ImageVector,
) {
    val onContainerColor = when (dialogType) {
        DialogType.WARNING -> MaterialTheme.colorScheme.primary
        DialogType.DANGER -> MaterialTheme.colorScheme.error
        else -> MaterialTheme.colorScheme.secondary
    }

    val checklistState = remember {
        mutableStateListOf(*List(checklistItems.size) { false }.toTypedArray())
    }

    val allChecked = checklistState.all { it }

    AlertDialog(
        shape = MaterialTheme.shapes.medium,
        containerColor = MaterialTheme.colorScheme.surfaceVariant,
        icon = {
            Icon(
                imageVector = icon,
                contentDescription = dialogTitle,
                tint = onContainerColor,
            )
        },
        title = { Text(text = dialogTitle) },
        text = {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 500.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                stickyHeader {
                    Surface(
                        color = MaterialTheme.colorScheme.surface,
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(MaterialTheme.colorScheme.surface), // Sticky headers can look odd if not re-painted
                    ) {
                        Text(
                            text = dialogText,
                            style = MaterialTheme.typography.bodyMedium,
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp),
                        )
                    }
                }

                itemsIndexed(checklistItems) { index, item ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { checklistState[index] = !checklistState[index] },
                    ) {
                        Checkbox(
                            checked = checklistState[index],
                            onCheckedChange = { checklistState[index] = it },
                            colors = CheckboxDefaults.colors(checkedColor = onContainerColor),
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = item)
                    }
                }
            }
        },
        onDismissRequest = onDismissRequest,
        confirmButton = {
            TextButton(
                onClick = onConfirmation,
                enabled = allChecked,
            ) {
                Text(
                    text = stringResource(R.string.i_understand),
                    color = if (allChecked) onContainerColor else MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismissRequest) {
                Text(
                    text = stringResource(R.string.dismiss),
                    color = onContainerColor,
                )
            }
        },
        modifier = modifier,
    )
}

enum class DialogType {
    NEUTRAL,
    INFO,
    WARNING,
    DANGER,
}
