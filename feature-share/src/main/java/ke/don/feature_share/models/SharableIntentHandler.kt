package ke.don.feature_share.models

import android.content.Context
import android.graphics.Picture
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionContext

sealed class SharableIntentHandler {
    class CaptureScreen(
        val picture: Picture,
        val context: Context,
    ): SharableIntentHandler()

    class ShareImage(
        val context: Context,
    ): SharableIntentHandler()
}