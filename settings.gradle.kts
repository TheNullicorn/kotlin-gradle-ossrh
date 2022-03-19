rootProject.name = settings.extra["project.name"] as String

pluginManagement {
    plugins {
        val kotlinVersion = settings.extra["kotlin.version"] as String

        kotlin("multiplatform") version kotlinVersion
        id("org.jetbrains.dokka") version kotlinVersion
    }
}