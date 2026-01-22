plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("kotlin-android")
    id("kotlin-kapt")
    id("dagger.hilt.android.plugin")
}

android {
    compileSdk = rootProject.extra["compileSdk"] as Int
    namespace = "com.sarang.torang"
    defaultConfig {
        minSdk = rootProject.extra["minSdk"] as Int
        targetSdk = rootProject.extra["targetSdk"] as Int
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        testInstrumentationRunner = "com.sarang.torang.CustomTestRunner"
    }

    android.buildFeatures.buildConfig = true

    buildTypes {
        getByName("debug") {
            buildConfigField("String", "SERVER_URL", "\"http://sarang628.iptime.org\"")
            buildConfigField("String", "IMAGE_PORT", "\"89\"")
            buildConfigField("String", "PROFILE_IMAGE_SERVER_URL", "\"http://sarang628.iptime.org:89/profile_images/\"")
            buildConfigField("String", "REVIEW_IMAGE_SERVER_URL", "\"http://sarang628.iptime.org:89/review_images/\"")
            buildConfigField("String", "RESTAURANT_IMAGE_SERVER_URL", "\"http://sarang628.iptime.org:89/restaurant_images/\"")
            buildConfigField("String", "MENU_IMAGE_SERVER_URL", "\"http://sarang628.iptime.org:89/menu_images/\"")
        }

        getByName("release") {
            buildConfigField("String", "SERVER_URL", "\"http://sarang628.iptime.org\"")
            buildConfigField("String", "IMAGE_PORT", "\"89\"")
            buildConfigField("String", "PROFILE_IMAGE_SERVER_URL", "\"http://sarang628.iptime.org:89/profile_images/\"")
            buildConfigField("String", "REVIEW_IMAGE_SERVER_URL", "\"http://sarang628.iptime.org:89/review_images/\"")
            buildConfigField("String", "RESTAURANT_IMAGE_SERVER_URL", "\"http://sarang628.iptime.org:89/restaurant_images/\"")
            buildConfigField("String", "MENU_IMAGE_SERVER_URL", "\"http://sarang628.iptime.org:89/menu_images/\"")
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
            signingConfig = signingConfigs.getByName("debug")
        }
    }

    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = libs.versions.kotlinCompilerExtension.get()
    }

    compileOptions {
        targetCompatibility = JavaVersion.VERSION_17
        sourceCompatibility = JavaVersion.VERSION_17
    }

    hilt {
        enableTransformForLocalTests = true
    }
}

dependencies {
    /** HILT */
    implementation(libs.hilt)
    kapt(libs.hilt.compiler)
    implementation(libs.hilt.nav.compose) // hiltViewModel

    // Testing Start
    testImplementation(libs.junit)
    androidTestImplementation(libs.x.junit.ext)
    androidTestImplementation(libs.x.espresso.core)
    testImplementation(libs.kotlinx.coroutines.test) // coroutines unit test
    androidTestImplementation(libs.x.ui.test.junit4) // Test rules and transitive dependencies
    debugImplementation(libs.x.ui.test.manifest) // Needed for createAndroidComposeRule, but not createComposeRule
    testImplementation(libs.mockito.core) // Mockito
    testImplementation(libs.mockito.inline)
    testImplementation(libs.core.testing) // AndroidX Core Testing
    // Testing End

    /** Retrofit */
    implementation(libs.retrofit)
    implementation(libs.converter.gson)
    implementation(libs.logging.interceptor)

    /** Room */
    implementation(libs.room.runtime)
    annotationProcessor(libs.room.compiler)
    implementation(libs.room.paging)

    /** Compose */
    androidTestImplementation(platform(libs.x.compose.bom))
    implementation(libs.x.ui) //없으면 @Composable import 안됨
    implementation(libs.x.ui.graphics)
    implementation(libs.x.ui.tooling.preview) // Android Studio Preview support
    debugImplementation(libs.x.ui.tooling)
    implementation(libs.material3) //JetNews Main 따라하기
    implementation(libs.material3.windows.size)
    implementation(libs.lifecycle.runtime.compose)
    implementation(libs.nav.compose)
    androidTestImplementation(libs.x.ui.test.junit4) //runTest
    debugImplementation(libs.x.ui.test.manifest) // Needed for createAndroidComposeRule, but not createComposeRule:
    implementation("androidx.constraintlayout:constraintlayout-compose:1.0.1")

    implementation(libs.theme)
    implementation(project(":library"))
    implementation(libs.torangRepository)
    implementation(libs.torangdatabase)
    implementation(libs.torangnetwork)
    implementation(libs.commonImageLoader)
    implementation(libs.coil)
}