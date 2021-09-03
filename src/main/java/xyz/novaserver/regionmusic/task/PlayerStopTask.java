package xyz.novaserver.regionmusic.task;

import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import xyz.novaserver.regionmusic.RegionPlayer;

public class PlayerStopTask extends BukkitRunnable {
    private final Plugin plugin;
    private final RegionPlayer player;

    public PlayerStopTask(Plugin plugin, RegionPlayer player) {
        this.plugin = plugin;
        this.player = player;
    }

    public void schedule() {
        this.runTaskTimerAsynchronously(plugin, 1L, plugin.getConfig().getInt("stop-period", 60));
    }

    @Override
    public void run() {
        player.stopGameMusic();
    }
}
