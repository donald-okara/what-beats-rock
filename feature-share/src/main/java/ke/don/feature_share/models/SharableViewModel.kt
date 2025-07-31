package ke.don.feature_share.models

import android.content.Context
import android.graphics.Picture
import android.net.Uri
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionContext
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.MultiplePermissionsState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SharableViewModel @Inject constructor() : ViewModel() {
    private val _uiState = MutableStateFlow(SharableUiState())
    val uiState: StateFlow<SharableUiState> = _uiState

    fun handleIntent(intent: SharableIntentHandler){
        when(intent){
            is SharableIntentHandler.CaptureScreen -> captureScreen(intent.picture, context = intent.context)

            is SharableIntentHandler.ShareImage -> {}
        }
    }

    fun updateState(transform: (SharableUiState) -> SharableUiState) {
        _uiState.update ( transform )
    }

    fun captureScreen(
        picture: Picture,
        context: Context,
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

            shareScreen(uri,context)
        }
    }


    fun shareScreen(
        uri: Uri,
        context: Context,
    ) {
        shareBitmap(context, uri)
    }

}