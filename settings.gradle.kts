pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven {
            url = uri("https://raw.githubusercontent.com/AAU-IQ/BabylAI-Android/main/releases")
            metadataSources {
                mavenPom()
                artifact()
            }
            content { includeGroup("iq.aau.babylai.android") }
        }
        maven { url = uri("https://jitpack.io") }

    }
}

rootProject.name = "BabylAI-Example"
include(":app")


val localSdkOverride = File(rootDir, "local.settings.gradle.kts")
if (localSdkOverride.exists()) {
    apply(from = localSdkOverride)
}
