package ke.don.feature_share.models

import android.net.Uri
import ke.don.core_datasource.domain.models.PodiumProfile

data class SharableUiState(
    val imageUri: Uri? = null,
    val isLoading: Boolean = false
)
