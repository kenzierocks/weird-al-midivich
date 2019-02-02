import com.techshroom.inciseblue.InciseBlueExtension
import com.techshroom.inciseblue.commonLib
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmCompile

plugins {
    kotlin("jvm") version "1.3.20"
}

dependencies {
    "api"(kotlin("stdlib-jdk8"))
    "implementation"("com.google.guava", "guava", "27.0.1-jre")
    commonLib("org.jetbrains.kotlinx", "kotlinx-coroutines", "1.1.1") {
        "api"(lib("core"))
    }
    "api"("org.jetbrains.kotlinx", "kotlinx-coroutines-io", "0.1.4")
    "api"("org.jetbrains.kotlinx", "kotlinx-coroutines-io-jvm", "0.1.4")
    "implementation"("org.slf4j", "slf4j-api", "1.7.25")

    "testImplementation"(kotlin("test-junit5"))
}

configure<InciseBlueExtension> {
    maven {
        projectDescription = "MIDI engine built on OpenAL."
        coords("kenzierocks", "weird-al-midivich")
        artifactName = "${rootProject.name}-${project.name}"
    }

    lwjgl {
        lwjglVersion = "3.2.1"
        addDependency("")
        addDependency("openal")
    }

    util.enableJUnit5()
}

plugins.withId("maven-publish") {
    rootProject.tasks.named("afterReleaseBuild").configure {
        dependsOn(tasks.named("publish"))
    }
}


tasks.withType<KotlinJvmCompile> {
    kotlinOptions {
        jvmTarget = "1.8"
    }
}
