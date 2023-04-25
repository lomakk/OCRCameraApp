plugins {
    id("com.android.application")
    kotlin("android")
}

android {
    namespace = "com.vision.scantexter.android"
    compileSdk = 33
    defaultConfig {
        applicationId = "com.vision.scantexter.android"
        minSdk = 24
        targetSdk = 33
        versionCode = 1
        versionName = "0.001"
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = libs.versions.versionCompose.get()
    }
    packagingOptions {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
}

dependencies {
    implementation(project(":shared"))

    implementation(libs.bundles.composeCore)
    implementation(libs.composeActivity)
    implementation(libs.composeCoil)
    implementation(libs.bundles.textRecognition)
    implementation(libs.bundles.coroutines)
    implementation(libs.bundles.cameraX)
    implementation(libs.timber)
    implementation(libs.bundles.accompanist)
    implementation(libs.bundles.koin)
}