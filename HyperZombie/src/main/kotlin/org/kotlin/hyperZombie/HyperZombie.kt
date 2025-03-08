package org.kotlin.hyperZombie

import org.bukkit.Material
import org.bukkit.attribute.Attribute
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Zombie
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.EntitySpawnEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.potion.PotionEffectType
import org.bukkit.scheduler.BukkitRunnable

class HyperZombie : JavaPlugin(), Listener {

    override fun onEnable() {
        server.pluginManager.registerEvents(this, this)
    }

    // 하이퍼 좀비가 스폰될 때 속도 증가
    @EventHandler
    fun onZombieSpawn(event: EntitySpawnEvent) {
        if (event.entity is Zombie) {
            val zombie = event.entity as Zombie
            zombie.getAttribute(Attribute.MOVEMENT_SPEED)?.baseValue = 0.4 // 속도 2배 증가
            zombie.getAttribute(Attribute.KNOCKBACK_RESISTANCE)?.setBaseValue(1000000000.0)
            zombie.getPotionEffect(PotionEffectType.RESISTANCE)

            // 아카시아 나무 버튼 아이템을 생성
            val acaciaButton = ItemStack(Material.ACACIA_BUTTON)

            // 좀비 머리에 아카시아 나무 버튼 착용
            zombie.equipment?.helmet = acaciaButton
        }
    }



    // 하이퍼 좀비가 공격할 때 10번 연속 공격
    @EventHandler
    fun onZombieAttack(event: EntityDamageByEntityEvent) {
        if (event.damager is Zombie) {
            val zombie = event.damager as Zombie

            // 대상이 살아있는 엔티티일 경우
            if (event.entity is LivingEntity) {
                event.isCancelled = true
                val target = event.entity as LivingEntity
                var attackCount = 0 // 연속 공격 횟수 카운터

                // 10번 연속 공격
                object : BukkitRunnable() {
                    override fun run() {
                        if (attackCount < 10 && target.isValid && zombie.isValid) {
                            target.damage(2.0) // 2.0 피해를 1회 공격
                            target.noDamageTicks = 0
                            attackCount++ // 공격 횟수 증가
                        } else {
                            cancel() // 10번 공격 후 반복 중단
                        }
                    }
                }.runTaskTimer(this, 0L, 1L) // 0초 후 첫 공격, 그 후 0.5초(10틱)마다 반복 공격
            }
        }
    }
}

private fun LivingEntity.sendActionBar(s: String) {

}
