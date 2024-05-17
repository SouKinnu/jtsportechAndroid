pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
        maven("https://jitpack.io")
        maven("https://maven.aliyun.com/repository/public")
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven("https://jitpack.io")
        maven("https://s01.oss.sonatype.org/content/groups/public")
        maven("https://maven.aliyun.com/repository/public" )
    }
}

rootProject.name = "jtsportechAndroid"
include(":app")
include(":lib_base")
include(":lib_common")
include(":activityResultLauncherLibrary")