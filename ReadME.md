```
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        mavenCentral()
        maven { url = uri("https://jitpack.io") }
    }
}

dependencies {
        implementation("com.github.sarang628:CommonImageLoader:1999de5a48")
}
```

```kotlin
val url  = "https://artrkl.com/cdn/shop/articles/thecreationofadam-1690035964350_d2d6280f-ed1d-465e-ad42-0ea0bbbcefde.webp?v=1690563054&width=1100"
TorangAsyncImage(url)
```

```kotlin
val url  = "https://artrkl.com/cdn/shop/articles/thecreationofadam-1690035964350_d2d6280f-ed1d-465e-ad42-0ea0bbbcefde.webp?v=1690563054&width=1100"
ZoomableTorangAsyncImage(url)
```


## 이미지 로드 모듈 추가

이미지 로드 모듈 단일 버전 관리를 위해 루트 프로젝트에서 구현하고 하위 프로젝트들은 인터페이스만 받음.
```
cd app/src/main/java/[package]/di
git submodule add (or git clone) https://github.com/sarang628/image.git
```
이미지 로드 모듈에 줌 처리 기능이 추가되 추가 모듈 설정 필요.
```
cd app/src/main/java/[package]/di
git submodule add (or git clone) https://github.com/sarang628/pinchzoom.git
```
이미지 로드 모듈 다운로드
```
implementation("com.github.sarang628:CommonImageLoader:1999de5a48") 
```