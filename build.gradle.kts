plugins {
    application
    id("org.openjfx.javafxplugin") version "0.0.14"
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}

application {
    mainClass.set("MainLauncher.main")
}

javafx {
    version = "17.0.16"
    modules = listOf("javafx.controls", "javafx.fxml")
}


dependencies {
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    implementation("org.postgresql:postgresql:42.7.7")
    implementation("org.springframework.security:spring-security-crypto:5.7.1")
    implementation("commons-logging:commons-logging:1.2")
}

tasks.test {
    useJUnitPlatform()
}