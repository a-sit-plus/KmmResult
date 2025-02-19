import org.gradle.kotlin.dsl.support.listFilesOrdered

plugins {
    base
    id("org.jetbrains.dokka")
    id("io.github.gradle-nexus.publish-plugin") version "1.3.0"
}

val artifactVersion: String by extra
group = "at.asitplus"
version = artifactVersion

dependencies {
    dokka(project(":kmmresult"))
    dokka(project(":kmmresult-test"))
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
