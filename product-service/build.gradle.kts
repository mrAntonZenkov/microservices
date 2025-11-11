import com.google.protobuf.gradle.id

plugins {
    java
    id("java")
    id("org.springframework.boot") version "3.5.7"
    id("io.spring.dependency-management") version "1.1.7"
    id("com.google.protobuf") version "0.9.4"
    id("com.github.davidmc24.gradle.plugin.avro") version "1.9.0"
}

group = "org.example"
version = "0.0.1-SNAPSHOT"
description = "product-service"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(17)
    }
}

repositories {
    mavenCentral()
    maven { url = uri("https://packages.confluent.io/maven/") }
}

extra["springGrpcVersion"] = "0.11.0"

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-data-mongodb")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("io.grpc:grpc-services")
    implementation("org.springframework.grpc:spring-grpc-server-web-spring-boot-starter")

    implementation("org.apache.kafka:kafka-clients:4.1.0")
    implementation("org.springframework.kafka:spring-kafka")
    implementation("org.apache.avro:avro:1.12.0")
    implementation("io.confluent:kafka-avro-serializer:8.0.0")

    implementation("io.grpc:grpc-stub:1.76.0")
    implementation("io.grpc:grpc-protobuf:1.76.0")
    implementation("io.grpc:grpc-api:1.76.0")

    implementation("io.grpc:grpc-netty:1.76.0")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.grpc:spring-grpc-test")
    testImplementation("org.springframework.kafka:spring-kafka-test")
    testImplementation("org.springframework.security:spring-security-test")
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
        all().forEach {task -> task.plugins { id("grpc")}
        }
    }
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