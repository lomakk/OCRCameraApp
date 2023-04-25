pluginManagement {
    repositories {
        google()
        gradlePluginPortal()
        mavenCentral()
    }
}

dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "OCRCameraApp"
include(":ScanTexter")
include(":shared")

dependencyResolutionManagement {
    versionCatalogs {
        // TODO
//        create("tools") {
//            version("kotlin", "1.8.0")
//            version("androidLibrary", "7.4.2")
//
//            plugin("kotlin_android", "org.jetbrains.kotlin.android").version("kotlin")
//            plugin("kotlin_multiplatform", "org.jetbrains.kotlin.multiplatform").version("kotlin")
//            plugin("android_application", "com.android.application").versionRef("androidLibrary")
//            plugin("android_library", "com.android.library").versionRef("androidLibrary")
//        }

        create("libs") {
            // Compose activity
            library("composeActivity", "androidx.activity:activity-compose:1.7.0")

            // Timber
            library("timber", "com.jakewharton.timber:timber:4.7.1")

            // Compose coil
            library("composeCoil", "io.coil-kt:coil-compose:2.3.0")

            // Accompanist
            version("versionAccompanist", "0.23.1")
            library("accompanistPermission", "com.google.accompanist", "accompanist-permissions").versionRef("versionAccompanist")
            library("accompanistInsets", "com.google.accompanist", "accompanist-insets").versionRef("versionAccompanist")
            library("accompanistSystemController", "com.google.accompanist", "accompanist-systemuicontroller").versionRef("versionAccompanist")
            bundle("accompanist", listOf("accompanistPermission", "accompanistInsets", "accompanistSystemController"))

            // Compose core
            version("versionCompose", "1.4.0")
            library("composeUi", "androidx.compose.ui", "ui").versionRef("versionCompose")
            library("composeUiTooling", "androidx.compose.ui", "ui-tooling").versionRef("versionCompose")
            library("composeUiPreview", "androidx.compose.ui", "ui-tooling-preview").versionRef("versionCompose")
            library("composeUiUtil", "androidx.compose.ui", "ui-util").versionRef("versionCompose")
            library("composeFoundation", "androidx.compose.foundation", "foundation").versionRef("versionCompose")
            library("composeMaterial", "androidx.compose.material", "material").versionRef("versionCompose")
            library("composeIcons", "androidx.compose.material", "material-icons-core").versionRef("versionCompose")
            library("composeIconsExtended", "androidx.compose.material", "material-icons-extended").versionRef("versionCompose")
            bundle("composeCore",
                listOf(
                    "composeUi",
                    "composeUiTooling",
                    "composeUiUtil",
                    "composeUiPreview",
                    "composeFoundation",
                    "composeMaterial", "composeIcons", "composeIconsExtended"
                )
            )

            // Coroutines
            version("versionCoroutines", "1.6.2")
            library("coroutinesAndroid", "org.jetbrains.kotlinx", "kotlinx-coroutines-android").versionRef("versionCoroutines")
            bundle("coroutines", listOf("coroutinesAndroid"))

            // Koin
            version("versionKoin", "3.3.0")
            library("koinAndroid", "io.insert-koin", "koin-android").versionRef("versionKoin")
            library("koinCompose", "io.insert-koin", "koin-androidx-compose").versionRef("versionKoin")
            bundle("koin", listOf("koinAndroid", "koinCompose"))

            // CameraX android
            version("versionCameraX", "1.1.0")
            library("cameraCore", "androidx.camera", "camera-camera2").versionRef("versionCameraX")
            library("cameraLifecycle", "androidx.camera", "camera-lifecycle").versionRef("versionCameraX")
            library("cameraView", "androidx.camera", "camera-view").versionRef("versionCameraX")
            library("cameraVideo", "androidx.camera", "camera-video").versionRef("versionCameraX")
            bundle("cameraX", listOf(
                "cameraCore",
                "cameraLifecycle",
                "cameraView",
                "cameraVideo")
            )

            // Text recognition mlkit
            version("mlkitOCR", "16.0.0-beta6")
            library("textRecognition", "com.google.mlkit", "text-recognition").versionRef("mlkitOCR")
            library("textRecognitionChinese", "com.google.mlkit", "text-recognition-chinese").versionRef("mlkitOCR")
            library("textRecognitionDevanagari", "com.google.mlkit", "text-recognition-devanagari").versionRef("mlkitOCR")
            library("textRecognitionJapanese", "com.google.mlkit", "text-recognition-japanese").versionRef("mlkitOCR")
            library("textRecognitionKorean", "com.google.mlkit", "text-recognition-korean").versionRef("mlkitOCR")
            bundle(
                "textRecognition",
                listOf(
                    "textRecognition",
                    "textRecognitionChinese",
                    "textRecognitionDevanagari",
                    "textRecognitionJapanese",
                    "textRecognitionKorean"
                )
            )
        }
    }
}

