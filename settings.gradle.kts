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
        maven("https://maven.singular.net/")
        maven("https://s3.ap-south-1.amazonaws.com/hvsdk/android/releases")
    }
}

rootProject.name = "mandate-sdk"
include(":androidApp")

include(":kernel-common")
include(":network-manager")
include(":sync-manager-core")

include(":mandate-module")
