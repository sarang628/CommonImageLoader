import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.compose.SubcomposeAsyncImage
import coil.request.ImageRequest
import com.sryang.library.R
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

enum class ImageState {
    LOADING,
    SUCCESS,
    FAILED
}

/**
 * @param model Either an [ImageRequest] or the [ImageRequest.data] value.
 */
@Composable
fun TorangAsyncImage(
    model                           : Any?          = "",
    modifier                        : Modifier      = Modifier,
    progressSize                    : Dp            = 50.dp,
    errorIconSize                   : Dp            = 50.dp,
    contentScale                    : ContentScale  = ContentScale.Fit,
    contentDescription              : String        = "",
    @DrawableRes previewPlaceHolder : Int?          = null,
) {
    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        if (LocalInspectionMode.current) {
            Image(modifier = Modifier.align(Alignment.Center)
                                     .size(progressSize),
                painter = painterResource(id = previewPlaceHolder ?: R.drawable.ic_loading),
                contentDescription = contentDescription,
            )
        } else
        {
            SubcomposeAsyncImage(
                modifier = Modifier.fillMaxSize(),
                model = ImageRequest.Builder(LocalContext.current)
                    .data(model)
                    .crossfade(300)
                    .build(),
                contentDescription = null,
                contentScale = contentScale,
                loading = {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            strokeWidth = 2.dp,
                            modifier = Modifier.size(progressSize)
                        )
                    }
                },
                error = {
                    Box(Modifier.fillMaxSize()){
                        Image(
                            modifier = Modifier.size(errorIconSize).align(Alignment.Center),
                            painter = painterResource(R.drawable.ic_connection_error),
                            contentDescription = null
                        )
                    }
                }
            )
        }
    }
}

