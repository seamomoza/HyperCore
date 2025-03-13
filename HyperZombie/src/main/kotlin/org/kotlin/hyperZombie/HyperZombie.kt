package org.kotlin.hyperZombie

import org.bukkit.Material
import org.bukkit.attribute.Attribute
import org.bukkit.entity.Entity
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

    @EventHandler
    fun onZombieAttack(event: EntityDamageByEntityEvent) {
        if (event.damager is Zombie) {
            val zombie = event.damager as Zombie
            val target = event.entity as? LivingEntity ?: return

            // 공격을 취소
            event.isCancelled = true

            // 1틱 간격으로 10번 공격
            object : BukkitRunnable() {
                var attacks = 0

                override fun run() {
                    if (attacks < 10) {
                        target.damage(2.0)
                        target.noDamageTicks = 0
                        attacks++
                    } else {
                        cancel()
                    }
                }
            }.runTaskTimer(this, 0L, 1L)
        }
    }
}
