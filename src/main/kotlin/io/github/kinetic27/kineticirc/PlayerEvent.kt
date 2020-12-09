package io.github.kinetic27.kineticirc

import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.event.player.*
import org.bukkit.event.server.BroadcastMessageEvent

data class PlayerJoin(val event: PlayerJoinEvent) {
    companion object : Event<PlayerJoin>()
    fun emit() = Companion.emit(this)
}

data class PlayerChat(val event: AsyncPlayerChatEvent) {
    companion object : Event<PlayerChat>()
    fun emit() = Companion.emit(this)
}

data class PlayerInteract(val event: PlayerInteractEvent) {
    companion object : Event<PlayerInteract>()
    fun emit() = Companion.emit(this)
}

data class PlayerQuit(val event: PlayerQuitEvent) {
    companion object : Event<PlayerQuit>()
    fun emit() = Companion.emit(this)
}

data class PlayerAdvancementDone(val event: PlayerAdvancementDoneEvent) {
    companion object : Event<PlayerAdvancementDone>()
    fun emit() = Companion.emit(this)
}

data class PlayerDeath(val event: PlayerDeathEvent) {
    companion object : Event<PlayerDeath>()
    fun emit() = Companion.emit(this)
}