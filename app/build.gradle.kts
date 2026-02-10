plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.google.gms.google.services)
    id("org.jetbrains.kotlin.plugin.serialization")
    id("com.google.devtools.ksp") // Required for Hilt & Room in 2026
}

android {
    ksp {
        arg("useKsp2", "true")
    }
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
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11

    }

    buildFeatures {
        compose = true
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    implementation(libs.firebase.auth)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
    // --- Hilt (Dependency Injection) ---
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler) // KSP is preferred over KAPT
    implementation(libs.androidx.hilt.navigation.compose)

    // --- Navigation 3 ---
    // Note: In 2026, Navigation 3 is part of the compose-navigation-suite
    implementation(libs.androidx.navigation.compose)
    implementation(libs.kotlinx.serialization.json)

    // --- Firebase ---
    // Import the BoM (Bill of Materials) - This manages versions for you
    implementation(platform(libs.firebase.bom))

    // Use the standard libraries (Kotlin extensions are now built-in)
    implementation(libs.google.firebase.auth)
    implementation(libs.firebase.firestore)

    // For Coroutines support (to use .await())
    implementation(libs.kotlinx.coroutines.play.services)

    // --- Glassmorphism & UI ---
    // Haze for hardware-accelerated iOS blur effects
    implementation(libs.haze)
    implementation(libs.haze.blur) // Required for blurEffect
    // ConstraintLayout for Compose
    implementation(libs.androidx.constraintlayout.compose)
}