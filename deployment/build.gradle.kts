plugins {
    id("com.avast.gradle.docker-compose") version "0.17.12"
}

dockerCompose {
    dockerExecutable.set("/usr/local/bin/docker")
}