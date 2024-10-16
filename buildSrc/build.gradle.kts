plugins {
    `kotlin-dsl`
}

repositories {
    mavenCentral()
    gradlePluginPortal()
}

dependencies {
    implementation("org.jetbrains.dokka:dokka-gradle-plugin:2.0.0-Beta")
    implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:2.0.20")
}