package ke.don.feature_share.components

import android.Manifest
import android.graphics.Bitmap
import android.graphics.Picture
import android.os.Build
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.graphics.Canvas
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.drawscope.draw
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.node.Ref
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import ke.don.core_designsystem.material_theme.components.WBIcon
import ke.don.core_designsystem.material_theme.ui.theme.ThemeModeProvider
import ke.don.core_designsystem.material_theme.ui.theme.ThemedPreviewTemplate
import ke.don.feature_share.models.Channel
import ke.don.feature_share.models.SharableIntentHandler
import ke.don.feature_share.models.SharableScreenModel
import ke.don.feature_share.models.SharableUiState
import kotlinx.coroutines.launch


@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun ShareFrameLayout(
    modifier: Modifier = Modifier,
    title: String = "Share this",
    state: SharableUiState,
    snackbarHostState: SnackbarHostState,
    handleIntent: (SharableIntentHandler) -> Unit,
    content: @Composable () -> Unit,
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val picture = remember { Picture() }

    val writeStorageAccessState = rememberMultiplePermissionsState(
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            // No permissions are needed on Android 10+ to add files in the shared storage
            emptyList()
        } else {
            listOf(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        },
    )

    fun shareBitmap(
        channel: Channel
    ) {
        coroutineScope.launch {
            if (writeStorageAccessState.allPermissionsGranted) {
                handleIntent(SharableIntentHandler.CaptureScreen(picture, context, channel))
            } else if (writeStorageAccessState.shouldShowRationale) {
                val result = snackbarHostState.showSnackbar(
                    message = "The storage permission is needed to save the image",
                    actionLabel = "Grant Access",
                )

                if (result == SnackbarResult.ActionPerformed) {
                    writeStorageAccessState.launchMultiplePermissionRequest()
                }
            } else {
                writeStorageAccessState.launchMultiplePermissionRequest()
            }
        }
    }
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // ✅ This is the part that will be captured
        Box(
            modifier = Modifier
                .padding(8.dp)
                .weight(1f)
                .clip(RoundedCornerShape(16.dp))
                .background(MaterialTheme.colorScheme.surface)
                .drawWithCache {
                    // Example that shows how to redirect rendering to an Android Picture and then
                    // draw the picture into the original destination
                    val width = this.size.width.toInt()
                    val height = this.size.height.toInt()
                    onDrawWithContent {
                        val pictureCanvas =
                            Canvas(
                                picture.beginRecording(
                                    width,
                                    height,
                                ),
                            )
                        draw(this, this.layoutDirection, pictureCanvas, this.size) {
                            this@onDrawWithContent.drawContent()
                        }
                        picture.endRecording()

                        drawIntoCanvas { canvas -> canvas.nativeCanvas.drawPicture(picture) }
                    }
                },
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.surface),
            ) {
                content()
            }

        }

        Spacer(modifier = Modifier.height(24.dp))

        // 🔄 Share options
        ShareRow(modifier, { shareBitmap(it) })
    }
}

@Composable
fun ShareRow(
    modifier: Modifier = Modifier,
    shareBitmap: (Channel) -> Unit
){
    Column(
        verticalArrangement = Arrangement.spacedBy(32.dp, Alignment.Top),
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.fillMaxWidth()
    ) {
        Text(
            text = "Share with",
            style = MaterialTheme.typography.titleLarge,
            textAlign = TextAlign.Center
        )

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(Channel.entries) {
                ChannelIcon(
                    channel = it,
                    shareBitmap = shareBitmap
                )
            }
        }
    }
}

@Composable
fun ChannelIcon(
    modifier: Modifier = Modifier,
    channel: Channel,
    shareBitmap: (Channel) -> Unit
){
    val iconSize = 32.dp
    Column(
        modifier = modifier.padding(4.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Surface(
            onClick = {shareBitmap(channel)},
            shape = MaterialTheme.shapes.large,
            tonalElevation = 8.dp,
            modifier = modifier.wrapContentSize()
        ) {
            when (channel){
                in listOf(Channel.Whatsapp, Channel.Instagram, Channel.Twitter) -> {
                    WBIcon(
                        iconResource = channel.icon!!,
                        size = iconSize,
                        modifier = Modifier
                            .padding(8.dp)
                    )
                }
                else -> {
                    Icon(
                        imageVector = Icons.Default.MoreHoriz,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier
                            .padding(8.dp)
                            .size(iconSize)
                    )

                }

            }

        }

        Text(
            text = channel.text.ifEmpty { channel.name },
            style = MaterialTheme.typography.labelSmall,
            textAlign = TextAlign.Center
        )
    }
}

@Preview
@Composable
fun ChannelIconPreview(
    @PreviewParameter(ThemeModeProvider::class) isDark : Boolean
){
    ThemedPreviewTemplate(isDark) {
        ChannelIcon(
            channel = Channel.Whatsapp,
            shareBitmap = {}
        )
    }
}
@Preview
@Composable
fun ShareRowPreview(
    @PreviewParameter(ThemeModeProvider::class) isDark : Boolean
){
    ThemedPreviewTemplate(isDark) {
        ShareRow(
            shareBitmap = {}
        )
    }
}