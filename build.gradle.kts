import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("multiplatform") version "1.3.61"
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

tasks.withType<KotlinCompile>{
    kotlinOptions.freeCompilerArgs += "-Xuse-experimental=kotlin.ExperimentalUnsignedTypes"
}

kotlin {
    /* Targets configuration omitted. 
    *  To find out how to configure the targets, please follow the link:
    *  https://kotlinlang.org/docs/reference/building-mpp-with-gradle.html#setting-up-targets */
    linuxX64("linux") {
        binaries {
            executable(buildTypes = setOf(DEBUG, RELEASE)) {
               entryPoint = "main"
            }
        }
        compilations.getByName("main") {
            val sdl2 by cinterops.creating {
                // Package to place the Kotlin API generated.
                packageName("sdl2")

                // Options to be passed to compiler by cinterop tool.
                compilerOpts("-I/usr/include/SDL2")

                // Directories to look for headers.

                // A shortcut for includeDirs.allHeaders.
                includeDirs("/usr/include/SDL2")
            }
        }
    }

    sourceSets {
        linuxX64("linux").compilations["main"].defaultSourceSet { /* ... */ }
        linuxX64("linux").compilations["test"].defaultSourceSet { /* ... */ }
    }
}
