plugins {
    id("java-library")
    id("net.labymod.gradle")
    id("net.labymod.gradle.addon")
}

group = "net.labymod.addons"
version = "1.0.0"

java.toolchain.languageVersion.set(JavaLanguageVersion.of(17))

labyMod {
    defaultPackageName = "net.labymod.addons.cider.core"

    addonInfo {
        namespace = "cider"
        displayName = "Cider"
        author = "Custom"
        description = "Display your Apple Music now playing from Cider in Minecraft"
        minecraftVersion = "1.20<1.21"
        version = System.getenv().getOrDefault("VERSION", "1.0.0")
    }

    minecraft {
        registerVersion("1.20.1", "1.20.1") {
            runs {
                getByName("client") {
                    // Development client configuration
                }
            }
        }
    }

    addonDev {
        productionRelease()
    }
}

dependencies {
    // Gson for JSON parsing
    api("com.google.code.gson:gson:2.10.1")
}

subprojects {
    plugins.apply("java-library")
    plugins.apply("net.labymod.gradle")
    plugins.apply("net.labymod.gradle.addon")

    repositories {
        maven("https://repo.labymod.net/repository/maven-public/")
        mavenCentral()
    }
}
