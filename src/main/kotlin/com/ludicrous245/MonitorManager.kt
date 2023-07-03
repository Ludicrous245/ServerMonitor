package com.ludicrous245

import com.ludicrous245.ServerMonitorPlugin.Companion.plugin
import com.sun.management.OperatingSystemMXBean
import net.kyori.adventure.text.Component.text
import net.kyori.adventure.text.TextComponent
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack
import org.bukkit.scheduler.BukkitRunnable
import org.bukkit.scheduler.BukkitTask
import java.lang.management.ManagementFactory

class MonitorManager(private val player: Player){
    private val screen: Inventory = Bukkit.createInventory(null, 9, "${ChatColor.GRAY}${ChatColor.BOLD}서버상태 자가진단")
    private val runtime: Runtime = Runtime.getRuntime()
    private val mb = 1024*1024

    private lateinit var heartBeat: BukkitTask

    private var openTime = 0

    fun render(){
        reload()
        player.openInventory(screen)
    }

    fun close(){
        heartBeat.cancel()
        println("[ServerMonitor] 실시간 정보 갱신을 종료합니다. (열람 시간: $openTime 초)")
        openTime = 0
    }

    fun reload(){
        val interval = object: BukkitRunnable(){
            override fun run() {
                //memory
                val used = "사용됨: ${(runtime.totalMemory() - runtime.freeMemory()) / mb}"
                val free = "여유: ${runtime.freeMemory() / mb}"
                val total = "합계: ${runtime.totalMemory() / mb}"
                val max = "최대: ${runtime.maxMemory() / mb}"

                val heapLore = ArrayList<TextComponent>()
                heapLore.run {
                    add(text("${ChatColor.WHITE}${used}MB"))
                    add(text("${ChatColor.WHITE}${free}MB"))
                    add(text("${ChatColor.WHITE}${total}MB"))
                    add(text("${ChatColor.WHITE}${max}MB"))
                }

                val heap = createDisplayItem("${ChatColor.DARK_GREEN}메모리(힙)", Material.EMERALD, heapLore)

                //런타임
                val runtimeMX = ManagementFactory.getRuntimeMXBean()
                val time = runtimeMX.uptime / 1000 / 60 / 60

                val uptimeLore = java.util.ArrayList<TextComponent>()
                uptimeLore.add(text("${ChatColor.WHITE}업타임: ${time}시간"))

                val uptime = createDisplayItem("${ChatColor.DARK_GRAY}업타임", Material.CLOCK, uptimeLore)

                //cpu
                val cores = runtime.availableProcessors()

                val osbean = ManagementFactory.getOperatingSystemMXBean() as OperatingSystemMXBean

                val cpuUsage = String.format("%.2f", osbean.systemCpuLoad * 100)

                val cpuLore = java.util.ArrayList<TextComponent>()
                cpuLore.run {
                    add(text("${ChatColor.WHITE}코어 수: ${cores}개"))
                    add(text("${ChatColor.WHITE}사용량: ${cpuUsage}%"))
                }

                val cpu = createDisplayItem("${ChatColor.RED}CPU", Material.COMMAND_BLOCK, cpuLore)

                val barrier = createDisplayItem("${ChatColor.AQUA}서버상태 자가진단", Material.LIGHT_BLUE_STAINED_GLASS_PANE, null)

                screen.run {
                    setItem(0, barrier)
                    setItem(1, barrier)
                    setItem(2, barrier)

                    setItem(3, cpu)
                    setItem(4, heap)
                    setItem(5, uptime)

                    setItem(6, barrier)
                    setItem(7, barrier)
                    setItem(8, barrier)
                }

                openTime++
            }
        }

        try {
            heartBeat = interval.runTaskTimer(plugin, 0, 20)
        } catch (e: Exception) {
            println("[ServerMonitor] 예기치 않은 오류가 발생했습니다!")
            close()
            player.closeInventory()
        }
        println("[ServerMonitor] 실시간 정보 갱신을 시작합니다. (갱신 주기: 1초)")
    }

    private fun createDisplayItem(name: String, item: Material, lore: List<TextComponent>?): ItemStack {
        val itemStack = ItemStack(item)
        val meta = itemStack.itemMeta
        meta.displayName(text(name))

        if (lore != null) {
            meta.lore(lore)
        }

        itemStack.setItemMeta(meta)
        return itemStack
    }
}