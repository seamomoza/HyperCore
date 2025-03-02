package org.kotlin.hyperSkeleton

import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.*
import org.bukkit.inventory.ItemStack
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import org.bukkit.event.Listener
import org.bukkit.event.EventHandler
import org.bukkit.event.entity.*
import org.bukkit.event.world.ChunkLoadEvent
import org.bukkit.event.entity.CreatureSpawnEvent
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.scheduler.BukkitRunnable
import java.util.*
import org.bukkit.scheduler.BukkitTask
import kotlin.random.Random
import org.bukkit.util.Vector

class HyperSkeleton : JavaPlugin(), Listener {

    // 스켈레톤별로 실행 중인 타이머를 관리하는 맵
    private val skeletonTasks = HashMap<UUID, BukkitTask>()

    override fun onEnable() {
        server.pluginManager.registerEvents(this, this)
    }

    // **스켈레톤이 스폰될 때 머리에 아카시아 버튼 착용 (햇빛 방어)**
    @EventHandler
    fun onSkeletonSpawn(event: CreatureSpawnEvent) {
        val entity = event.entity
        if (entity is Skeleton) {
            val button = ItemStack(Material.ACACIA_BUTTON)
            entity.equipment?.helmet = button
            entity.equipment?.setHelmetDropChance(0f) // 버튼이 드롭되지 않도록 설정
        }
    }

    // **기본 화살 발사를 막음**
    @EventHandler
    fun onSkeletonShoot(event: EntityShootBowEvent) {
        if (event.entity is Skeleton) {
            event.isCancelled = true // 스켈레톤의 기본 공격을 취소
        }
    }

    // **스켈레톤이 공격받으면 TP + 3초간 독화살 난사**
    @EventHandler
    fun onProjectileHit(event: ProjectileHitEvent) {
        val projectile = event.entity
        if (projectile is Projectile && event.hitEntity is LivingEntity) {
            val hitEntity = event.hitEntity as LivingEntity
            if (projectile.shooter is Player) {
                val player = projectile.shooter as Player
                if (hitEntity is Skeleton) {
                    hitEntity.teleport(player.location)

                    object : BukkitRunnable() {
                        var ticks = 0

                        override fun run() {
                            if (ticks >= 60) {
                                cancel()
                                return
                            }

                            repeat(5) {
                                val arrow = hitEntity.world.spawnArrow(
                                    hitEntity.eyeLocation,
                                    Vector(0, 0, 0), // 초기 속도
                                    2.0f, // 힘
                                    12.0f // 확산 정도
                                )

                                arrow.velocity = Vector(
                                    Random.nextDouble(-1.0, 1.0),
                                    Random.nextDouble(-1.0, 1.0),
                                    Random.nextDouble(-1.0, 1.0)
                                ).normalize().multiply(2.0)

                                // 독 화살 설정
                                arrow.addCustomEffect(
                                    PotionEffect(PotionEffectType.POISON, 100, 2),
                                    true
                                )
                            }

                            ticks++
                        }
                    }.runTaskTimer(this@HyperSkeleton, 0L, 1L)
                }
            }
        }
    }


    @EventHandler
    fun onSkeletonTarget(event: EntityTargetEvent) {
        val entity = event.entity
        if (entity is Skeleton) {
            val skeleton = entity

            // 기존에 실행 중인 스켈레톤의 루틴이 있다면 중지
            skeletonTasks[skeleton.uniqueId]?.cancel()

            // 새로운 루틴 생성 및 실행
            val task = object : BukkitRunnable() {
                override fun run() {
                    // 스켈레톤이 살아있는지 확인
                    if (!skeleton.isValid || skeleton.isDead) {
                        cancel() // 죽었으면 타이머 종료
                        skeletonTasks.remove(skeleton.uniqueId)
                        return
                    }

                    val target = skeleton.target as? LivingEntity ?: return

                    // 10발의 화살을 한 번에 발사
                    repeat(5) {
                        val arrow: Arrow = skeleton.launchProjectile(Arrow::class.java)

                        // 기본 방향 (목표를 향해)
                        val direction = target.location.subtract(skeleton.location).toVector().normalize()

                        // 화살이 퍼지도록 약간의 랜덤 벡터 추가
                        val spread = Vector(
                            Random.nextDouble(-0.15, 0.15),
                            Random.nextDouble(-0.05, 0.05),
                            Random.nextDouble(-0.15, 0.15)
                        )

                        arrow.velocity = direction.add(spread).multiply(2.0) // 속도 증가
                    }
                }
            }.runTaskTimer(this, 0L, 1L) // 1틱마다 실행 (기본 공격보다 훨씬 빠름)

            // 실행 중인 태스크를 관리 목록에 추가
            skeletonTasks[skeleton.uniqueId] = task as BukkitTask
        }
    }

    @EventHandler
    fun onSkeletonDeath(event: EntityDeathEvent) {
        val entity = event.entity
        if (entity is Skeleton) {
            // 스켈레톤이 죽으면 해당 타이머 중지
            skeletonTasks[entity.uniqueId]?.cancel()
            skeletonTasks.remove(entity.uniqueId)
        }
    }
}