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
package ke.don.feature_share.models

import android.content.Context
import android.graphics.Picture
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import ke.don.core_datasource.remote.RemoteConfigManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SharableViewModel @Inject constructor() : ViewModel() {
    private val _uiState = MutableStateFlow(SharableUiState())
    val uiState: StateFlow<SharableUiState> = _uiState

    fun handleIntent(intent: SharableIntentHandler) {
        when (intent) {
            is SharableIntentHandler.CaptureScreen -> captureScreen(intent.picture, context = intent.context, channel = intent.channel)

            is SharableIntentHandler.ShareImage -> {}
        }
    }

    fun updateState(transform: (SharableUiState) -> SharableUiState) {
        _uiState.update(transform)
    }

    fun captureScreen(
        picture: Picture,
        context: Context,
        channel: Channel
    ) {
        viewModelScope.launch {
            updateState { it.copy(isLoading = true) }
            val currentUri = uiState.value.imageUri

            val uri = currentUri ?: run {
                val bitmap = createBitmapFromPicture(picture)
                val newUri = bitmap.saveToDisk(context)
                updateState { it.copy(imageUri = newUri) }
                newUri
            }

            updateState { it.copy(isLoading = false) }

            shareScreen(uri, context, channel)

            clearUri()
        }
    }

    fun shareScreen(
        uri: Uri,
        context: Context,
        channel: Channel
    ) {
        val caption = RemoteConfigManager.getString("store_link")

        val annotatedCaption = buildString {
            append("Join the game\n\n")
            append(caption)
        }

        when(channel){
            Channel.Whatsapp -> shareToWhatsApp(context, uri, annotatedCaption)
            Channel.Instagram -> shareToInstagram(context, uri, annotatedCaption)
            Channel.Twitter -> shareToTwitter(context, uri, annotatedCaption)
            Channel.More -> shareBitmap(context, uri, annotatedCaption)

        }
    }

    fun clearUri() {
        updateState { it.copy(imageUri = null) }
    }
}
