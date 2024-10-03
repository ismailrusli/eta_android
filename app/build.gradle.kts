

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    id ("kotlin-kapt")
    id ("dagger.hilt.android.plugin")
}

android {
    namespace = "com.example.ble_bluetoothlowenergyesp32"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.ble_bluetoothlowenergyesp32"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.1"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }

    externalNativeBuild {
        cmake {
            path ( "src/main/cpp/CMakeLists.txt" ) // Path ke file CMakeLists.txt
            version = "3.30.3"          // Versi CMake
        }
    }

}


dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)


    //DI
    implementation ("com.google.dagger:hilt-android:2.52")
    annotationProcessor ("com.google.dagger:hilt-compiler:2.52")
    kapt ("com.google.dagger:hilt-compiler:2.52")
    kapt ("androidx.hilt:hilt-compiler:1.0.0")
    implementation ("androidx.hilt:hilt-navigation-compose:1.0.0")

    //Navigation
    implementation ("androidx.navigation:navigation-compose:2.5.2")

    //Permissions
    implementation ("com.google.accompanist:accompanist-permissions:0.21.1-beta")

    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.5")

    //Test Unit
    androidTestImplementation ("androidx.compose.ui:ui-test-junit4:$version")

    debugImplementation( "androidx.compose.ui:ui-test-manifest:$rootProject.composeVersion")

    //AudioPlayer
    implementation ("com.google.oboe:oboe:1.6.0")


    implementation ("androidx.media:media:1.6.0") // For SoundPool


}