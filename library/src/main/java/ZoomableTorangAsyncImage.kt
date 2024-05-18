import ZoomableImageDimens.MAGNIFICATION_BASELINE
import ZoomableImageDimens.MAGNIFICATION_SCALE_DEFAULT
import ZoomableImageDimens.MAGNIFICATION_THRESHOLD
import ZoomableImageDimens.OFFSET_X_BASELINE
import ZoomableImageDimens.OFFSET_Y_BASELINE
import ZoomableImageDimens.ROTATION_BASELINE
import android.util.Log
import androidx.annotation.DrawableRes
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.MutatePriority
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.gestures.ScrollableState
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.calculatePan
import androidx.compose.foundation.gestures.calculateRotation
import androidx.compose.foundation.gestures.calculateZoom
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.GraphicsLayerScope
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.sryang.library.R
import kotlinx.coroutines.Job
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min


@Composable
fun ZoomableTorangAsyncImage(
    model: Any?,
    modifier: Modifier,
    progressSize: Dp = 50.dp,
    errorIconSize: Dp = 50.dp,
    contentScale: ContentScale = ContentScale.Fit,
    onEdge: ((Boolean) -> Unit)? = null,
    @DrawableRes previewPlaceHolder: Int? = null,
) {

    ZoomableImage(modifier, onEdge = onEdge) { modifier ->
        TorangAsyncImage(
            model = model,
            modifier = modifier,
            contentScale = contentScale,
            previewPlaceHolder = previewPlaceHolder,
            errorIconSize = errorIconSize,
            progressSize = progressSize,
        )
    }

}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ZoomableImage(
    modifier: Modifier = Modifier,
    animateSnapBack: Boolean = true,
    containerModifier: Modifier = Modifier, //for box
    magnificationScale: Float = MAGNIFICATION_SCALE_DEFAULT,
    onZoomModeChanged: ((Boolean) -> Unit)? = null,
    scrollState: ScrollableState? = null,
    snapBack: Boolean = false,
    supportRotation: Boolean = false,
    onEdge: ((Boolean) -> Unit)? = null,
    compose: @Composable (Modifier) -> Unit,
) {
    val context = LocalContext.current

    val displayMetrics = context.resources.displayMetrics

    //Width And Height Of Screen
    val width = displayMetrics.widthPixels
    val height = displayMetrics.heightPixels

    //Device Density
    val density = displayMetrics.density

    var lastOffsetX by remember { mutableFloatStateOf(0f) }
    var lastOffsetY by remember { mutableFloatStateOf(0f) }

    var isDrag by remember { mutableStateOf(false) }

    var job: Job? = null
    var job1: Job? = null

    var scale by remember { mutableFloatStateOf(MAGNIFICATION_BASELINE) }
    var rotation by remember { mutableFloatStateOf(ROTATION_BASELINE) }
    var offsetX by remember { mutableFloatStateOf(OFFSET_X_BASELINE) }
    var offsetY by remember { mutableFloatStateOf(OFFSET_Y_BASELINE) }

    // for animating scale, rotation and translation back to its original state
    var isInGesture by remember { mutableStateOf(false) }
    val snapBackScale by animateFloatAsState(
        if (isInGesture) scale else MAGNIFICATION_BASELINE,
        label = "LABEL_ZOOMABLE_IMG_SNAP_BACK_SCALE"
    )
    val snapBackRotation by animateFloatAsState(
        if (isInGesture) rotation else ROTATION_BASELINE,
        label = "LABEL_ZOOMABLE_IMG_SNAP_BACK_ROTATION"
    )
    val snapBackOffsetX by animateFloatAsState(
        if (isInGesture) offsetX else OFFSET_X_BASELINE,
        label = "LABEL_ZOOMABLE_IMG_SNAP_BACK_OFFSET_X"
    )
    val snapBackOffsetY by animateFloatAsState(
        if (isInGesture) offsetY else OFFSET_Y_BASELINE,
        label = "LABEL_ZOOMABLE_IMG_SNAP_BACK_OFFSET_Y"
    )

    val coroutineScope = rememberCoroutineScope()


    Box(
        modifier = containerModifier
            .combinedClickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = {
                },
                onDoubleClick = {
                    if (isDrag)
                        return@combinedClickable

                    if (scale >= MAGNIFICATION_THRESHOLD) {
                        onZoomModeChanged?.invoke(false)
                        scale = MAGNIFICATION_BASELINE
                        offsetX = OFFSET_X_BASELINE
                        offsetY = OFFSET_Y_BASELINE
                    } else {
                        scale = magnificationScale
                    }
                },
            )
            .pointerInput(Unit) {
                awaitEachGesture {
                    awaitFirstDown()
                    do {
                        isInGesture = true

                        val event = awaitPointerEvent()
                        scale *= event.calculateZoom()
                        if (scale > MAGNIFICATION_BASELINE) {
                            scrollState?.run {
                                coroutineScope.launch {
                                    setScrolling(false)
                                }
                            }

                            onZoomModeChanged?.invoke(true)

                            val edgeOffset = (
                                    (size.width * if (scale > magnificationScale) magnificationScale else scale) // size.width * scale
                                            - width
                                    ) / 2

                            val pan = event.calculatePan()
                            if (abs(offsetX + pan.x) < edgeOffset) {
                                offsetX += pan.x
                                job?.cancel()
                                job = coroutineScope.launch {
                                    delay(300)
                                    lastOffsetX = offsetX
                                }
                            }

                            if (max(lastOffsetX, offsetX) - min(
                                    lastOffsetX,
                                    offsetX
                                ) > 50
                            ) {
                                if (!isDrag) {
                                    isDrag = true
                                }

                                job1?.cancel()
                                job1 = coroutineScope.launch {
                                    delay(500)
                                    isDrag = false
                                }
                            }

                            onEdge?.invoke(
                                !(abs(offsetX + pan.x) < (edgeOffset - 10))
                            )

                            if (abs(offsetY + pan.y) < (
                                        (size.height * if (scale > magnificationScale) magnificationScale else scale) // size.width * scale
                                                - height // - size
                                        ) / 2
                            ) {
                                offsetY += pan.y
                            }

                            rotation += event.calculateRotation()

                            scrollState?.run {
                                coroutineScope.launch {
                                    setScrolling(true)
                                }
                            }
                        } else if (!snapBack) {
                            // for the no snap back use case, the image should shift back into its container when it gets close enough to the original position
                            onZoomModeChanged?.invoke(false)
                            scale = MAGNIFICATION_BASELINE
                            offsetX = OFFSET_X_BASELINE
                            offsetY = OFFSET_Y_BASELINE
                            rotation = ROTATION_BASELINE
                        }
                    } while (event.changes.any { it.pressed })

                    // Gesture complete actions
                    if (snapBack) {
                        onZoomModeChanged?.invoke(false)

                        isInGesture = false

                        scale = MAGNIFICATION_BASELINE
                        offsetX = OFFSET_X_BASELINE
                        offsetY = OFFSET_Y_BASELINE
                        rotation = ROTATION_BASELINE
                    }
                }
            }
    ) {
        fun GraphicsLayerScope.manipulateImage() {
            if (!isInGesture && animateSnapBack) {
                scaleX = snapBackScale
                scaleY = snapBackScale
                if (supportRotation) {
                    rotationZ = snapBackRotation
                }
                translationX = snapBackOffsetX
                translationY = snapBackOffsetY
            } else {
                scaleX = maxOf(MAGNIFICATION_BASELINE, minOf(magnificationScale, scale))
                scaleY = maxOf(MAGNIFICATION_BASELINE, minOf(magnificationScale, scale))
                if (supportRotation) {
                    rotationZ = rotation
                }
                translationX = offsetX
                translationY = offsetY
            }
        }

        compose.invoke(modifier
            .graphicsLayer { manipulateImage() })
    }
}

