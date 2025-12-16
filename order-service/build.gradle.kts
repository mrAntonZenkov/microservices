import com.google.protobuf.gradle.id

plugins {
	java
	id("org.springframework.boot") version "3.5.7"
	id("io.spring.dependency-management") version "1.1.7"
	id("com.google.protobuf") version "0.9.4"
	id("com.github.davidmc24.gradle.plugin.avro") version "1.9.1"
}

group = "org.example"
version = "0.0.1-SNAPSHOT"
description = "order-service"

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(17)
	}
}

repositories {
	mavenCentral()
}

extra["springGrpcVersion"] = "0.12.0"

dependencies {

	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")
	implementation("org.springframework.boot:spring-boot-starter-security")
	implementation("org.springframework.boot:spring-boot-starter-validation")
	implementation("org.springframework.boot:spring-boot-starter-data-redis")

	implementation("org.mapstruct:mapstruct:1.5.5.Final")
	annotationProcessor("org.mapstruct:mapstruct-processor:1.5.5.Final")

	implementation(project(":common-grpc"))
	implementation("org.springframework.grpc:spring-grpc-server-web-spring-boot-starter")
	implementation("net.devh:grpc-client-spring-boot-starter:3.1.0.RELEASE")

	implementation("org.springframework.kafka:spring-kafka")
	implementation("io.confluent:kafka-avro-serializer:7.5.1")
	implementation("org.apache.avro:avro:1.11.3")

	implementation("org.springframework.boot:spring-boot-starter-actuator")
	implementation("io.micrometer:micrometer-tracing-bridge-brave")
	implementation("io.zipkin.reporter2:zipkin-reporter-brave")

	implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.3.0")

	runtimeOnly("org.postgresql:postgresql")

	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation("org.springframework.grpc:spring-grpc-test")
	testImplementation("org.springframework.kafka:spring-kafka-test")
	testImplementation("org.testcontainers:testcontainers")
	testImplementation("org.testcontainers:postgresql")
	testImplementation("org.testcontainers:kafka")
	testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

avro {
	setCreateSetters(false)
	setFieldVisibility("PRIVATE")
	isEnableDecimalLogicalType = true
}

dependencyManagement {
	imports {
		mavenBom("org.springframework.grpc:spring-grpc-dependencies:${property("springGrpcVersion")}")
	}
}

protobuf {
	protoc {
		artifact = "com.google.protobuf:protoc"
	}
	plugins {
		id("grpc") {
			artifact = "io.grpc:protoc-gen-grpc-java"
		}
	}
	generateProtoTasks {
		all().forEach {
			it.plugins {
				id("grpc") {
					option("@generated=omit")
				}
			}
		}
	}
}

tasks.withType<Test> {
	useJUnitPlatform()
}
