import org.gradle.kotlin.dsl.support.listFilesOrdered

plugins {
    kotlin("multiplatform") version "2.1.0" apply false
    id("com.android.library") version "8.2.2" apply false
    id("org.jetbrains.dokka") version "2.0.0"
    id("io.github.gradle-nexus.publish-plugin") version "1.3.0"
    id("org.jetbrains.kotlinx.binary-compatibility-validator") version "0.17.0"
}

val artifactVersion: String by extra
group = "at.asitplus"
version = artifactVersion

dependencies {
    dokka(project(":kmmresult"))
    dokka(project(":kmmresult-test"))
}

apiValidation {
    @OptIn(kotlinx.validation.ExperimentalBCVApi::class)
    klib {
        enabled = true
    }
}

dokka {
    val moduleDesc = File("$rootDir/dokka-tmp.md").also { it.createNewFile() }
    val readme =
        File("${rootDir}/README.md").readText()
    moduleDesc.writeText("\n\n$readme")
    moduleName.set("KmmResult")

    basePublicationsDirectory.set(file("${rootDir}/docs"))
    dokkaPublications.html {
        includes.from(moduleDesc)
    }
}

tasks.dokkaGenerate {
    doLast {
        rootDir.listFilesOrdered { it.extension.lowercase() == "png" || it.extension.lowercase() == "svg" }
            .forEach { it.copyTo(File("$rootDir/docs/html/${it.name}"), overwrite = true) }

    }
}

nexusPublishing {
    repositories {
        sonatype {
            nexusUrl.set(uri("https://s01.oss.sonatype.org/service/local/"))
            snapshotRepositoryUrl.set(uri("https://s01.oss.sonatype.org/content/repositories/snapshots/"))
        }
    }
}
