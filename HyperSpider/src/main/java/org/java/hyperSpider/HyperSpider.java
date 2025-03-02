package org.java.hyperSpider;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.entity.Spider;
import org.bukkit.entity.FallingBlock;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;

import java.util.Random;

public class HyperSpider extends JavaPlugin implements Listener {

    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(this, this);
    }

    // 스파이더가 생성될 때 버프를 추가
    @EventHandler
    public void onSpiderSpawn(EntitySpawnEvent event) {
        // 생성된 엔티티가 스파이더인지 확인
        if (event.getEntity() instanceof Spider) {
            Spider spider = (Spider) event.getEntity();

            spider.getAttribute(Attribute.KNOCKBACK_RESISTANCE).setBaseValue(1000000000.0);
            // 스파이더에게 무한 속도 2, 힘 1, 저항 1, 레제네이션 1 버프 추가
            spider.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 1)); // 속도 2
            spider.addPotionEffect(new PotionEffect(PotionEffectType.STRENGTH, Integer.MAX_VALUE, 1)); // 힘 1
            spider.addPotionEffect(new PotionEffect(PotionEffectType.RESISTANCE, Integer.MAX_VALUE, 2)); // 저항 1
            spider.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, Integer.MAX_VALUE, 1)); // 레제네이션 1
        }
    }

    // 스파이더가 플레이어나 다른 거미를 공격할 때 거미줄 난사
    @EventHandler
    public void onSpiderAttack(EntityDamageByEntityEvent event) {
        // 공격자가 HyperSpider인지 확인
        if (event.getDamager() instanceof Spider) {
            Spider spider = (Spider) event.getDamager();

            // 공격을 받는 엔티티가 플레이어인 경우
            if (event.getEntity() instanceof Player) {
                Player player = (Player) event.getEntity();

                // 플레이어가 거미줄에 걸려있는지 확인
                if (isPlayerInCobweb(player)) {
                    return; // 플레이어가 거미줄에 걸려있으면 거미줄 난사하지 않음
                }

                spawnCobwebs(spider.getLocation()); // 거미줄 난사
            }

            // 공격을 받는 엔티티가 거미인 경우에도 거미줄 난사
            else if (event.getEntity() instanceof Spider) {
                spawnCobwebs(spider.getLocation()); // 거미줄 난사
            }
        }
    }

    // 거미줄 난사하는 메소드
    private void spawnCobwebs(Location location) {
        World world = location.getWorld();
        Random random = new Random();

        // 거미줄(떨어지는 블록)을 25방향으로 난사
        for (int i = 0; i < 25; i++) {  // 반복 횟수를 25로 설정
            // FallingBlock 생성
            FallingBlock cobweb = world.spawnFallingBlock(location, Material.COBWEB, (byte) 0);
            Vector velocity = new Vector(
                    (random.nextDouble() - 0.5) * 2, // X 방향
                    0.5, // Y 방향 (살짝 떠오르게)
                    (random.nextDouble() - 0.5) * 2  // Z 방향
            );
            cobweb.setVelocity(velocity);
            cobweb.setDropItem(false); // 아이템으로 떨어지지 않도록 설정

            // 거미줄에 걸리지 않도록 FallingBlock의 물리적 상호작용을 무시 (거미줄이 걸리지 않도록 하는 부분)
            cobweb.setGlowing(true);  // 빛을 발산하는 효과를 줄 수도 있음
        }
    }

    // 플레이어가 거미줄에 걸려있는지 확인하는 메소드
    private boolean isPlayerInCobweb(Player player) {
        Location playerLocation = player.getLocation();
        // 플레이어 위치 주변에 거미줄이 있는지 체크
        for (int x = -1; x <= 1; x++) {
            for (int y = -1; y <= 1; y++) {
                for (int z = -1; z <= 1; z++) {
                    Location loc = playerLocation.clone().add(x, y, z);
                    if (loc.getBlock().getType() == Material.COBWEB) {
                        return true; // 거미줄이 있으면 걸려있다고 판단
                    }
                }
            }
        }
        return false; // 거미줄이 없다면 걸려있지 않음
    }
}
