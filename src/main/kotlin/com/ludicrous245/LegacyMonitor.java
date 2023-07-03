package com.ludicrous245;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import java.lang.management.ManagementFactory;
import com.sun.management.OperatingSystemMXBean;
import java.lang.management.RuntimeMXBean;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class LegacyMonitor {

    public Inventory inventory;

    private final Player user;

    private final Runtime runtime;
    private final JavaPlugin instance;
    private BukkitRunnable interval;

    private int openTime = 0;

    int mb = 1024*1024;

    public LegacyMonitor(JavaPlugin plugin, Player player){
        instance = plugin;
        user = player;
        runtime = Runtime.getRuntime();
        inventory = Bukkit.createInventory(null, 9, ChatColor.GRAY + "" + ChatColor.BOLD + "서버상태 자가진단");
    }

    public void displayPlayer(){
        reloadPlayer();
        user.openInventory(inventory);
    }

    public void breakPlayer(){
        interval.cancel();
        System.out.println("[ServerMonitor] 실시간 정보 갱신을 종료합니다. (열람 시간: " + openTime + "초)");
        openTime = 0;
    }

    private void reloadPlayer(){
        interval = new BukkitRunnable() {
            @Override
            public void run() {

                //memory

                String used = "사용됨: " + (runtime.totalMemory() - runtime.freeMemory()) / mb;
                String free = "여유: " + runtime.freeMemory() / mb;
                String total = "합계: " + runtime.totalMemory() / mb;
                String max = "최대: " + runtime.maxMemory() / mb;

                ArrayList<String> heapLore = new ArrayList<>();
                heapLore.add(ChatColor.WHITE + used+"MB");
                heapLore.add(ChatColor.WHITE + free+"MB");
                heapLore.add(ChatColor.WHITE + total+"MB");
                heapLore.add(ChatColor.WHITE + max+"MB");

                ItemStack heap = createDisplayItem(ChatColor.DARK_GREEN+"메모리(힙)", Material.EMERALD, heapLore);

                //런타임
                RuntimeMXBean runtimeMX = ManagementFactory.getRuntimeMXBean();
                long time = (((runtimeMX.getUptime() / 1000) / 60) / 60);

                ArrayList<String> uptimeLore = new ArrayList<>();
                uptimeLore.add(ChatColor.WHITE + "업타임: " + time+"시간");

                ItemStack uptime = createDisplayItem(ChatColor.DARK_GRAY+"업타임", Material.CLOCK, uptimeLore);


                //cpu
                int cores = runtime.availableProcessors();

                OperatingSystemMXBean osbean = (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();

                String cpuUsage = String.format("%.2f", osbean.getSystemCpuLoad() * 100);

                ArrayList<String> cpuLore = new ArrayList<>();
                cpuLore.add(ChatColor.WHITE + "코어 수: " + cores+"개");
                cpuLore.add(ChatColor.WHITE + "사용량: " + cpuUsage+"%");

                ItemStack cpu = createDisplayItem(ChatColor.RED+"CPU", Material.COMMAND_BLOCK, cpuLore);

                ItemStack barrier = createDisplayItem(ChatColor.AQUA+"서버상태 자가진단", Material.LIGHT_BLUE_STAINED_GLASS_PANE, null);

                inventory.setItem(0, barrier);
                inventory.setItem(1, barrier);
                inventory.setItem(2, barrier);

                inventory.setItem(3, cpu);
                inventory.setItem(4, heap);
                inventory.setItem(5, uptime);

                inventory.setItem(6, barrier);
                inventory.setItem(7, barrier);
                inventory.setItem(8, barrier);

                openTime++;
            }
        };

        try {
            interval.runTaskTimer(instance, 0, 20);
        }catch (Exception e){
            System.out.println("[ServerMonitor] 예기치 않은 오류가 발생했습니다!");
            breakPlayer();
            user.closeInventory();
        }
        System.out.println("[ServerMonitor] 실시간 정보 갱신을 시작합니다. (갱신 주기: 1초)");
    }

    public ItemStack createDisplayItem(String name, Material item, List<String> lore){

        ItemStack itemStack = new ItemStack(item);

        ItemMeta meta = itemStack.getItemMeta();

        Objects.requireNonNull(meta).setDisplayName(name);

        if(lore != null) {
            meta.setLore(lore);
        }

        itemStack.setItemMeta(meta);

        return itemStack;
    }
}