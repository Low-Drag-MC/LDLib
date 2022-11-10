plugins {
    id("dev.architectury.loom").version("1.0.+")
    id("maven-publish")
}


loom {
    silentMojangMappingsLicense()
    forge {
        mixin {
            mixinConfig(mixin_json_name)
        }

        dataGen {
            mod(mod_id)
        }
    }

    runConfigs.forEach {
        if (enable_mixin_debug) it.vmArg("-Dmixin.debug=true")
        it.vmArg("-Dmixin.debug.export=true")
        it.vmArg("-Dmixin.dumpTargetOnFailure=true")
        it.vmArg("-Dmixin.checks.interfaces=true")
        it.vmArg("-Dmixin.hotSwap=true")
        if (enable_hot_swap_agent) {
            it.vmArg("-XX:+AllowEnhancedClassRedefinition")
            it.vmArg("-XX:HotswapAgent=fatjar")
        }
    }

    runConfigs.getByName("data").programArg("--existing ${file("src/main/resources").absoluteFile}")

}

repositories {
    mavenCentral()
    maven("https://maven.shedaniel.me")
    maven {
        // location of the maven that hosts JEI files
        name = "Progwml6 maven"
        url = uri("https://dvs1.progwml6.com/files/maven/")
    }
    maven {
        // location of a maven mirror for JEI files, as a fallback
        name = "ModMaven"
        url = uri("https://modmaven.dev")
    }
    maven {
        url = uri("https://www.cursemaven.com")
        content {
            includeGroup("curse.maven")
        }
    }
    maven {
        name = ("ParchmentMC")
        url = uri("https://maven.parchmentmc.org")
    }
}

dependencies {
    minecraft("com.mojang:minecraft:$minecraft_version")

    forge("net.minecraftforge:forge:$forge_version")

    mappings(loom.layered {
        officialMojangMappings()
        parchment("org.parchmentmc.data:parchment-$parchment_version@zip")
    })

    //JEI
    modImplementation("mezz.jei:jei-$minecraft_version-forge:$jei_version"){
        isTransitive = false
    }
    //REI
    modCompileOnly("me.shedaniel:RoughlyEnoughItems-default-plugin-forge:$rei_version")
    modImplementation("me.shedaniel:RoughlyEnoughItems-forge:$rei_version")

    modCompileOnly("dev.architectury:architectury-forge:$architectury_version")
    modCompileOnly("me.shedaniel.cloth:cloth-config-forge:$cloth_config_version")

}


tasks.processResources {
    filesMatching("META-INF/mods.toml") {
        expand("version" to project.version)
    }
}

tasks.compileJava {
    options.encoding = "UTF-8"
    options.release.set(17)
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
    withSourcesJar()
}

base {
    archivesName.set(mod_name)
    group = maven_group
    version = "$minecraft_version-$mod_version"
}

publishing {
    publications {
        create<MavenPublication>(mod_name) {
            group = project.group
            version = project.version.toString()
            artifact("jar")
            artifact("sourceJar")
        }
    }
    repositories {
        maven {
            url = uri("https://maven.firstdarkdev.xyz/snapshots")
            credentials {
                username = System.getenv("MAVEN_USER")
                password = System.getenv("MAVEN_PASS")
            }
        }
    }
}