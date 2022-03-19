apply(plugin = "signing")
apply(plugin = "maven-publish")
apply(plugin = "org.jetbrains.dokka")

///////////////////////////////////////////////////////////////////////////
// Read Project's Configuration
///////////////////////////////////////////////////////////////////////////

fun property(key: String): String = project.extra[key] as String

// Set in "../gradle.properties".
val authorName = property("author.username")
val authorEmail = property("author.email")
val projectName = property("project.name")

// Both values should be set in "~/.gradle/gradle.properties".
val ossrhUsername = property("ossrhUsername")
val ossrhPassword = property("ossrhPassword")

///////////////////////////////////////////////////////////////////////////
// Documentation HTML Jar
///////////////////////////////////////////////////////////////////////////

// Include jar with the lib's KDoc HTML.
val kdocJar by tasks.registering(Jar::class) {
    val htmlTask = tasks["dokkaHtml"]
    dependsOn(htmlTask)

    // Create the Jar from the generated HTML files.
    from(htmlTask)
    archiveClassifier.set("javadoc")
}

///////////////////////////////////////////////////////////////////////////
// Artifact Signing
///////////////////////////////////////////////////////////////////////////

// Sign all of our artifacts for Nexus.
configure<SigningExtension> {
    val publishing = extensions["publishing"] as PublishingExtension
    sign(publishing.publications)
}

///////////////////////////////////////////////////////////////////////////
// Publishing to OSSRH & Maven Central
///////////////////////////////////////////////////////////////////////////

configure<PublishingExtension> {
    // Add extra metadata for the JVM jar's pom.xml.
    publications.withType<MavenPublication> {
        artifact(kdocJar)

        pom {
            val projectUrl = "github.com/$authorName/$projectName"

            url.set("https://$projectUrl")
            name.set(projectName)
            description.set("Access a Minecraft account via Microsoft login")

            developers {
                developer {
                    name.set(authorName)
                    email.set(authorEmail)
                }
            }

            licenses {
                license {
                    name.set("MIT License")
                    url.set("https://opensource.org/licenses/mit-license.php")
                }
            }

            scm {
                url.set("https://$projectUrl/tree/main")
                connection.set("scm:git:git://$projectUrl.git")
                developerConnection.set("scm:git:ssh://$projectUrl.git")
            }
        }
    }

    repositories {
        maven {
            name = "ossrh"

            val repoId = if (version.toString().endsWith("SNAPSHOT")) "snapshot" else "release"
            url = uri(project.extra["repo.$repoId.url"] as String)

            credentials {
                username = ossrhUsername
                password = ossrhPassword
            }
        }
    }
}