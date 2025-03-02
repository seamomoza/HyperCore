package org.java.hyperEnderMan;

import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.event.*;
import org.bukkit.event.entity.*;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.*;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Random;

public class HyperEnderMan extends JavaPlugin implements Listener {

    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(this, this);
        getLogger().info("HyperEnderman plugin enabled!");
    }

    @EventHandler
    public void on(EntityDeathEvent event) {
        if (event.getEntity() instanceof Enderman) {

            event.getDrops().clear();
            event.getDrops().add(new ItemStack(Material.ENDER_PEARL,3 ));
        }
    }


    @EventHandler
    public void onEndermanHitPlayer(EntityDamageByEntityEvent event) {
        // 플레이어가 엔더맨에게 맞을 때 아이템 제거
        if (event.getEntity() instanceof Player && event.getDamager() instanceof Enderman) {
            Player player = (Player) event.getEntity();
            Enderman enderman = (Enderman) event.getDamager();
            removeRandomItem(player);
        }

        // 플레이어가 엔더맨을 때렸을 때 아이템 제거
        if (event.getDamager() instanceof Player && event.getEntity() instanceof Enderman) {
            Player player = (Player) event.getDamager();
            Enderman enderman = (Enderman) event.getEntity();
            removeRandomItem(player);
        }
    }

    private void removeRandomItem(Player player) {
        Random random = new Random();
        Inventory inventory = player.getInventory();
        int randomSlot;

        // 36개 슬롯에서 갑옷 슬롯(36-39번)을 제외한 슬롯을 선택
        do {
            randomSlot = random.nextInt(inventory.getSize());
        } while (randomSlot >= 36 && randomSlot < 40);  // 갑옷 슬롯은 제외

        ItemStack itemToRemove = inventory.getItem(randomSlot);

        // 공기인 경우를 제외하고 아이템이 존재하는지 확인
        if (itemToRemove != null && itemToRemove.getType() != Material.AIR) {
            // 아이템 삭제
            inventory.setItem(randomSlot, null); // 아이템 삭제
            player.sendMessage(ChatColor.RED + "ㅋ! " + player.getName() + "님 엔더맨한테 " + itemToRemove.getType().toString() + "이(가) 탈탈털렸네요?");
        }
    }

    @Override
    public void onDisable() {
        getLogger().info("HyperEnderman plugin disabled!");
    }
}
