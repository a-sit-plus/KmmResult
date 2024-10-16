rootProject.name = "kmmresult-root" //DOKKA BUG with spaces


pluginManagement {
    repositories {
        mavenCentral()
        gradlePluginPortal()
    }
}

@Suppress("UnstableApiUsage")
dependencyResolutionManagement {
    repositories {
        mavenCentral()
    }
}


include("kmmresult")
include("kmmresult-test")