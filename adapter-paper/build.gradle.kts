plugins {
    id("java")
    id("io.spring.dependency-management")
    id("io.papermc.paperweight.userdev") version "2.0.0-beta.18"
}

repositories {
    mavenCentral()
    maven {
        name = "papermc"
        url = uri("https://repo.papermc.io/repository/maven-public/")
    }
}

dependencies {
    implementation(project(":application"))

    compileOnly("io.papermc.paper:paper-api:1.21.4-R0.1-SNAPSHOT") {
        isChanging = false
    }
    paperweight.paperDevBundle("1.21.4-R0.1-SNAPSHOT")

    implementation("org.springframework.boot:spring-boot-starter-webflux")
}