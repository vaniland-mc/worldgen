package land.vani.plugin.worldgen

import org.bukkit.block.Biome
import org.bukkit.configuration.file.YamlConfiguration
import kotlin.io.path.div
import kotlin.io.path.reader

class PresetsConfig(
    plugin: WorldGenPlugin,
) {
    private val configPath = plugin.dataFolder.toPath() / "presets.yml"
    private val config = YamlConfiguration()

    fun reload() {
        config.load(configPath.reader())
    }

    @Suppress("UNCHECKED_CAST")
    val presets: Map<String, Preset>
        get() = config.getValues(false)
            .mapValues { (_, value) ->
                value as Map<String, Any>
                val allowedBiomes = (value["allowedBiomes"] as List<String>).map {
                    Biome.valueOf(it.uppercase())
                }
                Preset(allowedBiomes)
            }
}

data class Preset(
    val allowedBiomes: List<Biome>,
)
