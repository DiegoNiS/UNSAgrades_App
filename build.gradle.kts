// Top-level build file where you can add configuration options common to all sub-projects/modules.
buildscript {
    repositories {
        google()
        mavenCentral()
    }
    dependencies {
        // Inyectamos la versión correcta de javapoet en el classpath
        // que usarán TODOS los plugins de Gradle, incluido Hilt.
        classpath("com.squareup:javapoet:1.13.0")
    }
}