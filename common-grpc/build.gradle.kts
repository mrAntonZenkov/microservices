import com.google.protobuf.gradle.id

plugins {
	java
	kotlin("jvm") version "1.9.10"
	id("org.springframework.boot") version "3.5.7"
	id("io.spring.dependency-management") version "1.1.7"
	id("com.google.protobuf") version "0.9.4"
}

group = "org.example"
version = "0.0.1-SNAPSHOT"
description = "common-grpc"

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

	// https://mvnrepository.com/artifact/io.grpc/grpc-bom
	implementation("io.grpc:grpc-bom:1.76.0")


	// https://mvnrepository.com/artifact/io.grpc/grpc-services
	runtimeOnly("io.grpc:grpc-services:1.76.0")
	implementation("net.devh:grpc-client-spring-boot-starter:3.1.0.RELEASE")
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation("org.springframework.grpc:spring-grpc-test")
	testRuntimeOnly("org.junit.platform:junit-platform-launcher")
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
