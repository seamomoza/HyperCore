package io.github.mozza.org.kotlin.hyperzobie

import org.bukkit.Material
import org.bukkit.entity.Creeper
import org.bukkit.entity.EntityType
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.entity.Projectile
import org.bukkit.entity.Zombie
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.CreatureSpawnEvent
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.EntityDeathEvent
import org.bukkit.event.entity.ProjectileHitEvent
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import org.bukkit.plugin.java.JavaPlugin

class HyperZombie : JavaPlugin(), Listener {

    override fun onEnable() {
        // Listener 등록
        server.pluginManager.registerEvents(this, this)
    }

    @EventHandler
    fun onZombieSpawn(event: CreatureSpawnEvent) {
        if (event.entity.type == EntityType.ZOMBIE) {
            val zombie = event.entity as Zombie
            zombie.setBaby() // 작은 크기의 아기 좀비로 설정

            // 좀비에게 투명 효과 부여 (지속 시간 무제한 대신 긴 시간 설정)
            zombie.addPotionEffect(PotionEffect(PotionEffectType.INVISIBILITY, 1000000, 1)) // 예시로 긴 지속 시간 설정
            zombie.addPotionEffect(PotionEffect(PotionEffectType.FIRE_RESISTANCE, 1000000, 1))
            zombie.addPotionEffect(PotionEffect(PotionEffectType.REGENERATION, 1000000, 1))
            zombie.addPotionEffect(PotionEffect(PotionEffectType.STRENGTH, 1000000, 1))
            zombie.addPotionEffect(PotionEffect(PotionEffectType.HEALTH_BOOST, 1000000, 1))
        }
    }

    @EventHandler
    fun onZombieAttack(event: EntityDamageByEntityEvent) {
        val damager = event.damager // 공격자
        if (damager is Zombie) {
            // 좀비가 플레이어나 다른 엔티티를 공격하면 Glow 효과 적용
            damager.addPotionEffect(PotionEffect(PotionEffectType.GLOWING, 20, 1)) // 40틱 = 2초
            damager.addPotionEffect(PotionEffect(PotionEffectType.SLOWNESS, 40, 1))
        }
    }
}
@EventHandler
fun onProjectileHit(event: ProjectileHitEvent) {
    val projectile = event.entity

    if (projectile is Projectile && event.hitEntity is LivingEntity) {
        val hitEntity = event.hitEntity as LivingEntity
        // 발사체의 발사자가 플레이어인지 확인
        if (projectile.shooter is Player) {
            val player = projectile.shooter as Player
            // 타격당한 크리퍼가 맞았을 경우
            if (hitEntity is Zombie) {
                // 크리퍼를 발사자 위치로 텔레포트
                hitEntity.teleport(player.location)
            }
        }
    }
}
