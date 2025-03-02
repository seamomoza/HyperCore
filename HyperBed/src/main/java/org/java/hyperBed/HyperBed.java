package org.java.hyperBed;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerBedEnterEvent;
import org.bukkit.event.player.PlayerBedLeaveEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Random;

public class HyperBed extends JavaPlugin implements Listener {

    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(this, this);
    }

    @EventHandler
    public void onPlayerBedEnter(PlayerBedEnterEvent event) {
        Player player = event.getPlayer();

        // 침대에 누웠을 때 금지된 행동 메시지 띄우기
        player.sendMessage(ChatColor.RED + "금지된 행동입니다! 일어나세요!!!!");
    }

    @EventHandler
    public void onPlayerBedLeave(PlayerBedLeaveEvent event) {
        Player player = event.getPlayer();

        player.sendMessage(ChatColor.DARK_RED + "호기심이 많으신가봐요?ㅋㅋ");

        // 플레이어의 위치
        Location bedLocation = player.getLocation();

        // 번개 치기
        bedLocation.getWorld().strikeLightning(bedLocation);

        // 랜덤 몬스터 소환
        spawnRandomMonsters(bedLocation);

        // 밤으로 바꾸기
        setNight();
    }

    private void setNight() {
        // 모든 월드를 밤으로 설정
        for (org.bukkit.World world : Bukkit.getServer().getWorlds()) {
            world.setTime(13000);  // 밤은 13000에 해당
        }
    }

    private void spawnRandomMonsters(Location location) {
        // 위험한 몬스터들 목록
    EntityType[] monsters = {EntityType.BOGGED,EntityType.END_CRYSTAL,EntityType.HORSE,EntityType.PIG,EntityType.WOLF,EntityType.WARDEN,EntityType.CREEPER, EntityType.SKELETON, EntityType.ZOMBIE, EntityType.SPIDER, EntityType.ENDERMAN};
        Random rand = new Random();

        // 랜덤 몬스터 소환
        for (int i = 0; i < 10; i++) {  // 5마리의 몬스터를 소환
            int index = rand.nextInt(monsters.length);
            EntityType monsterType = monsters[index];

            // 몬스터 소환
            Entity monster = location.getWorld().spawnEntity(location.add(rand.nextInt(3) - 1, 0, rand.nextInt(3) - 1), monsterType);
            if (monster instanceof Monster) {
                // 몬스터가 'Monster'라면 공격적인 행동을 하도록 설정
                ((Monster) monster).setTarget(location.getWorld().getPlayers().get(0));
            }
        }
    }
}
