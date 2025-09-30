package com.sryang.commonimageloader

import ZoomableTorangAsyncImage
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.sarang.torang.di.image.ImageLoadData
import com.sarang.torang.di.image.ZoomableTorangAsyncImage
import com.sarang.torang.di.image.provideZoomableTorangAsyncImage
import com.sarang.torang.di.pinchzoom.PinchZoomImageBox
import com.sarang.torang.di.pinchzoom.isZooming
import com.sarang.torang.di.pinchzoom.rememberPichZoomState
import com.sryang.commonimageloader.ui.theme.CommonImageLoaderTheme


class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalFoundationApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CommonImageLoaderTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    NavHost(
                        navController = rememberNavController(),
                        startDestination = "PinchZoomImageBoxTest"
                    ) {
                        composable("ZoomableTorangAsyncImageTest") {
                            ZoomableTorangAsyncImageTest()
                        }
                        composable("PinchZoomImageBoxTest") {
                            PinchZoomImageBoxTest()
                        }
                    }

                }
            }
        }
    }
}

@Composable
fun ZoomableTorangAsyncImageTest() {
    LazyColumn {
        items(10) {
            ZoomableTorangAsyncImage("https://artrkl.com/cdn/shop/articles/thecreationofadam-1690035964350_d2d6280f-ed1d-465e-ad42-0ea0bbbcefde.webp?v=1690563054&width=1100")
            ZoomableTorangAsyncImage("https://artrkl.com/cdn/shop/articles/thecreationofadam-1690035964350_d2d6280f-ed1d-465e-ad42-0ea0bbbcefde.webp?v=1690563054&width=1100")
            ZoomableTorangAsyncImage("https://artrkl.com/cdn/shop/articles/thecreationofadam-1690035964350_d2d6280f-ed1d-465e-ad42-0ea0bbbcefde.webp?v=1690563054&width=1100")
            ZoomableTorangAsyncImage("https://artrkl.com/cdn/shop/articles/thecreationofadam-1690035964350_d2d6280f-ed1d-465e-ad42-0ea0bbbcefde.webp?v=1690563054&width=1100")
            ZoomableTorangAsyncImage("https://artrkl.com/cdn/shop/articles/thecreationofadam-1690035964350_d2d6280f-ed1d-465e-ad42-0ea0bbbcefde.webp?v=1690563054&width=1100")
            ZoomableTorangAsyncImage("https://artrkl.com/cdn/shop/articles/thecreationofadam-1690035964350_d2d6280f-ed1d-465e-ad42-0ea0bbbcefde.webp?v=1690563054&width=1100")
        }
    }
}

@Composable
fun PinchZoomImageBoxTest() {
    val zoomState = rememberPichZoomState()

    LaunchedEffect(zoomState) {
        snapshotFlow { zoomState.isZooming }.collect {
            Log.d("__PinchZoomImageBoxTest", "isZooming : $zoomState.isZooming")
        }
    }

    PinchZoomImageBox(
        imageLoader = { modifier, url, contentScale ->
            provideZoomableTorangAsyncImage().invoke(
                ImageLoadData(
                    modifier = modifier,
                    url = url,
                    contentScale = contentScale ?: ContentScale.Fit
                )
            )
        },
        zoomState = zoomState
    ) { imageLoader ->
        Log.d("__PinchZoomImageBoxTest", "imageLoader : $imageLoader")
        LazyColumn {
            items(100) {
                Box {
                    imageLoader.invoke(
                        Modifier,
                        "https://artrkl.com/cdn/shop/articles/thecreationofadam-1690035964350_d2d6280f-ed1d-465e-ad42-0ea0bbbcefde.webp?v=1690563054&width=1100",
                        ContentScale.Crop,
                        100.dp
                    )
                    //Text(text = if (zoomState.isZooming) "isZooming" else "")
                }
            }
        }
    }
}