package xyz.novaserver.regionmusic;

import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.flags.StringFlag;
import com.sk89q.worldguard.protection.flags.registry.FlagConflictException;
import com.sk89q.worldguard.protection.flags.registry.FlagRegistry;
import org.bukkit.SoundCategory;
import org.bukkit.plugin.java.JavaPlugin;
import xyz.novaserver.regionmusic.cmd.SoundRegionCmd;
import xyz.novaserver.regionmusic.listener.RegionMusicListener;
import xyz.novaserver.regionmusic.task.RegionCheckTask;

import java.util.HashSet;
import java.util.Set;

public class RegionMusic extends JavaPlugin {
    private WorldGuard worldGuard;
    private RegionCheckTask checkTask;

    public static StringFlag SOUND_EVENT_FLAG;

    @Override
    public void onLoad() {
        this.worldGuard = WorldGuard.getInstance();
        FlagRegistry registry = worldGuard.getFlagRegistry();
        try {
            StringFlag soundFlag = new StringFlag("sound-event");
            registry.register(soundFlag);
            SOUND_EVENT_FLAG = soundFlag;
        }
        catch (FlagConflictException e) {
            getSLF4JLogger().error("Failed to register sound-event flag! Check for any conflicting plugins!", e);
        }
    }

    @Override
    public void onEnable() {
        saveDefaultConfig();
        reloadPlugin();

        getServer().getPluginCommand("rgmusic").setExecutor(new SoundRegionCmd(this));
        getServer().getPluginManager().registerEvents(new RegionMusicListener(this), this);
    }

    @Override
    public void onDisable() {
        checkTask.cancel();
    }

    public void reloadPlugin() {
        final String base = "events.";
        final Set<SoundRegion> regionSet = new HashSet<>();

        reloadConfig();
        if (checkTask != null) {
            checkTask.cancel();
        }

        // Loop through events and create SoundRegions
        for (String key : getConfig().getConfigurationSection(base).getKeys(false)) {
            String sound = getConfig().getString(base + key + ".sound", "unknown");
            SoundCategory category = SoundCategory.valueOf(getConfig().getString(base + key + ".category", "RECORDS").toUpperCase());
            boolean loop = getConfig().getBoolean(base + key + ".loop", false);
            long length = getConfig().getLong(base + key + ".length", 1200L);
            float volume = (float) getConfig().getDouble(base + key + ".volume", 1.0F);
            float pitch = (float) getConfig().getDouble(base + key + ".pitch", 1.0F);

            // Make new SoundRegion out of the gathered values
            regionSet.add(new SoundRegion(key, sound, category, loop, length, volume, pitch));
        }

        checkTask = new RegionCheckTask(this);
        // Load the regions into the checker
        checkTask.loadValues(regionSet);
        checkTask.schedule();
    }

    public RegionCheckTask getCheckTask() {
        return checkTask;
    }

    public WorldGuard getWorldGuard() {
        return worldGuard;
    }
}
