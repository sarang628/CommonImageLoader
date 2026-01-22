import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.SubcomposeAsyncImage
import coil.request.ImageRequest
import com.sryang.library.R

@Composable
fun TorangAsyncImage(
    modifier            : Modifier      = Modifier,
    model               : Any?          = "",
    progressSize        : Dp            = 50.dp,
    errorIconSize       : Dp            = 50.dp,
    contentScale        : ContentScale  = ContentScale.Fit,
    contentDescription  : String?       = null,
) {
    SubcomposeAsyncImage(
        modifier = modifier,
        model = ImageRequest.Builder(LocalContext.current)
                            .data(model)
                            .crossfade(300)
                            .build(),
        contentDescription = contentDescription,
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