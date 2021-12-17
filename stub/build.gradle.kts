plugins {
    id("com.android.application")
}

val androidStoreFile: String? by rootProject
val androidStorePassword: String? by rootProject
val androidKeyAlias: String? by rootProject
val androidKeyPassword: String? by rootProject

android {
    compileSdk = 32

    defaultConfig {
        applicationId = "org.lsposed.wsa.helper"
        minSdk = 30
        targetSdk = 32
        versionCode = 1
        versionName = "1.0"
    }

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
                (this as com.android.build.gradle.internal.dsl.BuildType).isShrinkResources = true
                proguardFiles("proguard-rules.pro")
            }
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
}