suspend fun ScrollableState.setScrolling(value: Boolean) {
    scroll(scrollPriority = MutatePriority.PreventUserInput) {
        when (value) {
            true -> Unit
            else -> awaitCancellation()
        }
    }
}

@Immutable
object ZoomableImageDimens {
    internal const val OFFSET_X_BASELINE = 1f
    internal const val OFFSET_Y_BASELINE = 1f
    internal const val MAGNIFICATION_BASELINE = 1f
    internal const val MAGNIFICATION_SCALE_DEFAULT = 3f
    internal const val ROTATION_BASELINE = 0f
    internal const val MAGNIFICATION_THRESHOLD = 2f
}


@Preview
@Composable
fun ZoomableTorangAsyncImagePreview() {
    Box(
        modifier = Modifier
            .background(Color.Gray)
            .size(200.dp)
    ) {
        ZoomableTorangAsyncImage(
            model = "https://artrkl.com/cdn/shop/articles/thecreationofadam-1690035964350_d2d6280f-ed1d-465e-ad42-0ea0bbbcefde.webp?v=1690563054&width=1100",
            modifier = Modifier
                .size(400.dp)
                .align(Alignment.Center),
            contentScale = ContentScale.Crop
        )

        Image(
            modifier = Modifier.scale(2.0f),
            painter = painterResource(id = com.sryang.library.R.drawable.loading_img),
            contentDescription = ""
        )
    }
}