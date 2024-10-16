import io.gitlab.arturbosch.detekt.Detekt
import java.net.URI

plugins {
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
                    URI("https://github.com/a-sit-plus/kmmresult/tree/main/src/$name/kotlin")
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
        commonTest.dependencies {
            implementation(kotlin("test"))
        }
    }
    sourceSets.filterNot { it.name.startsWith("common") || it.name.startsWith("jvm")  }.filter { it.name.endsWith("Main") }.forEach {srcSet->
        println(srcSet.name)
        srcSet.kotlin.srcDir("$projectDir/src/nonJvmMain/kotlin")
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

