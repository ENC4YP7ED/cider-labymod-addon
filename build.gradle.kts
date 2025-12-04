plugins {
    id("net.labymod.labygradle")
    id("net.labymod.labygradle.addon")
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
        minecraftVersion = "1.20<1.22"
        version = System.getenv().getOrDefault("VERSION", "1.0.0")
    }

    minecraft {
        registerVersion("1.20.1") {
            runs {
                getByName("client") {
                    // Development client configuration
                }
            }
        }
        registerVersion("1.20.2") {
            runs {
                getByName("client") {
                    // Development client configuration
                }
            }
        }
        registerVersion("1.20.4") {
            runs {
                getByName("client") {
                    // Development client configuration
                }
            }
        }
        registerVersion("1.20.6") {
            runs {
                getByName("client") {
                    // Development client configuration
                }
            }
        }
        registerVersion("1.21") {
            runs {
                getByName("client") {
                    // Development client configuration
                }
            }
        }
    }
}

subprojects {
    plugins.apply("net.labymod.labygradle")
    plugins.apply("net.labymod.labygradle.addon")

    group = rootProject.group
    version = rootProject.version
}
