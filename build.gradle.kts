plugins {
    kotlin("multiplatform") version "1.8.0"
    id("maven-publish")
    id("signing")
    id("io.github.gradle-nexus.publish-plugin") version "1.1.0"
    id("org.jetbrains.dokka") version "1.7.20"
}

val artifactVersion: String by extra
group = "at.asitplus"
version = artifactVersion

repositories {
    mavenCentral()
}

val dokkaOutputDir = "$buildDir/dokka"
tasks.dokkaHtml {
    outputDirectory.set(file(dokkaOutputDir))
}
val deleteDokkaOutputDir by tasks.register<Delete>("deleteDokkaOutputDirectory") {
    delete(dokkaOutputDir)
}
val javadocJar = tasks.register<Jar>("javadocJar") {
    dependsOn(deleteDokkaOutputDir, tasks.dokkaHtml)
    archiveClassifier.set("javadoc")
    from(dokkaOutputDir)
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
        publications {
            withType<MavenPublication> {
                artifact(javadocJar)
                pom {
                    name.set("KmmResult")
                    description.set("Functional equivalent of kotlin.Result but with KMM goodness")
                    url.set("https://github.com/a-sit-plus/kmmresult")
                    licenses {
                        license {
                            name.set("The Apache License, Version 2.0")
                            url.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
                        }
                    }
                    developers {
                        developer {
                            id.set("JesusMcCloud")
                            name.set("Bernd Prünster")
                            email.set("bernd.pruenster@a-sit.at")
                        }
                        developer {
                            id.set("nodh")
                            name.set("Christian Kollmann")
                            email.set("christian.kollmann@a-sit.at")
                        }
                    }
                    scm {
                        connection.set("scm:git:git@github.com:a-sit-plus/kmmresult.git")
                        developerConnection.set("scm:git:git@github.com:a-sit-plus/kmmresult.git")
                        url.set("https://github.com/a-sit-plus/kmmresult")
                    }
                }
            }
        }
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

signing {
    sign(publishing.publications)
}

nexusPublishing {
    repositories {
        sonatype {
            nexusUrl.set(uri("https://s01.oss.sonatype.org/service/local/"))
            snapshotRepositoryUrl.set(uri("https://s01.oss.sonatype.org/content/repositories/snapshots/"))
        }
    }
}

