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
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.launch
import kotlin.math.abs


@Composable
fun ZoomableTorangAsyncImage(
    model: Any?,
    modifier: Modifier,
    progressSize: Dp = 50.dp,
    errorIconSize: Dp = 50.dp,
    contentScale: ContentScale = ContentScale.Fit,
    @DrawableRes previewPlaceHolder: Int? = null,
) {

    ZoomableImage(modifier) { modifier ->
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
    parentWidth: Float = 100f,
    parentHeight: Float = 100f,
    compose: @Composable (Modifier) -> Unit,
) {
    val context = LocalContext.current

    val displayMetrics = context.resources.displayMetrics

    //Width And Height Of Screen
    val width = displayMetrics.widthPixels
    val height = displayMetrics.heightPixels

    //Device Density
    val density = displayMetrics.density

    Log.d("__sryang", "screen height = ${height}")

    val coroutineScope = rememberCoroutineScope()

    var scale by remember { mutableStateOf(MAGNIFICATION_BASELINE) }
    var rotation by remember { mutableStateOf(ROTATION_BASELINE) }
    var offsetX by remember { mutableStateOf(OFFSET_X_BASELINE) }
    var offsetY by remember { mutableStateOf(OFFSET_Y_BASELINE) }
    var isDrag by remember { mutableStateOf(false) }

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

    Box(
        modifier = containerModifier
            .onSizeChanged {
                Log.d("__sryang", "onSizeChanged: $it")
            }
            .combinedClickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = { },
                onDoubleClick = {
                    if (!isDrag)
                        if (scale >= MAGNIFICATION_THRESHOLD) {
                            onZoomModeChanged?.invoke(false)
                            scale = MAGNIFICATION_BASELINE
                            offsetX = OFFSET_X_BASELINE
                            offsetY = OFFSET_Y_BASELINE
                        } else {
                            if (!isDrag)
                                scale = magnificationScale
                        }
                },
            )
            .pointerInput(Unit) {
                detectHorizontalDragGestures(
                    onDragStart = {
                        isDrag = true
                    }, onDragEnd = {
                        isDrag = false
                    }, onHorizontalDrag = { change, dragAmount -> }
                )
            }
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

                            val pan = event.calculatePan()
                            if (abs(offsetX + pan.x) < (
                                        (size.width * if (scale > magnificationScale) magnificationScale else scale) // size.width * scale
                                                - width
                                        ) / 2
                            ) {
                                offsetX += pan.x
                            }

                            if (abs(offsetY + pan.y) < (
                                        (size.height * if (scale > magnificationScale) magnificationScale else scale) // size.width * scale
                                                - height // - size
                                        ) / 2
                            )
                                offsetY += pan.y

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

            Log.d("__sryang", "size = $size")
            Log.d("__sryang", "scaled size = ${size.height * scale} * ${size.width * scale}")
            Log.d("__sryang", "offsetY = $offsetY")

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