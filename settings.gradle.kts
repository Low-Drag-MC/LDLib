pluginManagement {
    repositories {
        mavenCentral()
        maven("https://maven.fabricmc.net/")
        maven("https://maven.architectury.dev/")
        maven {
            name = ("ParchmentMC")
            url = uri("https://maven.parchmentmc.org")
        }
        maven("https://files.minecraftforge.net/maven/")
        gradlePluginPortal()
    }
}
