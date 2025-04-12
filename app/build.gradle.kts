plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
}

android {
    namespace = "com.hayde117.mlkit_vision_face"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.hayde117.mlkit_vision_face"
        minSdk = 24
        targetSdk = 35
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
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        viewBinding = true
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

//    // CameraX core library using the camera2 implementation
//    def camerax_version = "1.3.0-alpha06"
//    implementation "androidx.camera:camera-camera2:${camerax_version}"
//    // If you want to additionally use the CameraX Lifecycle library
//    implementation "androidx.camera:camera-lifecycle:${camerax_version}"
//    // If you want to additionally add CameraX ML Kit Vision Integration
//    implementation "androidx.camera:camera-mlkit-vision:${camerax_version}"

    implementation("androidx.camera:camera-camera2:1.3.0-alpha06")
    implementation("androidx.camera:camera-lifecycle:1.3.0-alpha06")
    implementation("androidx.camera:camera-mlkit-vision:1.3.0-alpha06")


    // For Unbundled, the model is dynamically downloaded via Google Play Services when the user installs the app.
    // It keeps the app size from increasing a lot. However, the user has to wait to download the model the first
    // time the application is used. For this option, you have to declare this dependency.
  //  implementation 'com.google.android.gms:play-services-mlkit-face-detection:17.1.0'

 //   implementation ("com.google.android.gms:play-services-mlkit-face-detection:17.1.0")


    // For Bundled, the model is linked to the app at build time.
    // This makes the app size increase a lot but users don’t have to wait to download the model.
    // Declare this dependency if you want to use this option.

    implementation ("com.google.mlkit:face-detection:16.1.5")

}