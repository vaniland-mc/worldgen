package land.vani.plugin.worldgen

import land.vani.mcorouhlin.command.dsl.command
import land.vani.mcorouhlin.paper.McorouhlinKotlinPlugin
import land.vani.mcorouhlin.paper.permission.hasPermission
import land.vani.mcorouhlin.permission.registerPermissions
import org.bukkit.command.CommandSender
import org.bukkit.generator.BiomeProvider

class WorldGenPlugin : McorouhlinKotlinPlugin() {
    private val presetsConfig = PresetsConfig(this)

    override suspend fun onEnableAsync() {
        registerPermissions<Permissions>()
        saveResource("presets.yml", false)
        presetsConfig.reload()
        registerCommands()
    }

    private suspend fun registerCommands() {
        val command = command<CommandSender>("worldgen") {
            required { it.hasPermission(Permissions.COMMAND) }

            literal("reload") {
                runs {
                    presetsConfig.reload()
                    source.sendMessage("configをリロードしました")
                }
            }
        }
        registerCommand(command)
    }

    override fun getDefaultBiomeProvider(worldName: String, id: String?): BiomeProvider =
        ListBiomeProvider(
            presetsConfig.presets[id ?: "default"]
                ?.allowedBiomes
                .orEmpty()
        )
}
