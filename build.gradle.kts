import io.gitlab.arturbosch.detekt.Detekt
import org.gradle.kotlin.dsl.support.listFilesOrdered

plugins {
    kotlin("multiplatform") version "1.9.10"
    id("maven-publish")
    id("signing")
    id("io.github.gradle-nexus.publish-plugin") version "1.3.0"
    id("org.jetbrains.dokka") version "1.8.20"
    id("org.jetbrains.kotlinx.kover") version "0.7.3"
    id("io.gitlab.arturbosch.detekt") version "1.23.1"
}

val artifactVersion: String by extra
group = "at.asitplus"
version = artifactVersion

repositories {
    mavenCentral()
}

val dokkaOutputDir = "$projectDir/docs"
tasks.dokkaHtml {

    val moduleDesc = File("$rootDir/dokka-tmp.md").also { it.createNewFile() }
    val readme =
        File("${rootDir}/README.md").readText().replaceFirst("# ", "")
    val moduleTitle = readme.lines().first()
    moduleDesc.writeText("# Module $readme")
    moduleName.set(moduleTitle)

    dokkaSourceSets {
        named("commonMain") {

            includes.from(moduleDesc)
            sourceLink {
                localDirectory.set(file("src/$name/kotlin"))
                remoteUrl.set(
                    uri("https://github.com/a-sit-plus/kmmresult/tree/development/src/$name/kotlin").toURL()
                )
                // Suffix which is used to append the line number to the URL. Use #L for GitHub
                remoteLineSuffix.set("#L")
            }
        }
    }
    outputDirectory.set(file("${rootDir}/docs"))
    doLast {
        rootDir.listFilesOrdered { it.extension.lowercase() == "png" || it.extension.lowercase() == "svg" }
            .forEach { it.copyTo(File("$rootDir/docs/${it.name}"), overwrite = true) }
    }
}
val deleteDokkaOutputDir by tasks.register<Delete>("deleteDokkaOutputDirectory") {
    delete(dokkaOutputDir)
}
val javadocJar = tasks.register<Jar>("javadocJar") {
    dependsOn(deleteDokkaOutputDir, tasks.dokkaHtml)
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

    val xcf = org.jetbrains.kotlin.gradle.plugin.mpp.apple.XCFrameworkConfig(project, "KmmResult")
    macosArm64 {
        binaries.framework {
            baseName = "KmmResult"
            embedBitcode("bitcode")
            xcf.add(this)
        }
    }
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
    tvosX64 {
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
    iosSimulatorArm64 {
        binaries.framework {
            baseName = "KmmResult"
            embedBitcode("bitcode")
            xcf.add(this)
        }
    }

    jvmToolchain(11)
    jvm {
        compilations.all {
            kotlinOptions {
                freeCompilerArgs = listOf(
                    "-Xjsr305=strict"
                )
            }
        }
        withJava() //for Java Interop tests
    }
    js(IR) {
        browser { testTask { enabled = false } }
        nodejs()
    }
    linuxX64()
    linuxArm64()
    mingwX64()

    sourceSets {
        @Suppress("UNUSED_VARIABLE") val commonMain by getting

        @Suppress("UNUSED_VARIABLE") val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
            }
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

    dependencies {
        detektPlugins("io.gitlab.arturbosch.detekt:detekt-formatting:1.21.0")
    }

    repositories {
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
            mavenLocal() {
                signing.isRequired = false
            }
        }
    }
}

signing {
    val signingKeyId: String? by project
    val signingKey: String? by project
    val signingPassword: String? by project
    useInMemoryPgpKeys(signingKeyId, signingKey, signingPassword)
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

