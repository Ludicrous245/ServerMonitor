package com.ludicrous245;

import com.sun.istack.internal.NotNull;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public final class ServerMonitor extends JavaPlugin implements Listener {

    public HashMap<Player, MonitorManager> playerMonitor = new HashMap<>();

    @Override
    public void onEnable() {

        Bukkit.getPluginManager().registerEvents(this, this);

        for(Player all : Bukkit.getOnlinePlayers()){
            initPlayer(all);
        }

        System.out.println("[ServerMonitor] 로드되었습니다.");

    }

    @Override
    public void onDisable() {
        System.out.println("[ServerMonitor] 종료되었습니다.");
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if(sender instanceof Player){
            Player player = (Player) sender;
            if (label.equals("sm") || label.equals("servermonitor")) {
                if (player.isOp()) {
                    MonitorManager manager = playerMonitor.get(player);

                    manager.displayPlayer();
                    System.out.println("[ServerMonitor] " + player.getName() + "님께서 서버 상태를 확인중입니다.");

                } else {
                    if(player.hasPermission("ludicrous245.sm")){
                        MonitorManager manager = playerMonitor.get(player);

                        manager.displayPlayer();
                        System.out.println("[ServerMonitor] " + player.getName() + "님께서 서버 상태를 확인중입니다.");

                    }else{
                        player.sendMessage(ChatColor.RED + "권한이 부족합니다.");
                    }
                }
            }
        }
        return false;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event){
        initPlayer(event.getPlayer());
    }

    @EventHandler
    public void onClose(InventoryCloseEvent event){
        if(event.getPlayer() instanceof Player) {
            Player player = (Player) event.getPlayer();
            MonitorManager manager = playerMonitor.get(player);

            if (event.getInventory().equals(manager.inventory)){

                manager.breakPlayer();

            }
        }
    }

    @EventHandler
    public void onClick(InventoryClickEvent event){
        if(event.getWhoClicked() instanceof Player) {
            Player player = (Player) event.getWhoClicked();
            Inventory inventory = Objects.requireNonNull(event.getClickedInventory());

            if(inventory.equals(playerMonitor.get(player).inventory)){

                event.setCancelled(true);

            }
        }
    }

    private void initPlayer(Player player){
         if(!playerMonitor.containsKey(player)){
             playerMonitor.put(player, new MonitorManager(this, player));
         }
    }

}
