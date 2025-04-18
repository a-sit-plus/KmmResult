import io.gitlab.arturbosch.detekt.Detekt
import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import java.net.URI

plugins {
    id("com.android.library")
    kotlin("multiplatform")
    id("maven-publish")
    id("signing")
    id("org.jetbrains.dokka")
    id("org.jetbrains.kotlinx.kover") version "0.8.0"
    id("io.gitlab.arturbosch.detekt") version "1.23.6"
}

val artifactVersion: String by extra
group = "at.asitplus"
version = artifactVersion

repositories {
    google()
    mavenCentral()
}

val dokkaOutputDir = "$projectDir/docs"
dokka {
    dokkaSourceSets {

        named("commonMain") {
            sourceLink {
                val path = "${projectDir}/src/$name/kotlin"
                println(path)
                localDirectory.set(file(path))
                remoteUrl.set(
                    URI("https://github.com/a-sit-plus/KmmResult/tree/main/kmmresult-test/src/$name/kotlin")
                )
                // Suffix which is used to append the line number to the URL. Use #L for GitHub
                remoteLineSuffix.set("#L")
            }
        }
    }
    pluginsConfiguration.html {
        footerMessage = "&copy; 2024 A-SIT Plus GmbH"
    }
}
val deleteDokkaOutputDir by tasks.register<Delete>("deleteDokkaOutputDirectory") {
    delete(dokkaOutputDir)
}
val javadocJar = tasks.register<Jar>("javadocJar") {
    dependsOn(deleteDokkaOutputDir, tasks.dokkaGenerate)
    archiveClassifier.set("javadoc")
    from(dokkaOutputDir)
}

tasks.getByName("check") {
    dependsOn("detektMetadataMain")
}


//first sign everything, then publish!
tasks.withType<AbstractPublishToMaven>() {
    tasks.withType<Sign>().forEach {
        dependsOn(it)
    }
}

kotlin {
    jvmToolchain(17)

    macosArm64()
    macosX64()
    tvosArm64()
    tvosX64()
    tvosSimulatorArm64()
    iosX64()
    iosArm64()
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

    androidTarget {
        compilerOptions {
            publishLibraryVariants("release")
            jvmTarget = JvmTarget.JVM_1_8
        }
    }
    listOf(
        js(IR).apply { browser { testTask { enabled = false } } },
        @OptIn(ExperimentalWasmDsl::class)
        wasmJs().apply { browser { testTask { enabled = false } } }
    ).forEach {
        it.nodejs()
    }

    linuxX64()
    linuxArm64()
    mingwX64()

    sourceSets {
        commonMain.dependencies {
            implementation(project(":kmmresult"))
            api("io.kotest:kotest-assertions-core:6.0.0.M1")
        }
    }

    tasks.withType<Detekt>().configureEach {
        reports {
            xml.required.set(true)
            html.required.set(false)
            txt.required.set(false)
            sarif.required.set(true)
            md.required.set(true)
        }
    }
}


android {
    namespace = "at.asitplus.kmmresult.test"
    compileSdk = 34
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    defaultConfig {
        minSdk = 30
    }
}


dependencies {
        detektPlugins("io.gitlab.arturbosch.detekt:detekt-formatting:1.23.7")
    }

    repositories {
        mavenCentral()
    }

    publishing {
        publications {
            withType<MavenPublication> {
                artifact(javadocJar)
                pom {
                    name.set("KmmResult Test")
                    description.set("Kotest helperrs for KmmResult")
                    url.set("https://github.com/a-sit-plus/KmmResult")
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
                        connection.set("scm:git:git@github.com:a-sit-plus/KmmResult.git")
                        developerConnection.set("scm:git:git@github.com:a-sit-plus/KmmResult.git")
                        url.set("https://github.com/a-sit-plus/KmmResult")
                    }
                }
            }
        }
        repositories {
            mavenLocal() {
                signing.isRequired = false
            }
        }
    }

afterEvaluate {
    val signingTasks = tasks.withType<Sign>()
    if (signingTasks.isNotEmpty()) {
        logger.lifecycle("")
        logger.lifecycle("  Making signing tasks of project ${name} run after publish tasks")
        tasks.withType<AbstractPublishToMaven>().configureEach {
            mustRunAfter(*signingTasks.toTypedArray())
            logger.info("   * $name must now run after ${signingTasks.joinToString { it.name }}")
        }
        logger.info("")
    }
}

signing {
    val signingKeyId: String? by project
    val signingKey: String? by project
    val signingPassword: String? by project
    useInMemoryPgpKeys(signingKeyId, signingKey, signingPassword)
    sign(publishing.publications)
}

