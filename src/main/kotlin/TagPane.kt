import androidx.compose.foundation.Canvas
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Button
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp

@Composable
fun TagPane(bitmap: ImageBitmap, btnZoomFactor: Float = 1.5f) {

    var viewOffset by remember { mutableStateOf(Offset(0f, 0f)) }
    var viewScale  by remember { mutableStateOf(1f) }

    val points = remember { mutableStateListOf<Offset>() }

    // TODO: adapt image transformation to target platform.
    // Android based app would manage natively zoom and rotation.
    // However, on desktop with standard mouse control, only panning is working here.
    val transformationState = rememberTransformableState { zoomChange, panChange, _ ->
        viewScale *= zoomChange
        viewOffset += panChange
    }

    Column {
        Surface {
            Canvas(
                Modifier
                    .fillMaxSize()
                    .graphicsLayer(scaleX=viewScale, scaleY = viewScale, translationX=viewOffset.x, translationY = viewOffset.y)
                    .pointerInput("Draw corners") { detectTapGestures(onDoubleTap = { points.add(it) ; }) }
                    .transformable(transformationState)
                    .border(2.dp, Color.Red)
            ) {

                drawImage(bitmap, dstSize = IntSize(size.width.toInt(), size.height.toInt()))
                for (pt in points) drawCircle(Color.Green, radius = 5f, center = pt)
                points.windowed(4, 4, partialWindows = true).forEach { group ->
                    val groupPts = group.iterator()
                    if (!groupPts.hasNext()) return@forEach
                    var lineStart = groupPts.next()
                    while (groupPts.hasNext()) {
                        val lineEnd = groupPts.next()
                        drawLine(Color.Green, lineStart, lineEnd)
                        lineStart = lineEnd
                    }
                    if (group.size == 4) drawLine(Color.Green, lineStart, group[0])
                }
            }

            // Navigation bar. Required on Desktop because zoom gesture is not available (or is it with touchscreen/touchpad ?)
            Row(Modifier.graphicsLayer(alpha = 0.7f)) {
                Button( { viewScale *= btnZoomFactor } ) { Text("\uD83D\uDD0D+") }
                Button( { viewScale /= btnZoomFactor } ) { Text("\uD83D\uDD0D-") }
            }
        }
    }
}