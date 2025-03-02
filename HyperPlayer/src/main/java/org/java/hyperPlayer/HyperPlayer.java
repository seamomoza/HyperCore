package org.java.hyperPlayer;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Set;

public class HyperPlayer extends JavaPlugin implements Listener {
    private final Set<String> placedBlocks = new HashSet<>();

    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(this, this);
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        // 플레이어가 설치한 블록의 위치를 추적
        placedBlocks.add(event.getBlock().getLocation().toString());
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        // 설치된 블록인지 확인
        if (placedBlocks.contains(event.getBlock().getLocation().toString())) {
            // 설치된 블록은 드랍률을 적용하지 않음
            return;
        }

        // 블록이 부서질 때 나오는 드랍 아이템을 가져옴
        for (ItemStack item : event.getBlock().getDrops(event.getPlayer().getItemInHand())) {
            // 아이템을 한 번 더 드랍하여 자연스럽게 두 배 드랍되도록 함
            event.getBlock().getWorld().dropItemNaturally(event.getBlock().getLocation(), item);
            event.getBlock().getWorld().dropItemNaturally(event.getBlock().getLocation(), item);
        }

        // 기본 드랍을 비활성화하여 우리가 설정한 아이템만 드랍되도록 함
        event.setDropItems(false);
    }


    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        event.getDrops().clear();

        // 플레이어가 하드코어 모드일 때 리스폰을 할 수 있게 설정
        if (player.getWorld().getDifficulty() == org.bukkit.Difficulty.HARD) {
            // 하드코어 모드에서는 죽으면 리스폰이 안 되는데, 이를 수동으로 리스폰하도록 설정
            Bukkit.getScheduler().runTask(this, () -> {
                player.spigot().respawn();
            });
        }
    }

    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        Player player = event.getPlayer();
        player.sendMessage(ChatColor.GREEN + "인벤토리가 초기화됐습니다!");
        player.getInventory().clear();

        // 마지막 리스폰 위치로 순간이동
        if (player.getBedSpawnLocation() != null) {
            event.setRespawnLocation(player.getBedSpawnLocation());  // 마지막 침대 위치로 리스폰
        } else {
            // 침대 위치가 없다면 기본 월드의 스폰 위치로 리스폰
            event.setRespawnLocation(Bukkit.getWorld("world").getSpawnLocation());
        }
    }

}