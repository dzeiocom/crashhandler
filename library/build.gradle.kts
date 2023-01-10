plugins {
    id("com.android.library")
    `maven-publish`
    kotlin("android")
}

val artifact = "crashhandler"
group = "com.dzeio"
val projectVersion = project.findProperty("version") as String? ?: "1.0.0"
version = projectVersion

publishing {
    publications {
        register<MavenPublication>("release") {
            groupId = group as String?
            artifactId = artifact
            version = projectVersion

            afterEvaluate {
                from(components["release"])
            }
        }
    }
}

android {
    namespace = "${group}.${artifact}"
    compileSdk = 33
    buildToolsVersion = "33.0.0"

    defaultConfig {
        minSdk = 21
        targetSdk = 33
        aarMetadata {
            minCompileSdk = 21
        }


        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }

    testFixtures {
        enable = true
    }

    publishing {
        singleVariant("release") {
            withSourcesJar()
            withJavadocJar()
        }
    }

    buildTypes {
        getByName("release") {
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

    // Enable View Binding and Data Binding
    buildFeatures {
        viewBinding = true
    }
}

dependencies {
    // Necessary for the Activity (well to make it pretty :D)
    implementation("com.google.android.material:material:1.6.1")
}