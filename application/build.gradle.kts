plugins {
    id("java")
    id("io.spring.dependency-management")
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":domain"))

    implementation("org.springframework.boot:spring-boot-starter-webflux")
    implementation("org.springframework:spring-tx")
}