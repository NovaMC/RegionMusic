package xyz.novaserver.regionmusic;

import org.bukkit.SoundCategory;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import xyz.novaserver.regionmusic.task.PlayerMusicTask;
import xyz.novaserver.regionmusic.task.PlayerStopTask;

public class RegionPlayer {
    private final Plugin plugin;
    private final Player player;

    private SoundRegion region;
    private PlayerMusicTask musicTask;
    private PlayerStopTask stopTask;

    public RegionPlayer(Player player, Plugin plugin) {
        this.player = player;
        this.plugin = plugin;
    }

    public void playSound() {
        // Play custom music
        player.playSound(player.getLocation(), region.getSound(), region.getCategory(), region.getVolume(), region.getPitch());
    }

    public void stopSound() {
        player.stopSound(region.getSound(), region.getCategory());
    }

    public void stopGameMusic() {
        plugin.getConfig().getStringList("stop-sounds").forEach(sound -> player.stopSound(sound, SoundCategory.MUSIC));
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
