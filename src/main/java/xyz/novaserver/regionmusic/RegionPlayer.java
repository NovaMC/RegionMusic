package xyz.novaserver.regionmusic;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import xyz.novaserver.regionmusic.task.PlayerMusicTask;
import xyz.novaserver.regionmusic.task.PlayerStopTask;

public class RegionPlayer {
    private final Player player;

    private SoundRegion region;
    private PlayerMusicTask musicTask;
    private PlayerStopTask stopTask;

    public RegionPlayer(Player player) {
        this.player = player;
    }

    public Player getPlayer() {
        return player;
    }

    public SoundRegion getRegion() {
        return region;
    }

    public void setRegion(SoundRegion region) {
        this.region = region;
    }

    public PlayerMusicTask getMusicTask() {
        return musicTask;
    }

    public void createMusicTask(Plugin plugin) {
        musicTask = new PlayerMusicTask(plugin, this);
        musicTask.schedule();
    }

    public PlayerStopTask getStopTask() {
        return stopTask;
    }

    public void createStopTask(Plugin plugin) {
        stopTask = new PlayerStopTask(plugin, this);
        stopTask.schedule();
    }
}
