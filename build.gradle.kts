plugins {
    id("org.springframework.boot") version "3.1.0-M2"
    id("io.spring.dependency-management") version "1.1.0"
    kotlin("jvm") version "1.8.20-RC2"
    kotlin("plugin.spring") version "1.8.20-RC2"
}

group = "dev.citc.sampes"
version = "0.0.1-SNAPSHOT"

configurations {
    all {
        resolutionStrategy.dependencySubstitution.all {
            val moduleSelector = requested as? ModuleComponentSelector
            if (moduleSelector?.module == "spring-boot-starter-tomcat") {
                useTarget(
                    "${moduleSelector.group}:spring-boot-starter-jetty:${moduleSelector.version}",
                    "We need Jetty"
                )
            }
            // do this in maven...
            /* Attempt to use Jetty 12 failed autoconfiguration
            if (moduleSelector?.group == "org.eclipse.jetty") {
                val notation = when (moduleSelector.module) {
                    "jetty-servlets" -> "org.eclipse.jetty.ee10:jetty-ee10-servlets"
                    "jetty-servlet" -> "org.eclipse.jetty.ee10:jetty-ee10-servlet"
                    "jetty-annotations" -> "org.eclipse.jetty.ee10:jetty-ee10-annotations"
                    "jetty-webapp" -> "org.eclipse.jetty.ee10:jetty-ee10-webapp"
                    else -> "${moduleSelector.group}:${moduleSelector.module}"
                }
                useTarget("$notation:12.0.0.beta0")
            }
            */
        }
    }
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-data-jdbc")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.liquibase:liquibase-core")

    // minimal non synchronized version - remove when Boot 3.2 released
    runtimeOnly("org.postgresql:postgresql:42.6.0")
    // TODO https://github.com/spring-projects/spring-framework/issues/29585
    runtimeOnly("org.eclipse.jetty.toolchain:jetty-jakarta-servlet-api:5.0.2")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("io.zonky.test:embedded-database-spring-test:2.2.0")
    testImplementation(platform("io.zonky.test.postgres:embedded-postgres-binaries-bom:15.2.0"))
    testImplementation("io.zonky.test:embedded-postgres:2.0.3")
}

java {
    toolchain {
        // TODO use 20 when kotlin supports it
        languageVersion.set(JavaLanguageVersion.of(19))
        vendor.set(JvmVendorSpec.AZUL)
    }
}

tasks {
    withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
        kotlinOptions {
            freeCompilerArgs = listOf("-Xjsr305=strict")
        }
    }
    withType<Test> {
        useJUnitPlatform()
    }
}

repositories {
    mavenCentral()
    maven { url = uri("https://repo.spring.io/milestone") }
}