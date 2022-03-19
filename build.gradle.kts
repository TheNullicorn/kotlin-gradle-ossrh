@file:Suppress("UNUSED_VARIABLE")

fun property(key: String): String = project.extra[key] as String

plugins {
    kotlin("multiplatform")

    // Documentation generator.
    id("org.jetbrains.dokka")

    // Publishing to Sonatype & Maven Central.
    id("java")
    id("signing")
    id("maven-publish")
}

group = property("project.group_id")
version = "0.0.1-SNAPSHOT"

repositories {
    mavenCentral()
}

kotlin {
    // Java target.
    jvm {
        withJava()

        compilations.all {
            kotlinOptions.jvmTarget = "1.8"
        }

        testRuns["test"].executionTask.configure {
            useJUnitPlatform()
        }
    }

    // JavaScript target.
    js(BOTH) {
        nodejs()
        browser()
    }

    // Native target.
    val hostOs = System.getProperty("os.name")
    val isMingwX64 = hostOs.startsWith("Windows")
    val nativeTarget = when {
        hostOs == "Mac OS X" -> macosX64("native")
        hostOs == "Linux" -> linuxX64("native")
        isMingwX64 -> mingwX64("native")
        else -> throw GradleException("Host OS is not supported in Kotlin/Native.")
    }

    sourceSets {
        val commonMain by getting
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
            }
        }

        val jvmMain by getting
        val jvmTest by getting

        val jsMain by getting
        val jsTest by getting

        val nativeMain by getting
        val nativeTest by getting
    }
}

// Include package-level documentation & sample code in generated dokka.
tasks.dokkaHtml.configure {
    dokkaSourceSets {
        val commonMain by getting {
            val dokkaBase = File(projectDir, "src/commonMain/resources")

            // Code snippets available to the @sample tag in KDoc.
            val sampleFiles = File(dokkaBase, "samples")
                .walk()
                .maxDepth(1)
                .filter { it.extension == "kt" }

            // Package-level documentation.
            val packageDocFiles = File(dokkaBase, "package_docs")
                .walk()
                .maxDepth(1)
                .filter { it.extension == "md" }

            samples.from(*sampleFiles.toList().toTypedArray())
            includes.from(*packageDocFiles.toList().toTypedArray())
        }
    }
}

// Load the OSSRH publishing script.
apply(from = "gradle/publish.gradle.kts")
