package xyz.novaserver.regionmusic.task;

import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Player;
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
        this.runTaskTimerAsynchronously(plugin, 1L, 5 * 20L);
    }

    @Override
    public void run() {
        Player bukkitPlayer = player.getPlayer();
        bukkitPlayer.stopSound(Sound.MUSIC_GAME, SoundCategory.MUSIC);
        bukkitPlayer.stopSound(Sound.MUSIC_CREATIVE, SoundCategory.MUSIC);
        bukkitPlayer.stopSound(Sound.MUSIC_UNDER_WATER, SoundCategory.MUSIC);
    }
}
