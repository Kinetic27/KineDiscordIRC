package io.github.kinetic27.kineticirc

import net.dv8tion.jda.api.events.message.MessageReceivedEvent


data class MessageReceived(val event: MessageReceivedEvent) {
    companion object : Event<MessageReceived>()
    fun emit() = Companion.emit(this)
}