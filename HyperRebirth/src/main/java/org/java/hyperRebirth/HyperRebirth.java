package org.java.hyperRebirth;

import org.bukkit.*;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public final class HyperRebirth extends JavaPlugin implements Listener {
    private int Rebirth = 1;

    @Override
    public void onEnable() {
        // 이벤트 리스너 등록
        getServer().getPluginManager().registerEvents(this, this);
    }

    @EventHandler
    public void WitchDeath(EntityDeathEvent event) {
        Entity entity = event.getEntity();
        Player killer = event.getEntity().getKiller(); // 죽인 플레이어 가져오기

        if (entity.getType() == EntityType.WITCH && killer != null) { // 마녀가 죽었을 때만 실행
            Rebirth += 1;
            killer.sendMessage(ChatColor.DARK_RED + "부활 횟수 1회 증가!" + ChatColor.GREEN + " 현재 부활 횟수:" + Rebirth);
        }
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();


        // 부활 횟수가 1 이상일 경우 죽음을 취소하고 즉시 리스폰
        if (Rebirth >= 1) {
            event.setCancelled(true); // 죽음 이벤트 취소 (실제로 죽지 않음)
            Rebirth -= 1; // 부활 횟수 차감

            Bukkit.getScheduler().runTaskLater(this, () -> {
                Location respawnLocation = player.getBedSpawnLocation();
                if (respawnLocation == null) {
                    respawnLocation = player.getWorld().getSpawnLocation();
                }
                player.teleport(respawnLocation);
                // ✅ 2. 불사의 토템 파티클 효과
                Location loc = player.getLocation(); // loc를 반복문 밖에서 선언

                // ✅ 파티클을 여러 번 띄우기
                for (int i = 0; i < 30; i++) {  // 5번 반복
                    Bukkit.getScheduler().runTaskLater(this, () -> {
                        player.getWorld().spawnParticle(Particle.TOTEM_OF_UNDYING, loc, 300, 0.5, 1, 0.5);
                    }, 1L * i); // 20L = 1초, 반복적으로 띄우는 효과
                }

                // ✅ 3. 토템 사용 사운드
                player.playSound(player.getLocation(), Sound.ITEM_TOTEM_USE, 1.0f, 1.0f);

                // ✅ 5. 포션 효과 부여 (불사의 힘)
                player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 200, 1)); // 재생 (10초)
                player.addPotionEffect(new PotionEffect(PotionEffectType.ABSORPTION, 2400, 1)); // 흡수 (2분)
                player.addPotionEffect(new PotionEffect(PotionEffectType.RESISTANCE, 100, 1)); // 저항 (5초)

                // ✅ 7. 부활 메시지 출력
                player.sendMessage(ChatColor.GOLD + "부활했습니다! 남은 부활횟수: " + Rebirth);

            }, 1L); // 1 tick 후 실행
        }
    }
}
