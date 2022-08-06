package land.vani.plugin.worldgen

import land.vani.mcorouhlin.permission.Permission
import land.vani.mcorouhlin.permission.PermissionDefault

enum class Permissions(
    override val node: String,
    override val description: String? = null,
    override val default: PermissionDefault? = PermissionDefault.OP,
    override val children: Map<Permission, Boolean> = mapOf(),
) : Permission {
    COMMAND("worldgen.command", "Allows the user to use the worldgen command"),
}
