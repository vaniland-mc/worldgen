package land.vani.plugin.worldgen

import com.mojang.serialization.Lifecycle
import net.minecraft.core.MappedRegistry
import net.minecraft.core.Registry
import net.minecraft.data.BuiltinRegistries
import net.minecraft.resources.ResourceKey
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.level.biome.MultiNoiseBiomeSource
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings
import net.minecraft.world.level.levelgen.RandomState
import org.bukkit.Bukkit
import org.bukkit.block.Biome
import org.bukkit.craftbukkit.v1_19_R1.CraftServer
import org.bukkit.generator.BiomeProvider
import org.bukkit.generator.WorldInfo
import net.minecraft.world.level.biome.Biome as NmsBiome

class ListBiomeProvider(
    private val allowedBiomes: List<Biome>,
) : BiomeProvider() {
    private val biomeRegistry: Registry<NmsBiome> = MappedRegistry<NmsBiome>(
        ResourceKey.createRegistryKey(
            ResourceLocation("vaniland", "biomes")
        ),
        Lifecycle.stable(),
        null
    ).apply {
        allowedBiomes.forEach { biome ->
            val resourceKey = ResourceKey.create(
                Registry.BIOME_REGISTRY,
                ResourceLocation(biome.name.lowercase())
            )
            register(resourceKey, DEFAULT_BIOME_REGISTRY.getOrThrow(resourceKey), Lifecycle.stable())
        }
    }
    private val biomeSource = MultiNoiseBiomeSource.Preset.OVERWORLD.biomeSource(biomeRegistry)
    private val largeBiomeNoiseGeneratorSettings = BuiltinRegistries.NOISE_GENERATOR_SETTINGS
        .getHolderOrThrow(NoiseGeneratorSettings.LARGE_BIOMES)

    override fun getBiome(worldInfo: WorldInfo, x: Int, y: Int, z: Int): Biome {
        val randomState = RandomState.create(
            largeBiomeNoiseGeneratorSettings.value(),
            DEDICATED_SERVER.registryAccess().registryOrThrow(Registry.NOISE_REGISTRY),
            worldInfo.seed,
        )

        val nmsBiome = biomeSource.getNoiseBiome(x, y, z, randomState.sampler()).value()
        return Biome.valueOf(biomeRegistry.getKey(nmsBiome)!!.path.uppercase())
    }

    override fun getBiomes(worldInfo: WorldInfo): List<Biome> = allowedBiomes

    companion object {
        private val DEDICATED_SERVER = (Bukkit.getServer() as CraftServer).server
        private val DEFAULT_BIOME_REGISTRY = DEDICATED_SERVER.registryAccess().registryOrThrow(Registry.BIOME_REGISTRY)
    }
}
