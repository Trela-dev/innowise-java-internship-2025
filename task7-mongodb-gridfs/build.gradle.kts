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
    implementation(libs.liquibase.core)

    testImplementation(platform(libs.junit.bom))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testImplementation(libs.h2)
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

    compileOnly(libs.lombok)
    annotationProcessor(libs.lombok)

    testImplementation(libs.spring.test)


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
