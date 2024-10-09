plugins {base
    kotlin("multiplatform") version "2.0.0" apply false
    id("org.jetbrains.dokka")
    id("io.github.gradle-nexus.publish-plugin") version "1.3.0"
}

val artifactVersion: String by extra
group = "at.asitplus"
version = artifactVersion

dependencies {
    dokka(project(":kmmresult"))
}



nexusPublishing {
    repositories {
        sonatype {
            nexusUrl.set(uri("https://s01.oss.sonatype.org/service/local/"))
            snapshotRepositoryUrl.set(uri("https://s01.oss.sonatype.org/content/repositories/snapshots/"))
        }
    }
}
