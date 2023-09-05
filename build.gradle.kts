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

    // psp-commons
    implementation(files("lib/psp-commons.jar"))
    runtimeOnly("org.apache.logging.log4j:log4j-api:2.18.0")
    implementation("org.apache.logging.log4j:log4j-slf4j-impl:2.18.0")
    implementation("org.apache.logging.log4j:log4j-core:2.18.0")
    implementation("org.fusesource.jansi:jansi:2.4.0")
    implementation("org.jetbrains.kotlin:kotlin-reflect:1.8.20")
    implementation("com.google.code.gson:gson:2.9.0")

    // cache
    implementation("org.apache.commons:commons-compress:1.23.0")

    // cs2
    implementation("com.displee:disio:2.2")
    implementation("com.displee:rs-cache-library:6.5")
    implementation("org.apache.commons:commons-lang3:3.13.0")
    implementation("org.fxmisc.richtext:richtextfx:0.10.8")
    implementation("org.fxmisc.flowless:flowless:0.6.4")

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

tasks {
    compileJava {
        options.encoding = "UTF-8"
    }
    withType(JavaCompile::class) {
        options.encoding = "UTF-8"
    }
}
