package org.java.hyperCreeper;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.Objects;

public class HyperCreeper extends JavaPlugin implements Listener, Runnable {

    private BukkitTask bukkitTask;
    private Location explosionLocation = null;
    private int explosionTicks = 0;

    @Override
    public void onEnable() {
        // Register event listeners
        Bukkit.getPluginManager().registerEvents(this, this);
        // Start repeating task
        bukkitTask = Bukkit.getScheduler().runTaskTimer(this, this, 0L, 1L);
    }

    @Override
    public void run() {
        if (explosionLocation != null) {
            explosionTicks++;
            double radiusPerTick = 1.0;
            double pointPerCircum = 6.0;

            double radius = radiusPerTick * explosionTicks;
            double circum = 2.0 * Math.PI * radius;
            int pointsCount = (int) (circum / pointPerCircum);
            double angle = 360.0 / pointsCount;

            World world = explosionLocation.getWorld();
            double y = explosionLocation.getY();

            for (int i = 0; i < pointsCount; i++) {
                double currentAngle = Math.toRadians(i * angle);
                double x = -Math.sin(currentAngle);
                double z = Math.cos(currentAngle);

                Location tntLocation = explosionLocation.clone().add(x * radius, 0.0, z * radius);
                TNTPrimed tnt = (TNTPrimed) Objects.requireNonNull(world).spawnEntity(tntLocation, EntityType.TNT);

                tnt.setFuseTicks(0);
            }

            if (explosionTicks >= 100) {
                explosionTicks = 0;
                explosionLocation = null;
            }
        }
    }

    @EventHandler
    public void onCreeperSpawn(CreatureSpawnEvent event) {
        if (event.getEntity() instanceof Creeper) {
            Creeper creeper = (Creeper) event.getEntity();
            AttributeInstance attribute = creeper.getAttribute(Attribute.MAX_HEALTH);
            if (attribute != null) {
                attribute.setBaseValue(1000.0);
                creeper.setHealth(attribute.getBaseValue());
            }
        }
    }

    @EventHandler
    public void onCreeperExplosion(EntityExplodeEvent event) {
        if (event.getEntity() instanceof Creeper) {
            if (explosionLocation == null) {
                explosionLocation = event.getEntity().getLocation();
            }
        }
    }

    @EventHandler
    public void onProjectileHit(ProjectileHitEvent event) {
        Projectile projectile = event.getEntity();
        if (projectile.getShooter() instanceof Player && event.getHitEntity() instanceof LivingEntity) {
            LivingEntity hitEntity = (LivingEntity) event.getHitEntity();
            Player player = (Player) projectile.getShooter();
            if (hitEntity instanceof Creeper) {
                hitEntity.teleport(player.getLocation());
            }
        }
    }

    @Override
    public void onDisable() {
        // Cancel the task and unregister events
        bukkitTask.cancel();
        Bukkit.getPluginManager().disablePlugin(this);
    }
}
