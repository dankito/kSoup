plugins {
    kotlin("jvm") version "1.8.21"
}


repositories {
    mavenCentral()
}


java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(8))
    }
}


val junitVersion: String by project
val jettyVersion: String by project

dependencies {
    // javax.annotations.nonnull, with Apache 2 (not GPL) license. Build time only.
    compileOnly("com.google.code.findbugs:jsr305:3.0.2")

    testImplementation("org.junit.jupiter:junit-jupiter:$junitVersion")
    testImplementation("com.google.code.gson:gson:2.10.1")
    testImplementation("org.eclipse.jetty:jetty-server:$jettyVersion")
    testImplementation("org.eclipse.jetty:jetty-servlet:$jettyVersion")
}



tasks.named<Test>("test") {
    useJUnitPlatform()

    maxParallelForks = 4
//    maxHeapSize = "1G"

    testLogging {
        events("failed") //, "skipped", "passed"
    }
}