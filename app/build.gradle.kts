plugins {
    alias(libs.plugins.android.application)
    // In AGP 9.0, kotlin-android is often built-in,
    // but if you need it, add alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.hilt)
    alias(libs.plugins.ksp)
    alias(libs.plugins.google.services)
    kotlin("plugin.serialization")
}

android {
    namespace = "com.example.zentrix"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.example.zentrix"
        minSdk = 26
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }


    buildFeatures {
        compose = true
    }
}

dependencies {
    implementation(platform(libs.firebase.bom))

    // Add specific Firebase products (versions are managed by BoM)
    implementation(libs.firebase.analytics)
    implementation(libs.firebase.auth)
    implementation(libs.firebase.firestore)
    // Core & Compose
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.material3.window.size.class1)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.compose.foundation)
    debugImplementation(libs.androidx.ui.tooling)

    // Retrofit
    implementation(libs.retrofit)
    implementation(libs.converter.gson)

    // OkHttp (for logging)
    implementation(libs.okhttp)
    implementation(libs.logging.interceptor)

    // Coil for image loading
    implementation(libs.coil.compose)

    // Coroutines
    implementation(libs.kotlinx.coroutines.android.v173)

    // Lifecycle
    implementation(libs.androidx.lifecycle.viewmodel.compose.v270)
    implementation(libs.androidx.lifecycle.runtime.compose.v270)


    // Hilt
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)
    implementation(libs.hilt.navigation.compose)

    // THE CRITICAL METADATA FIX:
    // This forces the Hilt KSP processor to understand Kotlin 2.3.0 metadata
    ksp("org.jetbrains.kotlin:kotlin-metadata-jvm:2.3.0")

    // Navigation & Serialization
    implementation(libs.androidx.navigation.compose)
    implementation(libs.kotlinx.serialization.json)

    // Glassmorphism (Haze)
    implementation(libs.haze)
    implementation(libs.androidx.icons.extended)

    // Testing
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    implementation(libs.androidx.navigation3.runtime)
    implementation(libs.androidx.navigation3.ui)
    implementation(libs.androidx.lifecycle.viewmodel.navigation3)
}

