import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.round

/**
 *
 *This is an experimental UI to draw quadrilaterals over an image.
 *
 * TODO:
 * 1. use a proper view model for drawn objets
 * 2. Improve drag and drop behaviour
 * 3. Split components into smaller units
 * .
 */
@Composable
fun TagPane(bitmap: ImageBitmap, btnZoomFactor: Float = 1.5f) {

    var viewOffset by remember { mutableStateOf(Offset(0f, 0f)) }
    var viewScale  by remember { mutableStateOf(1f) }

    // TODO: adapt image transformation to target platform.
    // Android based app would manage natively zoom and rotation.
    // However, on desktop with standard mouse control, only panning is working here.
    val transformationState = rememberTransformableState { zoomChange, panChange, _ ->
        viewScale *= zoomChange
        viewOffset += panChange
    }

    Column {
        val points = remember { mutableStateListOf<Offset>() }

        Box(Modifier.aspectRatio(bitmap.width.toFloat() / bitmap.height)) {
            val canvasModifier = Modifier
                .fillMaxSize()
                .graphicsLayer(
                    scaleX = viewScale,
                    scaleY = viewScale,
                    translationX = viewOffset.x,
                    translationY = viewOffset.y
                )
                .pointerInput("Draw corners") { detectTapGestures(onDoubleTap = { points.add(it); }) }
                .transformable(transformationState)
                .border(4.dp, Color.Yellow)
            Box(canvasModifier) {
                Image(bitmap, "Annotated image", Modifier.fillMaxSize())

                Canvas(Modifier.fillMaxSize().border(2.dp, Color.Red)) {
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

                for ((i, pt) in points.withIndex()) Point(pt) { points[i] = it }
            }
        }

            // Navigation bar. Required on Desktop because zoom gesture is not available (or is it with touchscreen/touchpad ?)
            Row(Modifier.graphicsLayer(alpha = 0.7f)) {
                Button( { viewScale *= btnZoomFactor } ) { Text("\uD83D\uDD0D+") }
                Button( { viewScale /= btnZoomFactor } ) { Text("\uD83D\uDD0D-") }
            }
    }
}


@Composable
private fun Point(position: Offset, ptSize: Float = 10f, positionUpdate: (Offset) -> Unit) {
    var currentOffset by mutableStateOf(Offset(0F, 0F))
    val circleModifier = Modifier
        .clip(CircleShape)
        .background(Color.Green)
        .size(ptSize.dp)
        .pointerInput("Move point") { detectDragGestures(onDragEnd = {positionUpdate(position + currentOffset)}) { change, dragAmount ->
            if (!change.isConsumed) {
                change.consume()
                currentOffset += dragAmount
            }
        }}
    val positioningModifier = Modifier.offset { (position + currentOffset - Offset(ptSize/2f, ptSize/2f)).round() }
    Box(positioningModifier){
        Box(circleModifier)
    }
}