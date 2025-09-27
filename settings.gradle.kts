pluginManagement{
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven { url = uri("https://jitpack.io") }
        maven("https://maven.singular.net/")
        maven("https://s3.ap-south-1.amazonaws.com/hvsdk/android/releases")
    }
}

rootProject.name = "mandate-sdk"
include(":androidApp")
include(":mandate-module")
