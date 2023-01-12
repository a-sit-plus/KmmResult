plugins {
    kotlin("multiplatform") version "1.8.0"
    id("maven-publish")
}

val artifactVersion: String by extra
group = "at.asitplus"
version = artifactVersion

repositories {
    mavenCentral()
}

kotlin {

    val xcf = org.jetbrains.kotlin.gradle.plugin.mpp.apple.XCFrameworkConfig(project, "KmmResult")
    macosArm64()
    macosX64 {
        binaries.framework {
            baseName = "KmmResult"
            embedBitcode("bitcode")
            xcf.add(this)
        }
    }

    tvosArm64 {
        binaries.framework {
            baseName = "KmmResult"
            embedBitcode("bitcode")
            xcf.add(this)
        }
    }
    tvosX64() {
        binaries.framework {
            baseName = "KmmResult"
            embedBitcode("bitcode")
            xcf.add(this)
        }
    }
    tvosSimulatorArm64() {
        binaries.framework {
            baseName = "KmmResult"
            embedBitcode("bitcode")
            xcf.add(this)
        }
    }
    ios() {
        binaries.framework {
            baseName = "KmmResult"
            embedBitcode("bitcode")
            xcf.add(this)
        }
    }
    iosSimulatorArm64() {
        binaries.framework {
            baseName = "KmmResult"
            embedBitcode("bitcode")
            xcf.add(this)
        }
    }

    jvm {
        compilations.all {
            kotlinOptions {
                jvmTarget = "11"
                freeCompilerArgs = listOf(
                    "-Xjsr305=strict"
                )
            }
        }
    }


    wasm32()
    js(IR){
        browser { testTask { enabled = false } }
        nodejs()
    }

    linuxX64()
    linuxArm64()

    mingwX64()

    sourceSets {
        val commonMain by getting

        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
            }
        }
    }



    val gitLabPrivateToken: String? by extra
    val gitLabProjectId: String by extra
    val gitLabGroupId: String by extra

    repositories {
        mavenLocal()
        if (System.getenv("CI_JOB_TOKEN") != null || gitLabPrivateToken != null) {
            maven {
                name = "gitlab"
                url = uri("https://gitlab.iaik.tugraz.at/api/v4/groups/$gitLabGroupId/-/packages/maven")
                if (gitLabPrivateToken != null) {
                    credentials(HttpHeaderCredentials::class) {
                        name = "Private-Token"
                        value = gitLabPrivateToken
                    }
                } else if (System.getenv("CI_JOB_TOKEN") != null) {
                    credentials(HttpHeaderCredentials::class) {
                        name = "Job-Token"
                        value = System.getenv("CI_JOB_TOKEN")
                    }
                }
                authentication {
                    create<HttpHeaderAuthentication>("header")
                }
            }
        }
        mavenCentral()
    }


    publishing {
        repositories {
            mavenLocal()
            if (System.getenv("CI_JOB_TOKEN") != null) {
                maven {
                    name = "gitlab"
                    url = uri("https://gitlab.iaik.tugraz.at/api/v4/projects/$gitLabProjectId/packages/maven")
                    credentials(HttpHeaderCredentials::class) {
                        name = "Job-Token"
                        value = System.getenv("CI_JOB_TOKEN")
                    }
                    authentication {
                        create<HttpHeaderAuthentication>("header")
                    }
                }
            }
        }
    }

}
