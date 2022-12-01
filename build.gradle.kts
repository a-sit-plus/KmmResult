plugins {
    kotlin("multiplatform") version "1.7.21"
    id("maven-publish")
}

val artifactVersion: String by extra
group = "at.asitplus"
version = artifactVersion

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
                jvmTarget = "11"
                freeCompilerArgs = listOf(
                    "-Xjsr305=strict"
                )
            }
        }
    }


    wasm32()
    js(BOTH){
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
        maven("https://oss.sonatype.org/content/repositories/snapshots") //Kotest
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
