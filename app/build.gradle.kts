import com.android.build.gradle.internal.dsl.BuildType

plugins {
    id("com.android.application")
    id("kotlin-android")
}

val composeVersion: String by rootProject.extra


android {
    compileSdk = 32

    defaultConfig {
        applicationId = "org.lsposed.wsa.helper"
        minSdk = 30
        targetSdk = 32
        versionCode = 1
        versionName = "1.0"

    }

    val androidStoreFile: String? by rootProject
    val androidStorePassword: String? by rootProject
    val androidKeyAlias: String? by rootProject
    val androidKeyPassword: String? by rootProject

    signingConfigs {
        create("config") {
            androidStoreFile?.also {
                storeFile = rootProject.file(it)
                storePassword = androidStorePassword
                keyAlias = androidKeyAlias
                keyPassword = androidKeyPassword
            }
        }
    }

    buildTypes {
        signingConfigs.named("config").get().also {
            debug {
                if (it.storeFile?.exists() == true) signingConfig = it
            }
            release {
                signingConfig = if (it.storeFile?.exists() == true) it
                else signingConfigs.named("debug").get()
                isMinifyEnabled = true
                (this as BuildType).isShrinkResources = true
                proguardFiles("proguard-rules.pro")
            }
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
    composeOptions {
        kotlinCompilerExtensionVersion = composeVersion
    }
    packagingOptions {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    implementation("com.google.android.material:material:1.6.0-alpha01")
    implementation("androidx.compose.ui:ui:$composeVersion")
    implementation("androidx.compose.material:material:$composeVersion")
    implementation("androidx.compose.ui:ui-tooling-preview:$composeVersion")
    implementation("androidx.activity:activity-compose:1.4.0")
    implementation("com.google.accompanist:accompanist-drawablepainter:0.21.4-beta")
    debugImplementation("androidx.compose.ui:ui-tooling:$composeVersion")
}
