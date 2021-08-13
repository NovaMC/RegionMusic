package xyz.novaserver.regionmusic.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import xyz.novaserver.regionmusic.RegionMusic;
import xyz.novaserver.regionmusic.RegionPlayer;

import java.util.UUID;

public class RegionMusicListener implements Listener {
    private final RegionMusic plugin;

    public RegionMusicListener(RegionMusic plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        UUID uuid = event.getPlayer().getUniqueId();

        // Cancel tasks for player and remove them from the map
        if (plugin.getCheckTask().getPlayerMap().containsKey(uuid)) {
            RegionPlayer rPlayer = plugin.getCheckTask().getPlayerMap().get(uuid);
            if (rPlayer.getMusicTask() != null && !rPlayer.getMusicTask().isCancelled()) {
                rPlayer.getMusicTask().cancel();
            } if (rPlayer.getStopTask() != null && !rPlayer.getStopTask().isCancelled()) {
                rPlayer.getStopTask().cancel();
            }
        }
        plugin.getCheckTask().getPlayerMap().remove(uuid);
    }
}
