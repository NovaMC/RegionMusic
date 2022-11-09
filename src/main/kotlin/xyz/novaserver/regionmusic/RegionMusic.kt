package xyz.novaserver.regionmusic

import com.sk89q.worldguard.WorldGuard
import com.sk89q.worldguard.protection.flags.StringFlag
import com.sk89q.worldguard.protection.flags.registry.FlagConflictException
import org.bukkit.SoundCategory
import org.bukkit.plugin.java.JavaPlugin
import xyz.novaserver.regionmusic.cmd.SoundRegionCmd
import xyz.novaserver.regionmusic.data.RegionPlayer
import xyz.novaserver.regionmusic.data.SoundEvent
import xyz.novaserver.regionmusic.listener.RegionListener
import java.util.UUID

class RegionMusic : JavaPlugin() {

    // Store sound events and region players
    val soundEventMap = HashMap<String, SoundEvent>()
    val regionPlayerMap = HashMap<UUID, RegionPlayer>()

    // Static variables
    companion object {
        lateinit var SOUND_EVENT_FLAG: StringFlag
    }

    // Register WorldGuard flags
    override fun onLoad() {
        val registry = WorldGuard.getInstance().flagRegistry
        try {
            // Create and register the sound event flag
            val soundFlag = StringFlag("sound-event")
            registry.register(soundFlag)
            SOUND_EVENT_FLAG = soundFlag
        } catch (ex: FlagConflictException) {
            slF4JLogger.error("Failed to register sound-event flag! Check for any conflicting plugins!", ex)
        }
    }

    override fun onEnable() {
        saveDefaultConfig()
        reloadPlugin()

        server.getPluginCommand("rgmusic")?.setExecutor(SoundRegionCmd(this))
        server.pluginManager.registerEvents(RegionListener(this), this)
    }

    override fun onDisable() {
        regionPlayerMap.values.forEach { it.cancelTasks() }
    }

    fun reloadPlugin() {
        val base = "events."
        val eventSet = HashSet<SoundEvent>()

        reloadConfig()

        for (key in config.getConfigurationSection(base)!!.getKeys(false)) {
            val sound = config.getString("$base$key.sound", "unknown").orEmpty()
            val category = config.getString("$base$key.category", "RECORDS").orEmpty()
                .let { SoundCategory.valueOf(it.uppercase()) }
            val loop = config.getBoolean("$base$key.loop", false)
            val length = config.getLong("$base$key.length", 1200L)
            val volume = config.getDouble("$base$key.volume", 1.0).toFloat()
            val pitch = config.getDouble("$base$key.pitch", 1.0).toFloat()

            eventSet.add(SoundEvent(key, sound, category, loop, length, volume, pitch))
        }

        soundEventMap.clear()
        eventSet.forEach {
            soundEventMap[it.name] = it
        }
    }
}