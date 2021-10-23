package land.vani.plugin.worldgen

import net.minecraft.core.IRegistry
import net.minecraft.world.level.newbiome.layer.GenLayer
import net.minecraft.world.level.newbiome.layer.GenLayers
import org.bukkit.Bukkit
import org.bukkit.block.Biome
import org.bukkit.craftbukkit.v1_17_R1.CraftServer
import org.bukkit.generator.BiomeProvider
import org.bukkit.generator.WorldInfo

class ListBiomeProvider(
    seed: Long,
    private val overwriteBiomes: Map<Biome, Biome>,
    isLargeBiome: Boolean
): BiomeProvider() {
    private val layer: GenLayer = GenLayers.a(
        seed,
        false, // legacy_biome_init_layer
        if(isLargeBiome) 6 else 4, // biomeSize
        4
    )

    override fun getBiome(worldInfo: WorldInfo, x: Int, y: Int, z: Int): Biome {
        val nmsBiome = layer.a(registry, x, z)
        val biome = Biome.valueOf(registry.getKey(nmsBiome)!!.key.uppercase())

        return when(val overwritten = overwriteBiomes[biome]) {
            null -> biome
            else -> overwritten
        }
    }

    override fun getBiomes(worldInfo: WorldInfo): List<Biome> =
        Biome.values()
            .filter { it != Biome.CUSTOM }
            .filter { it !in overwriteBiomes }

    companion object {
        private val console = (Bukkit.getServer() as CraftServer).server
        private val registry = console.customRegistry.b(IRegistry.aO)
    }
}
