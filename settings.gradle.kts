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
        mavenCentral()
        maven(url = "https://jitpack.io") // ✅ Add this line
        google()
    }
}

rootProject.name = "What beats rock"
include(":app")
include(":core-datasource")
include(":feature-chat")
include(":core-designsystem")
include(":feature-onboarding")
include(":feature-profile")
include(":feature-share")
include(":feature-leaderboard")
