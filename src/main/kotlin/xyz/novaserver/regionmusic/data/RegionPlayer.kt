package xyz.novaserver.regionmusic.data

import org.bukkit.Bukkit
import org.bukkit.SoundCategory
import org.bukkit.entity.Player
import org.bukkit.plugin.Plugin
import org.bukkit.scheduler.BukkitTask

class RegionPlayer(
    private val plugin: Plugin,
    private val player: Player
) {
    private var musicTask: BukkitTask? = null
    private var stopTask: BukkitTask? = null
    var event: SoundEvent = SoundEvent.NO_EVENT

    fun playSound() {
        player.playSound(player.location, event.sound, event.category, event.volume, event.pitch)
    }

    fun stopSound() {
        player.stopSound(event.sound, event.category)
    }

    fun stopGameMusic() {
        plugin.config.getStringList("stop-sounds").forEach { player.stopSound(it, SoundCategory.MUSIC) }
    }

    fun createMusicTask() {
        musicTask = Bukkit.getScheduler().runTaskTimerAsynchronously(plugin,
            Runnable { this.playSound() },1, event.length)
    }

    fun createStopTask() {
        stopTask = plugin.server.scheduler.runTaskTimerAsynchronously(plugin, Runnable { this.stopGameMusic() },
                20, plugin.config.getLong("stop-period", 80))
    }

    fun cancelTasks() {
        if (musicTask != null && !musicTask!!.isCancelled) {
            musicTask?.cancel()
        }
        if (stopTask != null && !stopTask!!.isCancelled) {
            stopTask?.cancel()
        }
    }
}

