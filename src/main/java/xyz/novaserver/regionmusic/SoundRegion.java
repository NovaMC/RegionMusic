package xyz.novaserver.regionmusic;

import org.bukkit.SoundCategory;

public class SoundRegion {
    // Name of the sound event
    private final String name;

    // Sound variables
    private final String sound;
    private final SoundCategory category;
    private final boolean loop;
    private final long length;
    private final float volume;
    private final float pitch;

    public SoundRegion(String name, String sound, SoundCategory category,
                       boolean loop, long length, float volume, float pitch) {
        this.name = name;
        this.sound = sound;
        this.category = category;
        this.loop = loop;
        this.length = length;
        this.volume = volume;
        this.pitch = pitch;
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

    public float getPitch() {
        return pitch;
    }
}
