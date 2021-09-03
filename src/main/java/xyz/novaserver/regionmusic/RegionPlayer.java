package xyz.novaserver.regionmusic;

import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import xyz.novaserver.regionmusic.task.PlayerMusicTask;
import xyz.novaserver.regionmusic.task.PlayerStopTask;

import java.util.Arrays;

public class RegionPlayer {
    private final Player player;

    private SoundRegion region;
    private PlayerMusicTask musicTask;
    private PlayerStopTask stopTask;

    public RegionPlayer(Player player) {
        this.player = player;
    }

    public void playSound() {
        // Play custom music
        player.playSound(player.getLocation(), region.getSound(), region.getCategory(), region.getVolume(), region.getPitch());
    }

    public void stopSound() {
        player.stopSound(region.getSound(), region.getCategory());
    }

    public void stopGameMusic() {
        player.stopSound(Sound.MUSIC_GAME, SoundCategory.MUSIC);
        player.stopSound(Sound.MUSIC_CREATIVE, SoundCategory.MUSIC);
        player.stopSound(Sound.MUSIC_UNDER_WATER, SoundCategory.MUSIC);
        player.stopSound(Sound.MUSIC_END, SoundCategory.MUSIC);
        Arrays.stream(Sound.values())
                .filter(s -> s.name().startsWith("MUSIC_NETHER"))
                .forEach((s -> player.stopSound(s, SoundCategory.MUSIC)));
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
