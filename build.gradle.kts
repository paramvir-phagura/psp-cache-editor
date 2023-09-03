plugins {
    kotlin("jvm") version "1.9.0"
    application
}

group = "com.psp"
version = "unspecified"

repositories {
    mavenCentral()
}

dependencies {
    implementation("no.tornado:tornadofx:1.7.20")
    implementation(files("lib/psp-commons.jar"))
    runtimeOnly("org.apache.logging.log4j:log4j-api:2.18.0")
    implementation("org.apache.logging.log4j:log4j-slf4j-impl:2.18.0")
    implementation("org.apache.logging.log4j:log4j-core:2.18.0")
    implementation("org.fusesource.jansi:jansi:2.4.0")
    implementation("org.jetbrains.kotlin:kotlin-reflect:1.8.20")
    implementation("com.google.code.gson:gson:2.9.0")
    testImplementation(kotlin("test"))
}

application {
    mainClass.set("com.psp.PspCacheEditor")
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(8)
}
