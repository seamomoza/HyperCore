package org.java.hyperEnderMan;

import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.event.*;
import org.bukkit.event.entity.*;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.*;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Arrays;
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
            event.getDrops().add(new ItemStack(Material.ENDER_PEARL, 3));
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
        boolean hasItemToSteal = false; // 아이템이 하나라도 있는지 체크

        // 인벤토리에서 유효한 아이템이 하나라도 있는지 확인
        for (int i = 0; i < inventory.getSize(); i++) {
            ItemStack item = inventory.getItem(i);
            if (item != null && item.getType() != Material.AIR && (i < 36 || i > 39)) { // 갑옷 슬롯(36-39)은 제외
                hasItemToSteal = true;
                break; // 아이템이 있으면 루프 종료
            }
        }

        // 공기만 남아있다면 메시지 출력하고 아무것도 삭제하지 않음
        if (!hasItemToSteal) {
            ChatColor[] colors = Arrays.stream(ChatColor.values())
                    .filter(color -> color != ChatColor.STRIKETHROUGH)  // 난독체 제외
                    .toArray(ChatColor[]::new);
            ChatColor randomColor = colors[random.nextInt(colors.length)];
            player.sendMessage(randomColor + "도난당할 아이템이 없습니다!");
            return; // 아이템이 없으면 함수 종료
        }

        // 인벤토리에서 무작위로 슬롯을 선택하고, 갑옷 슬롯(36-39번)을 제외하고 공기인 경우를 제외하고 아이템을 삭제
        do {
            randomSlot = random.nextInt(inventory.getSize()); // 인벤토리 크기 내에서 랜덤 슬롯 선택
        } while ((randomSlot >= 36 && randomSlot <= 39) || inventory.getItem(randomSlot) == null || inventory.getItem(randomSlot).getType() == Material.AIR); // 갑옷 슬롯과 공기 아이템인 경우 다시 선택

        // 슬롯에서 아이템 가져오기
        ItemStack itemToRemove = inventory.getItem(randomSlot);

        // 아이템이 존재하면 삭제
        if (itemToRemove != null && itemToRemove.getType() != Material.AIR) {
            // 아이템 삭제
            inventory.setItem(randomSlot, null);

            // 랜덤 색상의 메시지 출력 (STRIKETHROUGH 없이)
            ChatColor[] colors = Arrays.stream(ChatColor.values())
                    .filter(color -> color != ChatColor.STRIKETHROUGH)  // 난독체 제외
                    .toArray(ChatColor[]::new);
            ChatColor randomColor = colors[random.nextInt(colors.length)];
            player.sendMessage(randomColor + itemToRemove.getType().toString() + "을 도난당했습니다!");

            // 좀비 주민 치료 소리 재생
            player.getWorld().playSound(player.getLocation(), Sound.ENTITY_ZOMBIE_VILLAGER_CURE, 1.0F, 1.0F);
        }
    }
}