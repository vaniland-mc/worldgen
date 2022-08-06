package land.vani.plugin.worldgen

import org.bukkit.generator.BiomeProvider
import org.bukkit.generator.ChunkGenerator
import org.bukkit.generator.WorldInfo

class ListChunkGenerator(
    private val biomeProvider: ListBiomeProvider,
) : ChunkGenerator() {
    override fun getDefaultBiomeProvider(worldInfo: WorldInfo): BiomeProvider = biomeProvider

    override fun shouldGenerateNoise(): Boolean = true
    override fun shouldGenerateSurface(): Boolean = true
    override fun shouldGenerateBedrock(): Boolean = true
    override fun shouldGenerateCaves(): Boolean = true
    override fun shouldGenerateDecorations(): Boolean = true
    override fun shouldGenerateMobs(): Boolean = true
    override fun shouldGenerateStructures(): Boolean = true
}
