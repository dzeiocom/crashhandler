plugins {
    id("com.android.application")
    kotlin("android")
}

android {
    namespace = "com.dzeio.crashhandlertest"
    compileSdk = 33

    defaultConfig {
        applicationId = "com.dzeio.crashhandlertest"
        minSdk = 21
        targetSdk = 33
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
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

    implementation(project(":library"))
    
    // Material Design
    implementation("com.google.android.material:material:1.6.1")

    // Navigation because I don't want to maintain basic transactions and shit
    implementation("androidx.navigation:navigation-fragment-ktx:2.5.1")

    // preferences
    implementation("androidx.preference:preference-ktx:1.2.0")
}