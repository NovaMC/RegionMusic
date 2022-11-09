package xyz.novaserver.regionmusic.cmd

import com.google.common.collect.ImmutableList
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.command.TabExecutor
import xyz.novaserver.regionmusic.RegionMusic
import java.util.stream.Collectors
import java.util.stream.Stream

class SoundRegionCmd(private val plugin: RegionMusic) : TabExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<String>): Boolean {
        if (!hasPermission(sender)) {
            sender.sendMessage(Component.text("You don't have permission to use this command!").color(NamedTextColor.RED))
            return true
        }
        if (args.size == 1 && args[0].equals("reload", ignoreCase = true)) {
            plugin.reloadPlugin()
            sender.sendMessage(Component.text("Successfully reloaded the plugin!").color(NamedTextColor.GREEN))
        }
        return true
    }

    override fun onTabComplete(sender: CommandSender, command: Command, alias: String, args: Array<String>): List<String>? {
        val possibilities = Stream.of("reload")
        if (hasPermission(sender)) {
            if (args.isEmpty()) {
                return possibilities.collect(Collectors.toList())
            } else if (args.size == 1) {
                return possibilities
                    .filter { name: String -> name.regionMatches(0, args[0], 0, args[0].length, ignoreCase = true) }
                    .collect(Collectors.toList())
            }
        }
        return ImmutableList.of()
    }

    private fun hasPermission(sender: CommandSender): Boolean {
        return sender.hasPermission("rgmusic.admin")
    }
}