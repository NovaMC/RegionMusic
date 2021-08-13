package xyz.novaserver.regionmusic.task;

import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import xyz.novaserver.regionmusic.RegionPlayer;

public class PlayerMusicTask extends BukkitRunnable {
    private final Plugin plugin;
    private final RegionPlayer player;

    public PlayerMusicTask(Plugin plugin, RegionPlayer player) {
        this.plugin = plugin;
        this.player = player;
    }

    public void schedule() {
        this.runTaskTimerAsynchronously(plugin, 1L, player.getRegion().getLength());
    }

    @Override
    public void run() {
        player.getRegion().playSound(player.getPlayer());
    }
}
