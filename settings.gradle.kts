pluginManagement {
    repositories {
        maven("https://repo.labymod.net/repository/gradle-plugins/")
        gradlePluginPortal()
        mavenCentral()
    }

    plugins {
        id("net.labymod.gradle") version "0.3.28"
        id("net.labymod.gradle.addon") version "0.3.28"
    }

    buildscript {
        repositories {
            maven("https://repo.labymod.net/repository/gradle-plugins/")
            gradlePluginPortal()
        }
    }
}

rootProject.name = "cider"
