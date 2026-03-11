import com.vanniktech.maven.publish.SonatypeHost

plugins {
    id("com.android.library")
    kotlin("android")
    id("com.vanniktech.maven.publish") version "0.29.0"
}

// from: https://discuss.kotlinlang.org/t/use-git-hash-as-version-number-in-build-gradle-kts/19818/8
fun String.runCommand(
    workingDir: File = File("."),
    timeoutAmount: Long = 60,
    timeoutUnit: TimeUnit = TimeUnit.SECONDS
): String = ProcessBuilder(split("\\s(?=(?:[^'\"`]*(['\"`])[^'\"`]*\\1)*[^'\"`]*$)".toRegex()))
    .directory(workingDir)
    .redirectOutput(ProcessBuilder.Redirect.PIPE)
    .redirectError(ProcessBuilder.Redirect.PIPE)
    .start()
    .apply { waitFor(timeoutAmount, timeoutUnit) }
    .run {
        val error = errorStream.bufferedReader().readText().trim()
        if (error.isNotEmpty()) {
            return@run ""
        }
        inputStream.bufferedReader().readText().trim()
    }

val branch = "git rev-parse --abbrev-ref HEAD".runCommand(workingDir = rootDir)
val tag = "git tag -l --points-at HEAD".runCommand(workingDir = rootDir)
val commitId = "git rev-parse HEAD".runCommand(workingDir = rootDir)

val libGroup = "com.dzeio"
val libArtifact = "crashhandler"
val libVersion = System.getenv("version") ?: tag.drop(1).ifEmpty {
    project.findProperty("VERSION_NAME") as String? ?: "1.1.0"
}

android {
    namespace = "$libGroup.$libArtifact"
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
        buildConfigField("String", "VERSION", "\"$libVersion\"")
    }

    testFixtures {
        enable = true
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

mavenPublishing {
    publishToMavenCentral(SonatypeHost.CENTRAL_PORTAL)

    signAllPublications()

    coordinates(libGroup, libArtifact, libVersion)

    pom {
        name.set("Crash Handler")
        description.set("An Android library that helps handling and reporting crashes gracefully.")
        url.set("https://github.com/dzeiocom/crashhandler")
        inceptionYear.set("2022")

        licenses {
            license {
                name.set("MIT License")
                url.set("https://github.com/dzeiocom/crashhandler/blob/master/LICENSE")
                distribution.set("repo")
            }
        }

        developers {
            developer {
                id.set("avior")
                name.set("Avior")
                email.set("contact@dze.io")
                url.set("https://github.com/Aviortheking")
            }
        }

        scm {
            url.set("https://github.com/dzeiocom/crashhandler")
            connection.set("scm:git:git://github.com/dzeiocom/crashhandler.git")
            developerConnection.set("scm:git:ssh://git@github.com/dzeiocom/crashhandler.git")
        }

        issueManagement {
            system.set("GitHub Issues")
            url.set("https://github.com/dzeiocom/crashhandler/issues")
        }
    }
}

dependencies {
    // Necessary for the Activity (well to make it pretty :D)
    implementation("com.google.android.material:material:1.8.0")
}
