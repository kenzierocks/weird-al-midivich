import com.techshroom.inciseblue.InciseBlueExtension
import net.researchgate.release.ReleaseExtension
import org.gradle.plugins.ide.idea.model.IdeaModel

plugins {
    id("net.researchgate.release") version "2.8.0"
    id("com.techshroom.incise-blue") version "0.3.13"
    idea
}

inciseBlue.ide()

val arb = tasks.named("afterReleaseBuild")
subprojects {
    apply(plugin = "com.techshroom.incise-blue")
    apply(plugin = "java")

    configure<InciseBlueExtension> {
        ide()
        license()
        util {
            setJavaVersion("1.8")
            setKotlinJvmTarget = false
        }
    }
}

tasks.register("build") {
    dependsOn(subprojects.map { it.tasks.named("build") })
}
