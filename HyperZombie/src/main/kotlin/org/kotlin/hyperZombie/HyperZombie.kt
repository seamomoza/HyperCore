package org.kotlin.hyperZombie
    import org.bukkit.Bukkit
    import org.bukkit.attribute.Attribute
    import org.bukkit.entity.EntityType
    import org.bukkit.entity.LivingEntity
    import org.bukkit.entity.Zombie
    import org.bukkit.event.EventHandler
    import org.bukkit.event.Listener
    import org.bukkit.event.entity.EntityDamageByEntityEvent
    import org.bukkit.plugin.java.JavaPlugin
    import org.bukkit.scheduler.BukkitRunnable

    class HyperZombie : JavaPlugin(), Listener {
        override fun onEnable() {
            logger.info("FastZombiePlugin has been enabled!")
            Bukkit.getPluginManager().registerEvents(this, this)
        }

        override fun onDisable() {
            logger.info("FastZombiePlugin has been disabled!")
        }

        @EventHandler
        fun onZombieAttack(event: EntityDamageByEntityEvent) {
            val damager = event.damager
            val target = event.entity

            if (damager is Zombie && target is LivingEntity) {
                event.isCancelled = true // 기본 공격 무효화
                val damage = damager.getAttribute(Attribute.ATTACK_DAMAGE)?.value ?: 4.0
                target.noDamageTicks = 0 // 무적 시간 제거

                object : BukkitRunnable() {
                    override fun run() {
                        if (!damager.isDead && !target.isDead) {
                            target.damage(damage, damager)
                            target.noDamageTicks = 0 // 지속적으로 무적 시간 제거
                        } else {
                            cancel()
                        }
                    }
                }.runTaskTimer(this, 0L, 1L) // 1틱마다 피해 적용
            }
        }
    }
