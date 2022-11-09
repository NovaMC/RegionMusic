package xyz.novaserver.regionmusic.listener

import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import xyz.novaserver.core.paper.event.RegionEnterEvent
import xyz.novaserver.core.paper.event.RegionExitEvent
import xyz.novaserver.core.paper.event.RegionInitializeEvent
import xyz.novaserver.regionmusic.RegionMusic
import xyz.novaserver.regionmusic.data.RegionPlayer
import xyz.novaserver.regionmusic.data.SoundEvent
import java.util.function.Consumer

class RegionListener(private val rgmusic: RegionMusic) : Listener {

    @EventHandler(priority = EventPriority.LOWEST)
    fun onPlayerJoin(event: PlayerJoinEvent) {
        // Create region player
        rgmusic.regionPlayerMap[event.player.uniqueId] = RegionPlayer(rgmusic, event.player)
    }

    @EventHandler
    fun onPlayerQuit(event: PlayerQuitEvent) {
        val uuid = event.player.uniqueId

        // Cancel player tasks and remove region player
        rgmusic.regionPlayerMap[uuid]?.cancelTasks()
        rgmusic.regionPlayerMap.remove(uuid)
    }

    @EventHandler
    fun onRegionInitialize(event: RegionInitializeEvent) {
        val regionPlayer = rgmusic.regionPlayerMap[event.player.uniqueId] ?: return
        val soundFlag = event.applicableSet.queryValue(event.player, RegionMusic.SOUND_EVENT_FLAG) ?: "unknown"
        runRegionCheck(soundFlag) {
            runRegionEnter(regionPlayer, it)
        }
    }

    @EventHandler
    fun onRegionEnter(event: RegionEnterEvent) {
        val regionPlayer = rgmusic.regionPlayerMap[event.player.uniqueId] ?: return
        val soundFlag = event.toSet.queryValue(event.player, RegionMusic.SOUND_EVENT_FLAG) ?: "unknown"

        runRegionCheck(soundFlag) {
            runRegionEnter(regionPlayer, it)
        }
    }

    @EventHandler
    fun onRegionExit(event: RegionExitEvent) {
        val regionPlayer = rgmusic.regionPlayerMap[event.player.uniqueId] ?: return
        val soundFlag = event.toSet.queryValue(event.player, RegionMusic.SOUND_EVENT_FLAG) ?: "unknown"

        runRegionCheck(soundFlag) {
            val currentEvent = regionPlayer.event

            if (it != SoundEvent.NO_EVENT) {
                runRegionEnter(regionPlayer, it)
            } else {
                // Stop current music
                if (currentEvent != SoundEvent.NO_EVENT) {
                    regionPlayer.stopSound()
                }
                regionPlayer.cancelTasks() // Cancel tasks
                regionPlayer.event = SoundEvent.NO_EVENT
            }
        }
    }

    private fun runRegionEnter(regionPlayer: RegionPlayer, newEvent: SoundEvent) {
        val currentEvent = regionPlayer.event

        // Return if the event hasn't changed
        if (newEvent == SoundEvent.NO_EVENT || currentEvent.name == newEvent.name) {
            return
        }
        // Stop the current region music
        if (currentEvent != SoundEvent.NO_EVENT) {
            regionPlayer.stopSound()
        }
        regionPlayer.cancelTasks() // Cancel the player's existing tasks
        regionPlayer.event = newEvent // Set player's region to the new one

        // Play sounds once if not looping otherwise create music task
        if (newEvent.isLoop) {
            regionPlayer.createMusicTask()
        } else {
            regionPlayer.playSound()
        }

        // Create stop task to periodically stop MC music from playing
        if (rgmusic.config.getBoolean("stop-music", true)) {
            regionPlayer.createStopTask()
        }
    }

    private fun runRegionCheck(soundFlag: String, consumer: Consumer<SoundEvent>) {
        val soundEvent = if (soundFlag != "unknown" && rgmusic.soundEventMap.containsKey(soundFlag)) {
            rgmusic.soundEventMap[soundFlag]
        } else {
            SoundEvent.NO_EVENT
        }
        soundEvent?.let { consumer.accept(it) }
    }
}