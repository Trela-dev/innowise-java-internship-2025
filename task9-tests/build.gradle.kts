plugins {
    id("java")
    alias(libs.plugins.shadow)
    alias(libs.plugins.spring.boot)
    alias(libs.plugins.spring.dependency.management)
}

dependencies {
    implementation(libs.spring.boot.web)
    implementation(libs.spring.boot.jpa)
    implementation(libs.spring.boot.validation)
    implementation(libs.spring.boot.mongodb)
    implementation(libs.spring.boot.security)
    implementation(libs.liquibase.core)

    implementation(libs.spring.context)
    implementation(libs.jackson.csv)
    implementation(libs.spring.aop)
    implementation(libs.spring.orm)
    implementation(libs.spring.jdbc)
    implementation(libs.aspectj.weaver)
    implementation(libs.postgresql)
    implementation(libs.hibernate.core)
    implementation(libs.hibernate.jcache)
    implementation(libs.ehcache)
    implementation(libs.cache.api)

    implementation(libs.jjwt.api)
    runtimeOnly(libs.jjwt.impl)
    runtimeOnly(libs.jjwt.jackson)

    compileOnly(libs.lombok)
    annotationProcessor(libs.lombok)

    testImplementation(platform(libs.junit.bom))
    testImplementation(libs.junit.jupiter)
    testImplementation(libs.mockito.core)
    testImplementation(libs.mockito.junit.jupiter)
    testImplementation(libs.testcontainers.postgresql)
    testImplementation(libs.spring.test)
    testImplementation(libs.h2)
    testImplementation(libs.testcontainers.junit.jupiter)
    testImplementation(libs.spring.boot.test)

}

group = "dev.trela"
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
        attributes["Main-Class"] = "dev.trela.Library3000App"
    }
    mergeServiceFiles()
}

tasks.register("checkPlugins") {
    doLast {
        println("Loaded plugins: " + project.plugins.map { it.javaClass.name })
    }
}
