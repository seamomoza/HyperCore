package org.java.hyperPlayer;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;

public class HyperPlayer extends JavaPlugin implements Listener {

    @Override
    public void onEnable() {
        // 플러그인 활성화 시 이벤트 리스너 등록
        Bukkit.getServer().getPluginManager().registerEvents(this, this);
        getLogger().info("드랍률 2배 적용됨!");
    }

    @Override
    public void onDisable() {
        getLogger().info("드랍률 1배로 복구됨.");
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        Block block = event.getBlock();
        block.setMetadata("placed_by_player", new FixedMetadataValue(this, true));
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Block block = event.getBlock();

        // 플레이어가 설치한 블록이면 2배 드랍을 적용하지 않음
        if (block.hasMetadata("placed_by_player")) {
            return;
        }

        List<ItemStack> extraDrops = new ArrayList<>();
        for (ItemStack drop : block.getDrops()) {
            extraDrops.add(new ItemStack(drop.getType(), drop.getAmount()));
        }
        extraDrops.forEach(item -> block.getWorld().dropItemNaturally(block.getLocation(), item));
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
        player.sendMessage(ChatColor.RED + "ㅋ!" + player.getName() + "님" + "죽으셨네요? 인벤토리는 제가 가져갑니다ㅋ");
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