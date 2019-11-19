import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.gradle.api.tasks.testing.logging.TestLogEvent
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("org.springframework.boot") version "2.2.0.RC1"
    id("io.spring.dependency-management") version "1.0.8.RELEASE"
    kotlin("jvm") version "1.3.31"
    kotlin("plugin.spring") version "1.3.31"
}

group = "com.liceu"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_1_8

configurations {
    compileOnly {
        extendsFrom(configurations.annotationProcessor.get())
    }
}

repositories {
    google()
    jcenter()
    mavenCentral()
    maven { url = uri("https://repo.spring.io/snapshot") }
    maven { url = uri("https://repo.spring.io/milestone") }
}

dependencyManagement {
    imports {
        mavenBom ("org.springframework.cloud:spring-cloud-dependencies:Hoxton.M3")
    }
}

dependencies {
    implementation(group = "com.google.cloud", name = "google-cloud-storage", version = "1.92.0")
    implementation(group = "com.google.auth", name = "google-auth-library-credentials", version = "0.16.2")
    implementation(group = "com.google.apis", name = "google-api-services-oauth2", version = "v2-rev151-1.25.0")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-data-mongodb")
    implementation("org.springframework.boot:spring-boot-starter-oauth2-resource-server")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("com.okta.spring:okta-spring-boot-starter:1.3.0")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("org.springframework.session:spring-session-core")
    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")
    testImplementation("org.springframework.boot:spring-boot-starter-test") {
    }
    testImplementation("de.flapdoodle.embed:de.flapdoodle.embed.mongo")
    testImplementation("org.springframework.security:spring-security-test")
    testImplementation("com.google.truth:truth:0.44")
    implementation("com.google.guava:guava:27.1-jre")
    testCompile(group = "com.google.guava", name = "guava-testlib", version = "27.1-jre")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.9.+")
    implementation(group = "net.logstash.logback", name = "logstash-logback-encoder", version = "5.3")
    implementation("io.jsonwebtoken:jjwt-api:0.10.5")
    runtime("io.jsonwebtoken:jjwt-impl:0.10.5")
    runtime("io.jsonwebtoken:jjwt-jackson:0.10.5")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.9.+")
    implementation(group = "org.springframework.security.oauth.boot", name = "spring-security-oauth2-autoconfigure", version = "2.2.0.RC1")
    implementation(group = "com.restfb", name = "restfb", version = "2.21.0")
    implementation(group = "com.maxmind.geoip2", name = "geoip2", version = "2.3.1")
    implementation ("com.google.code.gson:gson:2.8.5")
    implementation(group="khttp", name="khttp", version="1.0.0")
    implementation("org.elasticsearch.client:elasticsearch-rest-high-level-client:7.4.2")
    implementation("org.elasticsearch.client:elasticsearch-rest-client:7.4.2")
    implementation("org.elasticsearch:elasticsearch:7.4.2")
    implementation(group = "org.springframework.cloud", name = "spring-cloud-starter-netflix-hystrix", version = "2.2.0.RC1")

}

tasks.test {
    testLogging {
        events = setOf(TestLogEvent.FAILED, TestLogEvent.STANDARD_ERROR)
        exceptionFormat = TestExceptionFormat.FULL
    }
    failFast = true
}

tasks.withType<Test> {
    useJUnitPlatform()
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = "1.8"
    }
}
