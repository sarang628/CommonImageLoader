package com.sryang.commonimageloader

import TorangAsyncImage
import TorangAsyncImage1
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.sryang.commonimageloader.ui.theme.CommonImageLoaderTheme
import deleteCache


class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalFoundationApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            var userScrollEnabled by remember { mutableStateOf(true) }
            CommonImageLoaderTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val list = listOf(
                        "http://sarang628.iptime.org:89/review_images/1/233/2024-08-30/09_40_00_339.jpg",
                        "https://artrkl.com/cdn/shop/articles/thecreationofadam-1690035964350_d2d6280f-ed1d-465e-ad42-0ea0bbbcefde.webp?v=1690563054&width=1100",
                        "https://media.cnn.com/api/v1/images/stellar/prod/190430171751-mona-lisa.jpg?q=w_2000,c_fill",
                        "https://media.timeout.com/images/105795964/750/422/image.jpg",
                        "https://cdn.shopify.com/s/files/1/0047/4231/6066/files/The_Scream_by_Edvard_Munch_1893_800x.png",
                        "https://d16kd6gzalkogb.cloudfront.net/magazine_images/Vincent-van-Gogh-Whaet-Field-with-Cypresses.-Image-via-wikimedia.org_.jpg",
                        "https://www.singulart.com/blog/wp-content/uploads/2023/09/Self-portrait-with-Straw-Hat-1887-88-857x1024.jpg",
                        "https://media.timeout.com/images/105222677/750/562/image.jpg",
                        "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcR-gAiEsXkkkjEeBCOxlJdk1aP6FiTS4feGSCWq9f8lNQ&s"

                    )
                    val context = LocalContext.current
                    var position by remember { mutableStateOf(0) }
                    val state = rememberPagerState {
                        list.size
                    }
                    Column {
                        Button(onClick = { position++ }) {

                        }
                        Button(onClick = { deleteCache(context) }) {
                            Text(text = "cache clear")
                        }
                    }
                    HorizontalPager(state = state, userScrollEnabled = userScrollEnabled) {
                        Box(modifier = Modifier.fillMaxSize()) {
                            Box(
                                modifier = Modifier
                                    .background(Color.Gray)
                                    .align(Alignment.Center)
                            ) {
                                /*ZoomableTorangAsyncImage(
                                    model = list[it],
                                    modifier = Modifier
                                        .align(Alignment.Center),
                                    contentScale = ContentScale.Crop,
                                    onEdge = {
                                        Log.d("__MainActivity", "onEdge: ${it}")
                                        userScrollEnabled = it
                                    },
                                    onSwipeDown = {
                                        Log.d("__MainActivity", "onSwipeDown")
                                    }
                                )*/
                                TorangAsyncImage(
                                    model = list[it], modifier = Modifier
                                        .height(400.dp)
                                        .background(Color.LightGray)
                                        .align(Alignment.Center),
                                    contentScale = ContentScale.Crop
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    CommonImageLoaderTheme {
        TorangAsyncImage(
            model = "https://upload.wikimedia.org/wikipedia/commons/thumb/a/a5/Instagram_icon.png/600px-Instagram_icon.png",
            modifier = Modifier
        )
        Greeting("Android")
    }
}

@Preview
@Composable
fun ListTest() {
    Row {
        LazyColumn {
            items(10000) {
                TorangAsyncImage1(
                    model = "https://upload.wikimedia.org/wikipedia/commons/thumb/a/a5/Instagram_icon.png/600px-Instagram_icon.png",
                    modifier = Modifier.size(100.dp)
                )
            }
        }
        LazyColumn {
            items(10000) {
                TorangAsyncImage(
                    model = "https://upload.wikimedia.org/wikipedia/commons/thumb/a/a5/Instagram_icon.png/600px-Instagram_icon.png",
                    modifier = Modifier.size(100.dp)
                )
            }
        }
        LazyColumn {
            items(10000) {
                TorangAsyncImage1(
                    model = "https://upload.wikimedia.org/wikipedia/commons/thumb/a/a5/Instagram_icon.png/600px-Instagram_icon.png",
                    modifier = Modifier.size(100.dp)
                )
            }
        }
    }

}