import org.jetbrains.kotlin.gradle.plugin.mpp.apple.XCFrameworkConfig

plugins {
    kotlin("multiplatform") version "1.7.20"
}

group = "at.asitplus"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

kotlin {

    macosArm64()
    macosX64()

    tvosArm64()
    tvosX64()
    tvosSimulatorArm64()
    ios()
    iosSimulatorArm64()

    jvm {
        compilations.all {
            kotlinOptions {
                jvmTarget = "1.8"
                freeCompilerArgs = listOf(
                    "-Xjsr305=strict"
                )
            }
        }
    }


    wasm32()
    js(BOTH){
        browser()
        nodejs()
    }

    linuxX64()
    linuxArm64()

    mingwX64()

    sourceSets {
        val commonMain by getting
    }
}
