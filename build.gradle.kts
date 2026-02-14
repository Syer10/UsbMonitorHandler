plugins {
    kotlin("jvm") version "2.3.10"
    id("org.graalvm.buildtools.native") version "0.11.4"
}

group = "ca.gosyer.usbhandler"
version = "1.0.0"

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.10.2")

    implementation("com.github.ajalt.clikt:clikt:5.1.0")
    implementation("com.lordcodes.turtle:turtle:0.10.0")

    implementation("net.codecrete.usb:java-does-usb:1.2.0")

    testImplementation(kotlin("test"))
}

tasks {
    test {
        useJUnitPlatform()
    }

    register<Jar>("fatJar") {
        group = "build"
        duplicatesStrategy = DuplicatesStrategy.EXCLUDE

        manifest {
            attributes["Main-Class"] = "ca.gosyer.usbhandler.MainKt"
        }

        from(sourceSets.main.get().output)

        dependsOn(configurations.runtimeClasspath)
        from({
            configurations.runtimeClasspath.get().filter { it.name.endsWith("jar") }.map { zipTree(it) }
        })
    }
}

kotlin {
    jvmToolchain(23)
}

graalvmNative {
    binaries {
        named("main") {
            mainClass.set("ca.gosyer.usbhandler.MainKt")

            buildArgs.addAll(
                "-H:+UnlockExperimentalVMOptions",
                "-H:+ForeignAPISupport",
            )
            jvmArgs.add("--enable-native-access=ALL-UNNAMED")

            javaLauncher.set(javaToolchains.launcherFor {
                languageVersion.set(JavaLanguageVersion.of(25))
                vendor.set(JvmVendorSpec.matching("GraalVM Community"))
            })
        }
    }
}
