package com.sryang.commonimageloader

import TorangAsyncImage
import ZoomableTorangAsyncImage
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.sarang.torang.di.image.ImageLoadData
import com.sarang.torang.di.image.ProvideZoomableTorangAsyncImage
import com.sarang.torang.di.pinchzoom.PinchZoomImageBox
import com.sarang.torang.di.pinchzoom.isZooming
import com.sarang.torang.di.pinchzoom.rememberPichZoomState
import com.sryang.commonimageloader.ui.theme.CommonImageLoaderTheme
import coil.compose.AsyncImage


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
                        startDestination = "TorangAsyncImageTest"
                    ) {
                        composable("ZoomableTorangAsyncImageTest") {
                            ZoomableTorangAsyncImageTest()
                        }
                        composable("PinchZoomImageBoxTest") {
                            PinchZoomImageBoxTest()
                        }
                        composable("TorangAsyncImageTest"){
                            TorangAsyncImageTest()
                        }
                    }

                }
            }
        }
    }
}

@Composable
fun TorangAsyncImageTest(){
    var input by remember { mutableStateOf("http://sarang628.iptime.org:89/restaurant_images/311/2025-12-24/01_07_57_132.jpg") }
    var url  by remember { mutableStateOf("") }
    Box(Modifier.fillMaxSize()){
        Column {
            TorangAsyncImage(modifier = Modifier.size(200.dp),
                             model = url)
            AsyncImage(modifier = Modifier.size(200.dp),
                model = url,
                contentDescription = null)
        }

        Column(modifier = Modifier.fillMaxWidth()
                                  .align(Alignment.BottomCenter)) {
            TextField(value = input,
                      onValueChange = { input = it })
            Button(onClick = { url = input }) {
                Text("Load")
            }
        }

    }



}

@Composable
fun ZoomableTorangAsyncImageTest() {
    LazyColumn {
        items(10) {
            ZoomableTorangAsyncImage("https://artrkl.com/cdn/shop/articles/thecreationofadam-1690035964350_d2d6280f-ed1d-465e-ad42-0ea0bbbcefde.webp?v=1690563054&width=1100")
        }
    }
}

@Composable
fun PinchZoomImageBoxTest() {
    var zoomState = rememberPichZoomState()

    LaunchedEffect(zoomState) {
        snapshotFlow { zoomState.value.isZooming }.collect {
            Log.d("__PinchZoomImageBoxTest", "isZooming : $zoomState.isZooming")
        }
    }

    PinchZoomImageBox(zoomState){
        LazyColumn {
            items(100) {
                Box {
                    ProvideZoomableTorangAsyncImage(
                        onPress = { zoomState.value = it.copy(url = "https://artrkl.com/cdn/shop/articles/thecreationofadam-1690035964350_d2d6280f-ed1d-465e-ad42-0ea0bbbcefde.webp?v=1690563054&width=1100") },
                        data = ImageLoadData(
                            modifier = Modifier,
                            url = "https://artrkl.com/cdn/shop/articles/thecreationofadam-1690035964350_d2d6280f-ed1d-465e-ad42-0ea0bbbcefde.webp?v=1690563054&width=1100",
                            contentScale = ContentScale.Fit
                        )
                    )
                }
            }
        }
    }
}