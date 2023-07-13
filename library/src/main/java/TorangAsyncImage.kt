import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.sryang.library.R
import kotlinx.coroutines.delay

@Composable
fun TorangAsyncImage(
    url: String,
    modifier: Modifier
) {
    val loadingDelay = 2000
    val rotate = remember { mutableStateOf(10f) }
    val state = remember { mutableStateOf(0) }
    val current = remember { mutableStateOf(System.currentTimeMillis()) }
    val triggerLoading = remember { mutableStateOf(false) }

    LaunchedEffect(key1 = "1", block = {
        while (loadingDelay > System.currentTimeMillis() - current.value) {
            delay(100)
            rotate.value = rotate.value + 30f
        }
        triggerLoading.value = true
    })

    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        if (state.value == 0) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(R.drawable.loading_img)
                    .crossfade(true)
                    .build(),
                contentDescription = "",
                modifier = Modifier
                    .size(50.dp)
                    .rotate(rotate.value),
            )
        } else if (state.value == 1) {

        } else if (state.value == 2) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(R.drawable.ic_connection_error)
                    .crossfade(true)
                    .build(),
                contentDescription = "",
                modifier = Modifier
                    .size(50.dp)
            )
        }
        if (triggerLoading.value) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(url)
                    .crossfade(true)
                    .build(),
                onSuccess = {
                    state.value = 1
                },
                onError = {
                    state.value = 2
                },
                contentDescription = "",
                contentScale = ContentScale.Crop,
                modifier = modifier
            )
        }
    }

}

@Preview
@Composable
private fun PreviewTorangAsyncImage() {
    TorangAsyncImage(
//        url = "http://sarang628.iptime.org:89/restaurants/1-1.jpeg",
        url = "",
        Modifier
            .fillMaxWidth()
            .fillMaxHeight()
    )
}