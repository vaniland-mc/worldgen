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
    private val config = config(
        server.consoleSender,
        "presets.yml",
        DefaultConfigResource(this, "presets.yml")
    )

    override fun onEnable() {
        registerCommand()
    }

    override fun getDefaultWorldGenerator(worldName: String, id: String?): ChunkGenerator {
        val presetName = config.section("presets")?.find { it == id }
        val overwriteBiomes = config.section("presets.$presetName")?.associate { beforeBiome ->
            val afterBiome = config.get("presets.${presetName}.${beforeBiome}", ConfigDataType.String)!!
                .let {
                    Biome.valueOf(it.uppercase())
                }
            Biome.valueOf(beforeBiome.uppercase()) to afterBiome
        } ?: emptyMap()

        return ListChunkGenerator(
            ListBiomeProvider(
                seed = Random.nextLong(),
                overwriteBiomes = overwriteBiomes,
                isLargeBiome = true
            )
        )
    }

    private fun registerCommand() {
        command("worldgen") {
            tab {
                argument {
                    add("reload")
                }
            }
            execute {
                when(args.lowerOrNull(0)) {
                    "reload" -> {
                        config.reload()
                        sender.sendMessage("コンフィグをリロードしました")
                    }
                    else -> {
                        sender.sendMessage("不明なコマンドです")
                    }
                }
            }
        }
    }
}
