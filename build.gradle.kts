import io.gitlab.arturbosch.detekt.Detekt
import org.gradle.kotlin.dsl.support.listFilesOrdered
import org.jetbrains.kotlin.build.joinToReadableString

plugins {
    kotlin("multiplatform") version "2.0.0"
    id("maven-publish")
    id("signing")
    id("io.github.gradle-nexus.publish-plugin") version "1.3.0"
    id("org.jetbrains.dokka") version "1.9.20"
    id("org.jetbrains.kotlinx.kover") version "0.8.0"
    id("io.gitlab.arturbosch.detekt") version "1.23.6"
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
        File("${rootDir}/README.md").readText()
    val moduleTitle = project.name
    moduleDesc.writeText("# Module ${project.name}\n\n$readme")
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
    listOf(
        macosArm64(),
        macosX64(),
        tvosArm64(),
        tvosX64(),
        tvosSimulatorArm64(),
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).forEach {
        it.binaries.framework {
            baseName = "KmmResult"
            binaryOption("bundleId", "at.asitplus.KmmResult")
            embedBitcode("bitcode")
            xcf.add(this)
            isStatic = true
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
        commonMain.dependencies {
            implementation("io.arrow-kt:arrow-core:1.2.4")
        }

        commonTest.dependencies {
            implementation(kotlin("test"))
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
        detektPlugins("io.gitlab.arturbosch.detekt:detekt-formatting:1.23.6")
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

