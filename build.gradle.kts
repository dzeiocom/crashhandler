// Top-level build file where you can add configuration options common to all sub-projects/modules.
buildscript {
    repositories {
        mavenCentral()
    }
    dependencies {
        classpath("com.android.tools.build:gradle:8.1.1")

        // NOTE: Do not place your application dependencies here; they belong
        // in the individual module build.gradle files
    }
}

plugins {
    id("com.android.application") version "8.1.1" apply false
    id("com.android.library") version "8.1.1" apply false
    id("org.jetbrains.kotlin.android") version "1.7.0" apply false

    // Apply the java-library plugin for API and implementation separation.
    `java-library`
    `maven-publish`
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

version = System.getenv("version") ?: tag.drop(1).ifEmpty {
    branch
}

val finalVersion = version
val finalGroup = System.getenv("group") ?: "com.dzeio"
val artifact = System.getenv("artifact") ?: "crashhandler"

group = finalGroup

publishing {
    publications {
        create<MavenPublication>("maven") {
            groupId = finalGroup
            artifactId = artifact
            version = finalVersion as String

            from(components["java"])

            pom {
                name.set("Crash Handler")
                description.set("Library that help handling crashes")
                url.set("https://github.com/dzeiocom/crashhandler")
                licenses {
                    license {
                        name.set("MIT License")
                        url.set("https://github.com/dzeiocom/crashhandler/blob/master/LICENSE")
                    }
                }
                developers {
                    developer {
                        id.set("avior")
                        name.set("Avior")
                        email.set("contact@dze.io")
                    }
                }
                scm {
                    connection.set("scm:git@github.com:dzeiocom/crashhandler.git")
                    url.set("https://github.com/dzeiocom/crashhandler")
                }
            }
        }
    }
    repositories {
        maven {
            name = "GithubPackages"
            url = uri("https://maven.pkg.github.com/dzeiocom/crashhandler")
            credentials {
                username = project.findProperty("gpr.user") as String? ?: System.getenv("USERNAME")
                password = project.findProperty("gpr.key") as String? ?: System.getenv("TOKEN")
            }
        }
    }
}

//task("clean") {
//    delete(rootProject.buildDir)
//    delete(project.buildDir)
//}
