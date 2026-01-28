/**
 * Build configuration for the VoiceConnect application module.
 * This file defines the plugins, Android-specific settings, and dependencies required to build the app.
 */
plugins {
    // Standard Android application plugin
    alias(libs.plugins.androidApplication)
    // Kotlin support for Android
    alias(libs.plugins.kotlinAndroid)
    // Dependency Injection support via Dagger Hilt
    alias(libs.plugins.hiltAndroid)
    // Kotlin Serialization for JSON processing
    alias(libs.plugins.kotlinSerialization)
    // Kotlin Symbol Processing for efficient code generation
    alias(libs.plugins.ksp)
    // Jetpack Compose compiler support
    alias(libs.plugins.kotlinCompose)
    // Google Services plugin for Firebase integration
    alias(libs.plugins.google.services)
}

android {
    // Unique identifier for this module's R and BuildConfig classes
    namespace = "com.app.voiceconnect"
    // The SDK version used to compile the application
    compileSdk = 36

    defaultConfig {
        // Unique application ID for the Play Store and device
        applicationId = "com.app.voiceconnect"
        // Minimum Android version required to run the app
        minSdk = 26
        // The SDK version the app is tested against and targets
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            // Disable code shrinking and obfuscation for the release build
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    
    compileOptions {
        // Use Java 21 features for compilation
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }
    
    buildFeatures {
        // Enable Jetpack Compose UI toolkit
        compose = true
        // Generate a BuildConfig class for build-time constants
        buildConfig = true
    }
}

dependencies {
    // --- Core Android Extensions ---
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.activity.compose)

    // --- Jetpack Compose UI Stack ---
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    debugImplementation(libs.androidx.ui.tooling)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.material.icons.extended)

    // --- Lifecycle & State Management ---
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.lifecycle.viewmodel.compose)

    // --- Navigation (Traditional & Experimental Navigation 3) ---
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.navigation3.runtime)
    implementation(libs.androidx.navigation3.ui)

    // --- Dependency Injection (Hilt) ---
    implementation(libs.hilt.android)
    implementation(libs.androidx.hilt.navigation.compose)
    ksp(libs.hilt.compiler)

    // --- Networking (Retrofit & OkHttp) ---
    implementation(libs.retrofit)
    implementation(libs.retrofit.serialization.converter)
    implementation(libs.retrofit.gson)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.okhttp.logging)
    implementation(libs.okhttp)

    // --- Media Playback (Media3) ---
    implementation(libs.androidx.media3.session)
    implementation(libs.androidx.media3.exoplayer)

    // --- Utilities & Permissions ---
    implementation(libs.accompanist.permissions)
    implementation(libs.androidx.datastore.preferences)

    // --- Twilio Voice SDK ---
    implementation(libs.twilio.voice)

    // --- Firebase Cloud Messaging ---
    implementation(libs.firebase.messaging)
}
