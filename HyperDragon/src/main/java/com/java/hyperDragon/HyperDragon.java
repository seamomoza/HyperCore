package com.java.hyperDragon;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.EnderDragon;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

public class HyperDragon extends JavaPlugin implements Listener {

    // 드래곤 체력을 설정할 변수 (기본값 200)
    private double dragonHealthMultiplier = 200.0;

    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(this, this);
    }

    @EventHandler
    public void onPlayerPortal(PlayerPortalEvent event) {
        Player player = event.getPlayer();
        World to = event.getTo().getWorld();

        // 플레이어가 엔더월드로 포탈을 통해 이동했을 때
        if (to != null && to.getName().equalsIgnoreCase("world_the_end")) {
            setDragonHealth(player);
        }
    }

    @EventHandler
    public void onPlayerDeath(EntityDeathEvent event) {
        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();

            // 드래곤 체력 1.5배 증가
            dragonHealthMultiplier *= 1.5;
            // 메시지 출력
            Bukkit.getLogger().info("Dragon health multiplier increased: " + dragonHealthMultiplier);
        }
    }


    private void setDragonHealth(final Player player) {
        new BukkitRunnable() {
            @Override
            public void run() {
                World endWorld = Bukkit.getWorld("world_the_end");
                if (endWorld == null) return;

                // 엔더월드에 드래곤이 존재하는지 확인
                final EnderDragon dragon = endWorld.getEntitiesByClass(EnderDragon.class).stream().findFirst().orElse(null);

                if (dragon != null) {
                    // 드래곤의 최대 체력을 설정하고, 체력을 최대로 채운다.
                    dragon.getAttribute(Attribute.MAX_HEALTH).setBaseValue(dragonHealthMultiplier);
                    dragon.setHealth(dragonHealthMultiplier);  // 드래곤 체력도 최대값으로 설정

                    // 액션바를 주기적으로 갱신
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            // 현재 드래곤의 체력을 액션바에 표시
                            double currentHealth = dragon.getHealth();
                            player.sendActionBar(ChatColor.YELLOW + "드래곤 체력: " + ChatColor.DARK_PURPLE + (int) currentHealth);
                        }
                    }.runTaskTimer(HyperDragon.this, 0L, 20L); // 1초마다 업데이트 (20틱 간격)

                    Bukkit.getLogger().info("Ender Dragon's health set to: " + dragonHealthMultiplier);
                } else {
                    player.sendMessage("No Ender Dragon found in the End!");
                }
            }
        }.runTask(this); // 엔더월드에 들어갔을 때 즉시 실행
    }
    @EventHandler
    public void onplayerRespawn(PlayerRespawnEvent event){
        Player player = (Player) event.getPlayer();
        player.playSound(player.getLocation(), Sound.ENTITY_WITHER_SPAWN, 1.0f, 1.0f);
        player.sendMessage(ChatColor.DARK_RED + "드래곤 체력 1.5배!: " + (int) dragonHealthMultiplier);
    }
}
