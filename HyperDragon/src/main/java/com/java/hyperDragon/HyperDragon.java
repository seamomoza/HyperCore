package com.java.hyperDragon;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.World;
import org.bukkit.entity.EnderDragon;
import org.bukkit.attribute.Attribute;
import org.bukkit.Particle;
import org.bukkit.util.ChatPaginator;

public class HyperDragon extends JavaPlugin implements Listener {
    private int Rebirth = 1;
    private double dragonHealthMultiplier = 200.0;

    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(this, this);
    }

    @EventHandler
    public void onPlayerPortal(PlayerPortalEvent event) {
        Player player = event.getPlayer();
        World to = event.getTo().getWorld();
        if (to != null && to.getName().equalsIgnoreCase("world_the_end")) {
            setDragonHealth(player);
        }
    }

    @EventHandler
    public void onPlayerDeath(EntityDeathEvent event) {
        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();
            dragonHealthMultiplier *= 1.2;
            Bukkit.getLogger().info("Dragon health multiplier increased: " + dragonHealthMultiplier);
        }
    }

    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        Player player = event.getPlayer();
        player.playSound(player.getLocation(), Sound.ENTITY_WITHER_SPAWN, 1.0f, 1.0f);
        player.sendMessage(ChatColor.DARK_RED + "드래곤 체력 1.2배!: " + (int) dragonHealthMultiplier);
    }

    @EventHandler
    public void WitchDeath(EntityDeathEvent event) {
        Entity entity = event.getEntity();
        Player killer = event.getEntity().getKiller();
        if (entity.getType() == EntityType.WITCH && killer != null) {
            Rebirth += 1;
            killer.sendMessage(ChatColor.DARK_RED + "부활 횟수 1회 증가!" + ChatColor.GREEN + " 현재 부활 횟수:" + Rebirth);
        }
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        if (Rebirth >= 1) {
            event.setCancelled(true);
            Rebirth -= 1;

            Bukkit.getScheduler().runTaskLater(this, () -> {
                Location respawnLocation = player.getBedSpawnLocation();
                if (respawnLocation == null) {
                    respawnLocation = player.getWorld().getSpawnLocation();
                }
                player.teleport(respawnLocation);

                Location loc = player.getLocation();
                for (int i = 0; i < 30; i++) {
                    Bukkit.getScheduler().runTaskLater(this, () -> {
                        player.getWorld().spawnParticle(Particle.TOTEM_OF_UNDYING, loc, 300, 0.5, 1, 0.5);
                    }, 1L * i);
                }

                player.playSound(player.getLocation(), Sound.ITEM_TOTEM_USE, 1.0f, 1.0f);
                player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 200, 1));
                player.addPotionEffect(new PotionEffect(PotionEffectType.ABSORPTION, 2400, 1));
                player.addPotionEffect(new PotionEffect(PotionEffectType.RESISTANCE, 100, 1));
                player.sendMessage(ChatColor.GOLD + "부활했습니다! 남은 부활횟수: " + Rebirth);
                player.sendMessage(ChatColor.DARK_RED + "드래곤 체력 1.2배!: " + (int) dragonHealthMultiplier);
            }, 1L);
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        // 플레이어가 서버에 접속할 때 Tab 목록에 부활 횟수를 표시
        Player player = event.getPlayer();
        updateTabList(player);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        // 플레이어가 서버에서 나갈 때 Tab 목록에서 부활 횟수를 제거
        Player player = event.getPlayer();
        updateTabList(player);
    }

    // 부활 횟수를 Tab 목록에 표시하기 위한 메서드
    public void updateTabList(Player player) {
        String playerName = player.getName();
        String footer = "남은 부활 횟수: " + ChatColor.GREEN +  Rebirth;
        player.setPlayerListFooter(footer);  // 플레이어 이름 아래에 부활 횟수 표시
    }

    private void setDragonHealth(final Player player) {
        new BukkitRunnable() {
            @Override
            public void run() {
                World endWorld = Bukkit.getWorld("world_the_end");
                if (endWorld == null) return;

                final EnderDragon dragon = endWorld.getEntitiesByClass(EnderDragon.class).stream().findFirst().orElse(null);

                    dragon.getAttribute(Attribute.MAX_HEALTH).setBaseValue(dragonHealthMultiplier);
                    dragon.setHealth(dragonHealthMultiplier);

                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            double currentHealth = dragon.getHealth();
                            player.sendActionBar(ChatColor.YELLOW + "드래곤 체력: " + ChatColor.DARK_PURPLE + (int) currentHealth);
                        }
                    }.runTaskTimer(HyperDragon.this, 0L, 1L);
                    Bukkit.getLogger().info("Ender Dragon's health set to: " + dragonHealthMultiplier);
                }

        }.runTask(this);
    }
}