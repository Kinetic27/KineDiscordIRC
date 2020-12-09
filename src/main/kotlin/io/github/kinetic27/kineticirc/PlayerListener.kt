package io.github.kinetic27.kineticirc

import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.event.player.*
import org.bukkit.event.server.BroadcastMessageEvent

class PlayerListener : Listener {
    @EventHandler(priority = EventPriority.NORMAL)
    fun onPlayerJoin(event: PlayerJoinEvent) = PlayerJoin(event).emit()

    @EventHandler(priority = EventPriority.NORMAL)
    fun onPlayerInteract(event: PlayerInteractEvent) = PlayerInteract(event).emit()

    @EventHandler(priority = EventPriority.NORMAL)
    fun onPlayerChat(event: AsyncPlayerChatEvent) = PlayerChat(event).emit()

    @EventHandler(priority = EventPriority.NORMAL)
    fun onPlayerQuit(event: PlayerQuitEvent) = PlayerQuit(event).emit()

    @EventHandler(priority = EventPriority.NORMAL)
    fun onPlayerAdvancementDone(event: PlayerAdvancementDoneEvent) = PlayerAdvancementDone(event).emit()

    @EventHandler(priority = EventPriority.NORMAL)
    fun onPlayerDeath(event: PlayerDeathEvent) = PlayerDeath(event).emit()
}