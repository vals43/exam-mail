#!/bin/sh
set -e

BUILD_FILE="build.gradle"
MARKER="testImplementation 'org.junit-pioneer:junit-pioneer:2.2.0'"

if grep -q "spring-boot-starter-data-jpa" "$BUILD_FILE"; then
  echo "JPA deps already present, skipping."
  exit 0
fi

sed -i "/$MARKER/a\\
    implementation 'org.springframework.boot:spring-boot-starter-data-jpa:3.2.2'\\
    implementation 'org.postgresql:postgresql:42.7.1'\\
    implementation 'org.flywaydb:flyway-core:9.22.3'\\
    runtimeOnly 'org.flywaydb:flyway-database-postgresql:10.7.1'\\
    testRuntimeOnly 'com.h2database:h2:2.2.224'" "$BUILD_FILE"

echo "JPA deps added to $BUILD_FILE"
