plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.compose) apply false

    // MATCH THESE TO YOUR TOML: [plugins] hilt = ... and ksp = ...
    alias(libs.plugins.hilt) apply false
    alias(libs.plugins.ksp) apply false
    alias(libs.plugins.google.services) apply false

    // Serialization version set to match your Kotlin 2.3.0
    kotlin("plugin.serialization") version "2.3.0" apply false
}