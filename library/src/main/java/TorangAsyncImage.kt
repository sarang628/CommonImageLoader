import androidx.annotation.DrawableRes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.sryang.library.R
import kotlinx.coroutines.launch

@Composable
fun TorangAsyncImage(
    model: Any?,
    modifier: Modifier,
    progressSize: Dp = 50.dp,
    errorIconSize: Dp = 50.dp,
    @DrawableRes previewPlaceHolder: Int? = null
) {
    var state by remember { mutableStateOf(0) }
    val coroutine = rememberCoroutineScope()

    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        AnimatedVisibility(
            visible = true,
            modifier.fillMaxSize()
        ) {
            Box {
                if (LocalInspectionMode.current) {
                    Image(
                        painter =
                        painterResource(id = previewPlaceHolder ?: R.drawable.ic_loading),
                        contentDescription = "",
                        modifier = Modifier
                            .align(Alignment.Center)
                            .size(progressSize)
                    )
                } else if (state == 0) {
                    CircularProgressIndicator(
                        strokeWidth = 2.dp,
                        strokeCap = StrokeCap.Round,
                        color = Color.LightGray,
                        modifier = Modifier
                            .align(Alignment.Center)
                            .size(progressSize)
                    )
                } else if (state == 1) {
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(model)
                            .crossfade(true)
                            .build(),
                        contentDescription = "",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                } else if (state == 2) {
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(R.drawable.ic_connection_error)
                            .crossfade(true)
                            .build(),
                        contentDescription = "",
                        modifier = Modifier
                            .size(errorIconSize)
                            .align(Alignment.Center)
                    )
                }
            }
        }


        if (state == 0) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(model)
                    .build(),
                onSuccess = {
                    coroutine.launch {
                        //delay(2000)
                        state = 1 // success
                    }
                },
                onError = {
                    coroutine.launch {
                        //delay(2000)
                        state = 2 // failed
                    }
                },
                contentDescription = ""
            )
        }
    }

}

@Preview
@Composable
private fun PreviewTorangAsyncImage() {
    TorangAsyncImage(
        model = "http://sarang628.iptime.org:89/restaurants/1-1.jpeg",
//        model = "",
//        model = R.drawable.loading_img,
        Modifier.size(450.dp),
        progressSize = 30.dp,
        errorIconSize = 30.dp
    )
}