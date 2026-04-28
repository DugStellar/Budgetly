pluginManagement {
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
        // FIX: This allows the PieChart library to be downloaded
        maven { url = uri("https://jitpack.io") }
    }
}

rootProject.name = "Budgetly"
include(":app")
