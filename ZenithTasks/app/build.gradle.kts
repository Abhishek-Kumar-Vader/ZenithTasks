plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    id("com.google.dagger.hilt.android")
    id("kotlin-kapt")

}

android {
    namespace = "com.example.zenithtasks"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.zenithtasks"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables{
            useSupportLibrary = true
        }
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
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    // --- Core AndroidX Dependencies (from libs.versions.toml, ensure these are correct) ---
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx) // Ensure 2.6.1 in libs.versions.toml
    implementation(libs.androidx.activity.compose)      // Ensure 1.8.2 in libs.versions.toml

    // --- Compose BOM (Bill of Materials) ---
    implementation(platform(libs.androidx.compose.bom)) // Ensure 2024.04.00 in libs.versions.toml
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3) // Will be pulled by BOM 2024.04.00

    // Material 2 (for Icons.Default.Add/Delete) - needed if you use Material2 icons/components specifically
//    implementation("androidx.compose.material:material:1.6.7")
//    // Material Icons Extended (for task icons) - using a version compatible with Compose 1.6.7
//    implementation("androidx.compose.material:material-icons-extended:1.6.7")


    // --- Room Database ---
    implementation("androidx.room:room-runtime:2.6.1")
    implementation("androidx.room:room-ktx:2.6.1")
    kapt("androidx.room:room-compiler:2.6.1")

    // --- ViewModel ---
    // Ensure this version is 2.6.1 for best compatibility with Kotlin 1.9.23 and other 2.6.x libs
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.6.1")
    // LiveData (if used, keep it compatible with lifecycle-viewmodel-compose)
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.6.1")


    // --- Navigation ---
    // Ensure this version is 2.7.7 for best compatibility with current setup
    implementation(libs.androidx.navigation.compose)

    // --- Kotlin Coroutines ---
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.8.0")


    // --- Testing ---
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)

    // --- Debug ---
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    implementation ("androidx.compose.material:material-icons-extended:1.7.8")

    // Dagger Hilt
    implementation("com.google.dagger:hilt-android:2.51") // Use the same version as above
    kapt("com.google.dagger:hilt-android-compiler:2.51") // Use the same version as above

    // Hilt for Compose (optional, but good practice for ViewModel integration)
    implementation("androidx.hilt:hilt-navigation-compose:1.2.0")
}