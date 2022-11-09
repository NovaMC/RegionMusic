package xyz.novaserver.regionmusic.data

import org.bukkit.SoundCategory

data class SoundEvent(
    val name: String, // Name of the sound event
    val sound: String,
    val category: SoundCategory, // Sound variables
    val isLoop: Boolean,
    val length: Long,
    val volume: Float,
    val pitch: Float
) {
    companion object {
        val NO_EVENT = SoundEvent("unknown", "", SoundCategory.MASTER, false, 0, 0.0f, 0.0f)
    }
}