plugins {
	java
	id("org.springframework.boot") version "3.5.6"
	id("io.spring.dependency-management") version "1.1.7"
	id("com.github.davidmc24.gradle.plugin.avro") version "1.9.0"
}

group = "auth-service"
version = "0.0.1-SNAPSHOT"
description = "auth-service"

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(17)
	}
}

repositories {
	mavenCentral()
	maven { url = uri("https://packages.confluent.io/maven/") }
}

dependencies {
	implementation("org.postgresql:postgresql:42.7.8")
	implementation("org.projectlombok:lombok:1.18.42")
	implementation("org.springframework.boot:spring-boot-starter-actuator")
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")
	implementation("org.springframework.boot:spring-boot-starter-oauth2-client")
	implementation("org.springframework.boot:spring-boot-starter-oauth2-resource-server")
	implementation("org.springframework.boot:spring-boot-starter-security")
	implementation("org.springframework.boot:spring-boot-starter-validation")
	implementation("org.springframework.boot:spring-boot-starter-web")

	implementation("org.apache.kafka:kafka-clients:4.1.0")
	implementation("org.springframework.kafka:spring-kafka")

	implementation("org.apache.avro:avro:1.12.0")
	implementation("io.confluent:kafka-avro-serializer:8.0.0")


	implementation("io.jsonwebtoken:jjwt-api:0.11.5")
	runtimeOnly("io.jsonwebtoken:jjwt-impl:0.11.5")
	runtimeOnly("io.jsonwebtoken:jjwt-jackson:0.11.5")
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation("org.springframework.boot:spring-boot-testcontainers")
	testImplementation("org.springframework.kafka:spring-kafka-test")
	testImplementation("org.springframework.security:spring-security-test")
	testImplementation("org.testcontainers:postgresql:1.19.1")
	testImplementation("org.testcontainers:junit-jupiter")
	testImplementation("org.testcontainers:kafka")
	testRuntimeOnly("org.junit.platform:junit-platform-launcher")
	testImplementation("com.h2database:h2")
}

tasks.withType<Test> {
	useJUnitPlatform()
}

avro {
	isCreateSetters = false
	isCreateOptionalGetters = false
	isGettersReturnOptional = false
	isOptionalGettersForNullableFieldsOnly = false
	fieldVisibility = "PRIVATE"
	outputCharacterEncoding = "UTF-8"
	stringType = "String"
}

tasks.named("compileJava") {
	dependsOn("generateAvroJava")
}
sourceSets {
	main {
		java {
			srcDir("build/generated-sources/avro/main/java")
		}
	}
}

tasks.named<JavaCompile>("compileJava") {
	dependsOn("generateAvroJava")
	source(file("build/generated/sources/avro/main"))
}