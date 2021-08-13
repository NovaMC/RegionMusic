package xyz.novaserver.regionmusic.task;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import org.bukkit.scheduler.BukkitRunnable;
import xyz.novaserver.regionmusic.RegionMusic;
import xyz.novaserver.regionmusic.RegionPlayer;
import xyz.novaserver.regionmusic.SoundRegion;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class RegionCheckTask extends BukkitRunnable {
    private final RegionMusic plugin;

    private final Map<String, SoundRegion> soundRegionMap = new HashMap<>();
    private final Map<UUID, RegionPlayer> regionPlayerMap = new HashMap<>();

    public RegionCheckTask(RegionMusic plugin) {
        this.plugin = plugin;
    }

    public void loadValues(Set<SoundRegion> regionSet) {
        soundRegionMap.clear();
        for (SoundRegion region : regionSet) {
            soundRegionMap.put(region.getName(), region);
        }
    }

    public void schedule() {
        this.runTaskTimerAsynchronously(plugin, 20L,
                Math.round(plugin.getConfig().getDouble("checker-period", 1.0D) * 20.0));
    }

    @Override
    public void run() {
        RegionContainer container = plugin.getWorldGuard().getPlatform().getRegionContainer();

        plugin.getServer().getOnlinePlayers().forEach(player -> {
            // Get the sound-event flag at the player's location
            LocalPlayer localPlayer = WorldGuardPlugin.inst().wrapPlayer(player);
            ApplicableRegionSet regionSet = container.createQuery().getApplicableRegions(BukkitAdapter.adapt(player.getLocation()));
            String soundEvent = regionSet.queryValue(localPlayer, RegionMusic.SOUND_EVENT_FLAG);

            final UUID uuid = player.getUniqueId();
            final RegionPlayer rPlayer;

            // Create or get a RegionPlayer for the player
            if (!regionPlayerMap.containsKey(uuid)) {
                rPlayer = new RegionPlayer(player);
                regionPlayerMap.put(uuid, rPlayer);
            } else {
                rPlayer = regionPlayerMap.get(uuid);
            }

            // Check if the sound-event flag is set
            if (soundEvent != null && !soundEvent.equalsIgnoreCase("unknown")
                    && soundRegionMap.containsKey(soundEvent)) {

                final SoundRegion soundRegion = soundRegionMap.get(soundEvent);

                // Check if the player's region has changed
                if (rPlayer.getRegion() == null || !rPlayer.getRegion().getName().equals(soundRegion.getName())) {
                    // Stop the current region music
                    if (rPlayer.getRegion() != null) {
                        rPlayer.getRegion().stopSound(rPlayer.getPlayer());
                    }
                    // Cancel the player's music task if it exists
                    if (rPlayer.getMusicTask() != null && !rPlayer.getMusicTask().isCancelled()) {
                        rPlayer.getMusicTask().cancel();
                    }
                    // Cancel the player's stop task if it exists
                    if (rPlayer.getStopTask() != null && !rPlayer.getStopTask().isCancelled()) {
                        rPlayer.getStopTask().cancel();
                    }

                    // Set player's region to the new one
                    rPlayer.setRegion(soundRegion);
                    // Play sounds once if not looping otherwise create music task
                    if (!soundRegion.isLoop()) {
                        soundRegion.playSound(rPlayer.getPlayer());
                    } else {
                        rPlayer.createMusicTask(plugin);
                    }
                    // Stop task to stop mc music from playing
                    if (plugin.getConfig().getBoolean("stop-music", true)) {
                        rPlayer.createStopTask(plugin);
                    }
                }
            }
            // If sound-event isn't set then stop sound and cancel tasks
            else if (regionPlayerMap.containsKey(uuid)) {
                if (rPlayer.getRegion() != null) {
                    rPlayer.getRegion().stopSound(rPlayer.getPlayer());
                } if (rPlayer.getMusicTask() != null && !rPlayer.getMusicTask().isCancelled()) {
                    rPlayer.getMusicTask().cancel();
                } if (rPlayer.getStopTask() != null && !rPlayer.getStopTask().isCancelled()) {
                    rPlayer.getStopTask().cancel();
                }
                rPlayer.setRegion(null);
            }
        });
    }

    @Override
    public synchronized void cancel() throws IllegalStateException {
        plugin.getServer().getOnlinePlayers().forEach(player -> {
            soundRegionMap.values().forEach(soundRegion -> soundRegion.stopSound(player));
        });
        regionPlayerMap.values().forEach(rPlayer -> {
            if (rPlayer.getMusicTask() != null) {
                rPlayer.getMusicTask().cancel();
            } if (rPlayer.getStopTask() != null) {
                rPlayer.getStopTask().cancel();
            }
        });
        regionPlayerMap.clear();
        super.cancel();
    }

    public Map<UUID, RegionPlayer> getPlayerMap() {
        return regionPlayerMap;
    }
}
