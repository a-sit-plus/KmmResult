rootProject.name = "kmmresult-root" //DOKKA BUG with spaces


pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

@Suppress("UnstableApiUsage")
dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
    }
}


include("kmmresult")
include("kmmresult-test")