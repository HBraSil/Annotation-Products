plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.room)
    alias(libs.plugins.hilt.plugin)
    alias(libs.plugins.ksp)
    alias(libs.plugins.google.services)
}

android {
    namespace = "com.hilquias.anotafacil"
    compileSdk = 37

    defaultConfig {
        applicationId = "com.hilquias.anotafacil"
        minSdk = 24
        targetSdk = 37
        versionCode = 3
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true

            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            ndk {
                debugSymbolLevel = "FULL"
            }
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    buildFeatures {
        compose = true
    }
    kotlin {
        compilerOptions {
            jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17)
        }
    }
}

room {
    schemaDirectory("$projectDir/schemas")
}

ksp {
    arg("room.generateKotlin", "true")
}

dependencies {
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.datastore.core)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.navigation.compose)
    testImplementation(libs.junit)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.junit)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
    debugImplementation(libs.androidx.compose.ui.tooling)


    implementation(libs.androidx.material.icons)


    // ROOM
    implementation(libs.room.runtime)
    implementation(libs.room.ktx)
    ksp(libs.room.compiler)

    implementation(libs.gson)

    // SPLASH SCREEN
    implementation(libs.androidx.core.splashscreen)

    // HILT
    ksp(libs.hilt.compiler)
    implementation(libs.hilt.android)
    implementation(libs.hilt.navigation)

    // VICO
    implementation(libs.vico.compose)
    implementation(libs.vico.compose.m3)

    // Bottom Navigation
    implementation(libs.animated.navigation.bar)

    // FIREBASE
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.firestore)
    implementation(libs.firebase.analytics)
    implementation(libs.google.firebase.auth)

    // CREDENCIAIS
    implementation(libs.googleid)
    implementation(libs.androidx.credentials)
    implementation(libs.androidx.credentials.play.services.auth)
}