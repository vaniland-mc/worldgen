package land.vani.plugin.worldgen

import com.github.syari.spigot.api.command.command
import com.github.syari.spigot.api.command.tab.CommandTabArgument.Companion.argument
import com.github.syari.spigot.api.config.config
import com.github.syari.spigot.api.config.def.DefaultConfigResource
import com.github.syari.spigot.api.config.type.ConfigDataType
import org.bukkit.block.Biome
import org.bukkit.generator.ChunkGenerator
import org.bukkit.plugin.java.JavaPlugin
import kotlin.random.Random

class WorldGenPlugin: JavaPlugin() {
    private val presetConfig = config(
        server.consoleSender,
        "presets.yml",
        DefaultConfigResource(this, "presets.yml")
    )

    private val generatorsConfig = config(
        server.consoleSender,
        "generators.yml",
    )

    override fun onEnable() {
        registerCommand()
    }

    override fun getDefaultWorldGenerator(worldName: String, id: String?): ChunkGenerator {
        val worldSection = generatorsConfig.section("generators.$worldName")
        if(worldSection != null) {
            val preset = generatorsConfig.get("generators.${worldName}.preset", ConfigDataType.String)!!
            val seed = generatorsConfig.get("generators.${worldName}.seed", ConfigDataType.Long)!!
            return ListChunkGenerator(
                ListBiomeProvider(
                    seed = seed,
                    overwriteBiomes = getOverwriteBiomes(preset),
                    isLargeBiome = true
                )
            )
        }

        val presetName = presetConfig.section("presets")?.find { it == id }
        val seed = Random.nextLong()
        return ListChunkGenerator(
            ListBiomeProvider(
                seed = seed,
                overwriteBiomes = getOverwriteBiomes(presetName!!),
                isLargeBiome = true
            )
        ).also {
            generatorsConfig.set("generators.${worldName}.preset", ConfigDataType.String, presetName, true)
            generatorsConfig.set("generators.${worldName}.seed", ConfigDataType.Long, seed, true)
        }
    }

    private fun getOverwriteBiomes(presetName: String): Map<Biome, Biome> {
        return presetConfig.section("presets.$presetName")?.associate { beforeBiome ->
            val afterBiome = presetConfig.get("presets.${presetName}.${beforeBiome}", ConfigDataType.String)!!
                .let {
                    Biome.valueOf(it.uppercase())
                }
            Biome.valueOf(beforeBiome.uppercase()) to afterBiome
        } ?: emptyMap()
    }

    private fun registerCommand() {
        command("worldgen") {
            permission = "worldgen.command"

            tab {
                argument {
                    addAll("reload", "presets", "generator")
                }
                argument("generator") {
                    addAll(server.worlds.map { it.name })
                }
            }
            execute {
                when(args.lowerOrNull(0)) {
                    "reload" -> {
                        presetConfig.reload()
                        generatorsConfig.reload()
                        sender.sendMessage("コンフィグをリロードしました")
                    }
                    "presets" -> {
                        val presets = presetConfig.section("presets").orEmpty().joinToString(",")
                        sender.sendMessage(presets)
                    }
                    "generator" -> {
                        val worldName = args.lowerOrNull(1) ?: run {
                            sender.sendMessage("ワールドを指定してください")
                            return@execute
                        }
                        val world = server.getWorld(worldName) ?: run {
                            sender.sendMessage("ワールドが見つかりませんでした")
                            return@execute
                        }
                        sender.sendMessage("${world.generator?.javaClass?.name}")
                    }
                    else -> {
                        sender.sendMessage("不明なコマンドです")
                    }
                }
            }
        }
    }
}
