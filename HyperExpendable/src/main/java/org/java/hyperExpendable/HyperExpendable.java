package org.java.hyperExpendable;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Random;

public class HyperExpendable extends JavaPlugin implements Listener {

    private Random random = new Random();

    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(this, this);
        getLogger().info("RandomItemBreak 플러그인이 활성화되었습니다!");
    }

    @Override
    public void onDisable() {
        getLogger().info("RandomItemBreak 플러그인이 비활성화되었습니다.");
    }

    // 도구를 사용할 때마다 발생하는 이벤트
    @EventHandler
    public void onToolUse(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack item = event.getItem();

        // 플레이어가 아이템을 들고 있지 않으면 처리하지 않음
        if (item == null) return;

        // 방어구 및 검 아이템의 내구도가 0.1% 확률로 깨지게 처리
        if (isArmorOrSword(item.getType())) {
            // 0.1% 확률로 아이템 깨지게 설정
            if (random.nextInt(100) < 1) { // 0.1% 확률
                // 아이템을 없
                item.setAmount(0);
                // 파티클 효과: 큰 폭발 (파티클 수를 10으로 설정하여 여러 개의 파티클을 소환)
                player.getWorld().spawnParticle(Particle.EXPLOSION, player.getLocation(), 10); // 10개의 파티클을 소환
                // 아이템 깨지는 소리
                player.playSound(player.getLocation(), Sound.ENTITY_ITEM_BREAK, 1.0f, 1.0f);
                player.sendMessage(ChatColor.RED + "아이템 박살ㅋ");
            }
        }

        // 양동이가 사용될 때 깨지게 하는 부분
        if (item.getType() == Material.BUCKET || item.getType() == Material.AXOLOTL_BUCKET ||
                item.getType() == Material.LAVA_BUCKET || item.getType() == Material.WATER_BUCKET ||
                item.getType() == Material.POWDER_SNOW_BUCKET) {
            // 0.1% 확률로 양동이를 깨뜨리기
            if (random.nextInt(100) < 1) {
                // 아이템을 없앰
                item.setAmount(0);
                // 파티클 효과: 큰 폭발 (파티클 수를 10으로 설정하여 여러 개의 파티클을 소환)
                player.getWorld().spawnParticle(Particle.EXPLOSION, player.getLocation(), 10); // 10개의 파티클을 소환
                // 아이템 깨지는 소리
                player.playSound(player.getLocation(), Sound.ENTITY_ITEM_BREAK, 1.0f, 1.0f);
                player.sendMessage(ChatColor.RED + "아이템 박살ㅋ");
            }
        }
    }

    // 블럭을 부술 때 발생하는 이벤트 (도구에 대한 확률 체크)
    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        ItemStack item = player.getInventory().getItemInMainHand();

        // 플레이어가 아이템을 들고 있지 않으면 처리하지 않음
        if (item == null) return;

        Block block = event.getBlock();

        // 도구 아이템인지 확인
        if (isTool(item.getType())) {
            // 0.1% 확률로 아이템 깨지게 설정 (확률을 0.1%로 낮춤)
            if (random.nextInt(100) < 1) { // 0.1% 확률
                // 아이템을 없앰
                item.setAmount(0);
                // 파티클 효과: 큰 폭발 (파티클 수를 10으로 설정하여 여러 개의 파티클을 소환)
                player.getWorld().spawnParticle(Particle.EXPLOSION, player.getLocation(), 10); // 10개의 파티클을 소환
                // 아이템 깨지는 소리
                player.playSound(player.getLocation(), Sound.ENTITY_ITEM_BREAK, 1.0f, 1.0f);
                player.sendMessage(ChatColor.RED + "아이템 박살ㅋ");
            }
        }
    }

    // 도구인지 확인하는 메서드
    private boolean isTool(Material material) {
        return material == Material.DIAMOND_PICKAXE || material == Material.IRON_PICKAXE || material == Material.GOLDEN_PICKAXE ||
                material == Material.STONE_PICKAXE || material == Material.WOODEN_PICKAXE ||
                material == Material.DIAMOND_AXE || material == Material.IRON_AXE || material == Material.GOLDEN_AXE ||
                material == Material.STONE_AXE || material == Material.WOODEN_AXE ||
                material == Material.DIAMOND_SHOVEL || material == Material.IRON_SHOVEL || material == Material.GOLDEN_SHOVEL ||
                material == Material.STONE_SHOVEL || material == Material.WOODEN_SHOVEL ||
                material == Material.SHEARS || material == Material.DIAMOND_HOE || material == Material.IRON_HOE ||
                material == Material.GOLDEN_HOE || material == Material.STONE_HOE || material == Material.WOODEN_HOE ||
                material == Material.FISHING_ROD || material == Material.BOW || material == Material.CROSSBOW ||
                material == Material.SHIELD || material == Material.TRIDENT ||
                material == Material.AXOLOTL_BUCKET || material == Material.BUCKET ||
                material == Material.LAVA_BUCKET || material == Material.WATER_BUCKET || material == Material.POWDER_SNOW_BUCKET ||
                material == Material.NETHERITE_PICKAXE || material == Material.NETHERITE_AXE || material == Material.NETHERITE_SHOVEL ||
                material == Material.NETHERITE_SWORD || material == Material.NETHERITE_HOE;
    }

    // 방어구와 검인지 확인하는 메서드 (검 추가)
    private boolean isArmorOrSword(Material material) {
        return material == Material.DIAMOND_HELMET || material == Material.IRON_HELMET || material == Material.GOLDEN_HELMET ||
                material == Material.LEATHER_HELMET || material == Material.CHAINMAIL_HELMET ||
                material == Material.DIAMOND_CHESTPLATE || material == Material.IRON_CHESTPLATE || material == Material.GOLDEN_CHESTPLATE ||
                material == Material.LEATHER_CHESTPLATE || material == Material.CHAINMAIL_CHESTPLATE ||
                material == Material.DIAMOND_LEGGINGS || material == Material.IRON_LEGGINGS || material == Material.GOLDEN_LEGGINGS ||
                material == Material.LEATHER_LEGGINGS || material == Material.CHAINMAIL_LEGGINGS ||
                material == Material.DIAMOND_BOOTS || material == Material.IRON_BOOTS || material == Material.GOLDEN_BOOTS ||
                material == Material.LEATHER_BOOTS || material == Material.CHAINMAIL_BOOTS ||
                material == Material.NETHERITE_HELMET || material == Material.NETHERITE_CHESTPLATE ||
                material == Material.NETHERITE_LEGGINGS || material == Material.NETHERITE_BOOTS ||
                // 검 추가
                material == Material.DIAMOND_SWORD || material == Material.IRON_SWORD || material == Material.GOLDEN_SWORD ||
                material == Material.STONE_SWORD || material == Material.WOODEN_SWORD || material == Material.NETHERITE_SWORD;
    }
}
