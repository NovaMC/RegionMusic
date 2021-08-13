package xyz.novaserver.regionmusic.cmd;

import com.google.common.collect.ImmutableList;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xyz.novaserver.regionmusic.RegionMusic;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SoundRegionCmd implements TabExecutor {
    private final RegionMusic plugin;

    public SoundRegionCmd(RegionMusic plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!hasPermission(sender)) {
            sender.sendMessage(Component.text("You don't have permission to use this command!").color(NamedTextColor.RED));
            return true;
        }

        if (args.length == 1 && args[0].equalsIgnoreCase("reload")) {
            plugin.reloadPlugin();
            sender.sendMessage(Component.text("Successfully reloaded the plugin!").color(NamedTextColor.GREEN));
        }
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        Stream<String> possibilities = Stream.of("reload");

        if (hasPermission(sender)) {
            if (args.length == 0) {
                return possibilities.collect(Collectors.toList());
            } else if (args.length == 1) {
                return possibilities
                        .filter(name -> name.regionMatches(true, 0, args[0], 0, args[0].length()))
                        .collect(Collectors.toList());
            }
        }
        return ImmutableList.of();
    }

    private boolean hasPermission(CommandSender sender) {
        return sender.hasPermission("rgmusic.admin");
    }
}
