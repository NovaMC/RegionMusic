package xyz.novaserver.regionmusic;

import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Player;

public class SoundRegion {
    private final RegionMusic plugin;

    // Name of the sound event
    private final String name;

    // Sound variables
    private final String sound;
    private final SoundCategory category;
    private final boolean loop;
    private final long length;
    private final float volume;

    public SoundRegion(String name, String sound, SoundCategory category,
                       boolean loop, long length, float volume, RegionMusic plugin) {
        this.name = name;
        this.sound = sound;
        this.category = category;
        this.loop = loop;
        this.length = length;
        this.volume = volume;
        this.plugin = plugin;
    }

    public void playSound(Player player) {
        // Stop in-game music so it doesn't conflict with ours
        if (plugin.getConfig().getBoolean("stop-music", true)) {
            player.stopSound(Sound.MUSIC_GAME, SoundCategory.MUSIC);
            player.stopSound(Sound.MUSIC_CREATIVE, SoundCategory.MUSIC);
            player.stopSound(Sound.MUSIC_UNDER_WATER, SoundCategory.MUSIC);
        }

        // Play custom music
        player.playSound(player.getLocation(), sound, category, volume, 1.0F);
    }

    public void stopSound(Player player) {
        player.stopSound(sound, category);
    }

    public String getName() {
        return name;
    }

    public String getSound() {
        return sound;
    }

    public SoundCategory getCategory() {
        return category;
    }

    public boolean isLoop() {
        return loop;
    }

    public long getLength() {
        return length;
    }

    public float getVolume() {
        return volume;
    }
}
