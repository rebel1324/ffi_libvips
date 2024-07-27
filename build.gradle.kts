import org.gradle.kotlin.dsl.support.kotlinCompilerOptions
import org.gradle.tooling.model.java.JavaRuntime

plugins {
    id("java")
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

tasks.withType(JavaCompile::class) {
    options.setIncremental(true)
    options.compilerArgs.add("-Xlint:all");
}

dependencies {
    //<editor-fold desc="Dependencies">
    // https://mvnrepository.com/artifact/org.slf4j/slf4j-api
    // https://mvnrepository.com/artifact/ch.qos.logback/logback-core
    // https://mvnrepository.com/artifact/ch.qos.logback/logback-classic
    implementation("com.github.jnr:jnr-ffi:2.2.16")
    implementation("ch.qos.logback:logback-classic:1.5.6")
    implementation("ch.qos.logback:logback-core:1.5.6")
    implementation("org.slf4j:slf4j-api:2.0.13")

    compileOnly("org.projectlombok:lombok:1.18.34")
    annotationProcessor("org.projectlombok:lombok:1.18.34")
    testCompileOnly("org.projectlombok:lombok:1.18.34")
    testAnnotationProcessor("org.projectlombok:lombok:1.18.34")


    // https://mvnrepository.com/artifact/io.projectreactor/reactor-core
    implementation("io.projectreactor:reactor-core:3.6.8")
    //</editor-fold>

    //<editor-fold desc="Test Dependencies">
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    //</editor-fold>
}

tasks.test {
    useJUnitPlatform()
}

tasks.withType(JavaExec::class) {
    mainClass.set("com.totally.unsafe.Main")
    classpath(sourceSets.main.get().runtimeClasspath)
    environment("PATH", "${project.rootDir}/vips/bin;${System.getenv("PATH")}") // remember, this is for windows.
}
