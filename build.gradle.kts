import io.spring.gradle.dependencymanagement.dsl.DependencyManagementExtension

plugins {
    id("java")
    id("io.spring.dependency-management") version "1.1.7" apply false
}

allprojects {
    group = "io.github.flux7k"
    version = "1.0.0-SNAPSHOT"
}

subprojects {
    afterEvaluate {
        extensions.findByType<DependencyManagementExtension>()?.imports {
            mavenBom("org.springframework.boot:spring-boot-dependencies:3.5.5")
        }

        extensions.findByType<JavaPluginExtension>()?.toolchain {
            languageVersion.set(JavaLanguageVersion.of(21))
        }
    }
}