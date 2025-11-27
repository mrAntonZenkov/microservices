import com.google.protobuf.gradle.id

plugins {
    `java-library`
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

val grpcVersion = "1.76.0"


dependencies {
    api("io.grpc:grpc-stub:${grpcVersion}")
    api("io.grpc:grpc-protobuf:${grpcVersion}")
    api("io.grpc:grpc-services:${grpcVersion}")
    api("io.grpc:grpc-netty:${grpcVersion}")

    compileOnly("javax.annotation:javax.annotation-api:1.3.2")
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

protobuf {
    protoc {
        artifact = "com.google.protobuf:protoc:3.25.1"
    }
    plugins {
        create("grpc") {
            artifact = "io.grpc:protoc-gen-grpc-java:${grpcVersion}"
        }
    }
    generateProtoTasks {
        all().forEach { task ->
            task.plugins {
                create("grpc")
            }
        }
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}
