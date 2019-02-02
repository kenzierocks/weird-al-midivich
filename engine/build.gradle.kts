import com.techshroom.inciseblue.InciseBlueExtension
import org.gradle.internal.impldep.org.junit.experimental.categories.Categories.CategoryFilter.include
import org.gradle.internal.jvm.Jvm
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmCompile

plugins {
    kotlin("jvm") version "1.3.11"
}

dependencies {
    "implementation"(kotlin("stdlib-jdk8"))
}

configure<InciseBlueExtension> {
    maven {
        projectDescription = "MIDI engine built on OpenAL."
        coords("kenzierocks", "weird-al-midivich")
        artifactName = "${rootProject.name}-${project.name}"
    }
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
