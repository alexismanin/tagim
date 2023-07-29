import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.layout.Row
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.graphicsLayer

@Composable
fun TagPane(bitmap: ImageBitmap, btnZoomFactor: Float = 1.5f) {
    var imgOffset by remember { mutableStateOf(Offset(0f, 0f)) }
    var imgScale by remember { mutableStateOf(1f) }
    // TODO: adapt image transformation to target platform.
    // Android based app would manage natively zoom and rotation.
    // However, on desktop with standard mouse control, only panning is working here.
    val transformationState = rememberTransformableState { zoomChange, panChange, rotationChange ->
        imgScale *= zoomChange
        imgOffset += panChange
    }

    Image(
        bitmap, "Image to tag",
        Modifier.graphicsLayer(scaleX = imgScale, scaleY = imgScale,
                               translationX = imgOffset.x, translationY = imgOffset.y)
                .transformable(state = transformationState)
    )

    Row(Modifier.graphicsLayer(alpha = 0.7f)) {
        Button( { imgScale *= btnZoomFactor } ) { Text("\uD83D\uDD0D+") }
        Button( { imgScale /= btnZoomFactor } ) { Text("\uD83D\uDD0D-") }
    }
}