import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("multiplatform") version "1.8.22"
}


group = "net.dankito"
version = "1.0.0-SNAPSHOT"


repositories {
    mavenCentral()
    // TODO: remove again as soon as the final release of kmp-base is available
    mavenLocal()
    maven("https://s01.oss.sonatype.org/content/repositories/snapshots/")
}


kotlin {
    // Enable the default target hierarchy:
    targetHierarchy.default()

    jvm {
//        jvmToolchain(8)
        withJava()
        testRuns["test"].executionTask.configure {
            useJUnitPlatform()
        }
    }

    js(IR) {
        moduleName = "ksoup"
        binaries.executable()

        browser {
            commonWebpackConfig {
                cssSupport {
//                    enabled.set(true)
                }
            }
            testTask {
                useKarma {
//                    useChromeHeadless()
                    useFirefoxHeadless()
                }
            }
        }

        nodejs {

        }
    }


    linuxX64()
    mingwX64()


    ios {
        binaries {
            framework {
                baseName = "stopwatch"
            }
        }
    }
    iosSimulatorArm64()
    macosX64()
    macosArm64()
    watchos()
    watchosSimulatorArm64()
    tvos()
    tvosSimulatorArm64()


    sourceSets {
        val junitVersion: String by project
        val jettyVersion: String by project

        val commonMain by getting {
            dependencies {
                implementation("net.codinux.kotlin:kmp-base:0.1.0-SNAPSHOT")
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
            }
        }

        val jvmMain by getting
        val jvmTest by getting {
            dependencies {
                implementation("org.junit.jupiter:junit-jupiter:$junitVersion")
                implementation("com.google.code.gson:gson:2.10.1")
                implementation("org.eclipse.jetty:jetty-server:$jettyVersion")
                implementation("org.eclipse.jetty:jetty-servlet:$jettyVersion")
            }
        }
    }
}


tasks.named<Test>("jvmTest") {
    useJUnitPlatform()

    maxParallelForks = 4
//    maxHeapSize = "1G"

    filter {
        isFailOnNoMatchingTests = false
    }
    testLogging {
        showExceptions = true
        showStandardStreams = true
        events = setOf(
            org.gradle.api.tasks.testing.logging.TestLogEvent.FAILED,
            org.gradle.api.tasks.testing.logging.TestLogEvent.PASSED
        )
        exceptionFormat = org.gradle.api.tasks.testing.logging.TestExceptionFormat.FULL
    }
}



ext["projectDescription"] = "Port of the popular Jsoup library for Kotlin Multiplatform"
ext["sourceCodeRepositoryBaseUrl"] = "github.com/dankito/kSoup"

val publishingScript = File(File(project.gradle.gradleUserHomeDir, "scripts"), "publish-dankito.gradle.kts")
if (publishingScript.exists()) {
    apply(from = publishingScript)
}
