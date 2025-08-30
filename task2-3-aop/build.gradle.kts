plugins {
    id("java")
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

group = "dev.trela.testing"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    // https://mvnrepository.com/artifact/org.springframework/spring-context
    implementation("org.springframework:spring-context:6.2.3")
    // https://mvnrepository.com/artifact/com.fasterxml.jackson.dataformat/jackson-dataformat-csv
    implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-csv:2.18.2")
    // https://mvnrepository.com/artifact/org.springframework.boot/spring-boot-starter-aop
    implementation("org.springframework.boot:spring-boot-starter-aop:3.4.3")
    testImplementation("org.springframework:spring-test:6.2.3")



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
