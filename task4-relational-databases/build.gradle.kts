plugins {
    id("java")
    alias(libs.plugins.shadow)
}

dependencies {
    testImplementation(platform(libs.junit.bom))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testImplementation(libs.h2)
    implementation(libs.spring.context)
    implementation(libs.jackson.csv)
    implementation(libs.spring.aop)
    implementation(libs.spring.jdbc)
    implementation(libs.aspectj.weaver)
    implementation(libs.postgresql)
    implementation(libs.flyway.postgresql)

    compileOnly(libs.lombok)
    annotationProcessor(libs.lombok)

    testImplementation(libs.spring.test)


}

group = "dev.trela.testing"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}
tasks.test {
    useJUnitPlatform()
}


tasks.shadowJar {
    archiveClassifier.set("")
    manifest {
        attributes["Main-Class"] = "dev.trela.testing.Library3000App"
    }
}

tasks.register("checkPlugins") {
    doLast {
        println("Loaded plugins: " + project.plugins.map { it.javaClass.name })
    }
}
