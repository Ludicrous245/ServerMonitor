package com.ludicrous245

import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin

class ServerMonitorPlugin: JavaPlugin() {
    companion object{
        lateinit var plugin: JavaPlugin

        private val playerMonitorTask: MutableMap<Player, MonitorManager> = mutableMapOf()

        fun findTask(player: Player){
            if(!playerMonitorTask.containsKey(player)){
                playerMonitorTask[player] = MonitorManager(player)
            }

            playerMonitorTask[player]!!
        }
    }

    override fun onEnable() {
        plugin = this
    }

    
}