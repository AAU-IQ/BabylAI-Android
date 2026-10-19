import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.serialization)
}

val localProperties = Properties().apply {
    rootProject.file("local.properties").takeIf { it.exists() }?.inputStream()?.use { load(it) }
}

fun localStringField(key: String): String = "\"${localProperties.getProperty(key, "")}\""

android {
    namespace = "iq.aau.babylai.android.BabylAI"
    compileSdk = 35

    defaultConfig {
        applicationId = "iq.aau.babylai.android.BabylAI"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        debug {
            isMinifyEnabled = false
            isDebuggable = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            // Debug-only credential prefills, read from the gitignored local.properties
            buildConfigField("String", "DEFAULT_HELP_SCREEN_ID", localStringField("babylai.helpScreenId"))
            buildConfigField("String", "DEFAULT_TENANT_ID", localStringField("babylai.tenantId"))
            buildConfigField("String", "DEFAULT_API_KEY", localStringField("babylai.apiKey"))
        }
        release {
            isMinifyEnabled = true

            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            buildConfigField("String", "DEFAULT_HELP_SCREEN_ID", "\"\"")
            buildConfigField("String", "DEFAULT_TENANT_ID", "\"\"")
            buildConfigField("String", "DEFAULT_API_KEY", "\"\"")
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
        buildConfig = true
    }
}

dependencies {
    // BabylAI SDK - local source when :BabylAISDK is included via local.settings.gradle.kts,
    // otherwise the published artifact from the distribution repository.
    if (findProject(":BabylAISDK") != null) {
        implementation(project(":BabylAISDK"))
    } else {
        implementation("iq.aau.babylai.android:babylaisdk:1.1.18")
    }
    // Android Core
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    
    // Jetpack Compose BOM
    implementation(platform("androidx.compose:compose-bom:2025.08.00"))
    
    // Compose dependencies
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.runtime:runtime")
    implementation("androidx.compose.foundation:foundation")
    implementation("androidx.compose.material:material")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.material:material-icons-core")
    implementation("androidx.compose.material:material-icons-extended")
    
    // Activity Compose
    implementation("androidx.activity:activity-compose:1.8.2")
    
    // Navigation Compose
    implementation("androidx.navigation:navigation-compose:2.7.6")
    
    // ViewModel
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.7.0")
    
    // Network
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.3")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")
    
    // UI tooling for Compose previews
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")
    
    // Testing
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}