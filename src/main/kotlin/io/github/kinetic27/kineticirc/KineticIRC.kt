package io.github.kinetic27.kineticirc

import club.minnced.discord.webhook.WebhookClient
import club.minnced.discord.webhook.send.WebhookMessageBuilder
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.JDABuilder
import net.dv8tion.jda.api.entities.Activity
import net.dv8tion.jda.api.utils.Compression
import net.dv8tion.jda.api.utils.cache.CacheFlag
import org.bukkit.Bukkit
import org.bukkit.ChatColor.GREEN
import org.bukkit.ChatColor.RED
import org.bukkit.ChatColor.WHITE
import org.bukkit.ChatColor.YELLOW
import org.bukkit.plugin.java.JavaPlugin
import java.util.*


class KineticIRC : JavaPlugin() {
    private var client: WebhookClient? = null
    private var jda: JDA? = null

    private var token = ""
    private var channelID = ""
    private var botPrefix = "/"

    private var url = ""
    private var show3d = false

    private var prefix = ""
    private var suffix = ""
    private var joiner = ""

    init {
        reload()

        // 채팅 입력 이벤트
        PlayerChat on {
            with(it.event) {
                // logger.info("$GREEN[${player.name}] ${WHITE}${message.}")
                sendToDiscord(player.name, player.uniqueId.getAvatarHeadUrl(), message)
            }
        }

        // 접속 이벤트
        PlayerJoin on {
            client!!.send("${it.event.player.name} joined the game")
        }

        // 퇴장 이벤트
        PlayerQuit on {
            client!!.send("${it.event.player.name} left the game")
        }

        // 도전과제 완료 이벤트
        PlayerAdvancementDone on {
            client!!.send("${it.event.player.name}이(가) [${it.event.eventName}] 발전 과제를 달성했습니다 :tada:")
        }

        // 유저 사망 이벤트
        PlayerDeath on {
            client!!.send("${it.event.deathMessage} :coffin:")
        }

        // 디코 메시지 전달 이벤트
        MessageReceived on {
            with(it.event) {
                if (message.author.isBot)
                    return@with

                when (message.contentRaw) {
                    "${botPrefix}help" -> {
                        logger.info("${WHITE}help cmd called")
                        val cmds = (
                                "${botPrefix}help: 명령어 목록을 봅니다.\n" +
                                "${botPrefix}list: 접속중인 유저들을 봅니다."
                                )
                        channel.sendMessage("**[ 명령어 리스트 ]**\n$cmds").queue()
                    }

                    "${botPrefix}list" -> {
                        var users = Bukkit.getOnlinePlayers().joinToString(separator = "\n") { user -> "- ${user.name}" }

                        if (users.isBlank()) users = "- 접속중인 유저가 없습니다."

                        channel.sendMessage("**[ 접속중인 유저 리스트 ]**\n$users").queue()
                    }

                    else -> if (channel.id == channelID) {
                        logger.info("$WHITE${it.event.author.name}: ${it.event.message.contentDisplay}")

                        sendToDiscord(author.name, author.avatarUrl ?: "", message.contentRaw)
                        Bukkit.broadcastMessage("$prefix${author.name}$suffix$joiner${message.contentRaw}")
                        // 마크는 "<이름> 메시지 이렇게 뜸
                        // channel.sendMessage("Pong!")
                        message.delete().complete()
                    }
                }
            }
        }
    }

    @Suppress("SameParameterValue")
    private fun getConfigSafe(configPath: String, msg: () -> Unit) =
            with(config.getString(configPath).toString()) {
                when {
                    isNotEmpty() -> this
                    else -> {
                        msg()
                        null
                    }
                }
            }

    private fun reload() {
        saveDefaultConfig()

        // bot-setting
        token = getConfigSafe("bot-setting.bot-token") {
            logger.info("$RED[❌] ${WHITE}config 파일에 토큰을 입력해주세요")
            server.shutdown()
        } ?: ""
        channelID = getConfigSafe("bot-setting.channel-id") {
            logger.info("$RED[❌] ${WHITE}config 파일에 채널 아이디를 입력해주세요")
            server.shutdown()
        } ?: ""
        botPrefix = config.getString("bot-setting.bot-prefix").toString()

        // webhook-setting
        url = getConfigSafe("webhook-setting.webhook-link") {
            logger.info("$RED[❌] ${WHITE}config 파일에 웹훅 주소를 입력해주세요")
            server.shutdown()
        } ?: ""
        show3d = config.getString("webhook-setting.show-3d-head")?.toBoolean() ?: false

        // discord-chat
        prefix = config.getString("discord-chat.prefix").toString()
        suffix = config.getString("discord-chat.suffix").toString()
        joiner = config.getString("discord-chat.joiner").toString()

        if (jda == null) {
            jda = JDABuilder.createDefault(token).apply {
                disableCache(CacheFlag.MEMBER_OVERRIDES, CacheFlag.VOICE_STATE)
                setBulkDeleteSplittingEnabled(false)
                setCompression(Compression.NONE)
                setActivity(Activity.playing("Server Online(${botPrefix}help)"))
                addEventListeners(DiscordListener())
            }.build()

            jda!!.awaitReady()

            logger.info("${YELLOW}JDA 로딩...")
        }

        if (client == null) {
            client = WebhookClient.withUrl(url)
            logger.info("${YELLOW}웹훅 로딩...")
        }

        logger.info("$GREEN[✔] ${WHITE}KineIRC가 로딩되었습니다.")
    }

    override fun onEnable() {
        logger.info("$GREEN[✔] ${WHITE}KineIRC가 실행되었습니다.")

        server.pluginManager.registerEvents(PlayerListener(), this)

    }

    override fun onDisable() {
        logger.info("$GREEN[✔] ${WHITE}KineIRC가 종료되었습니다.")
        // jda?.presence?.setPresence(Activity.playing("Server Offline"), true)
    }


    private fun sendToDiscord(name: String, avatarUrl: String, msg: String) {
        val builder = WebhookMessageBuilder().apply {
            if (avatarUrl.isNotEmpty())
                setAvatarUrl(avatarUrl)

            setUsername(name)
            setContent(msg)
        }

        client!!.send(builder.build())
    }

    private fun UUID.getAvatarHeadUrl() = when (show3d) {
        false -> "http://cravatar.eu/avatar/$this/64.png"
        else -> "http://cravatar.eu/head/$this/64.png"
    }
}