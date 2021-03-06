package by.lemooor.event;

import by.lemooor.Main;
import by.lemooor.MyWorldEdit;
import by.lemooor.creator.CreateMaps;
import org.bukkit.*;
import org.bukkit.block.Chest;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class PlayerEvent implements Listener {
    private final CreateMaps createMaps;
    private Iterator<Location> locationIterator;

    private Map<String, Set<Location>> map;
    private Map<String, List<ItemStack>> item;

    private final BossBar bar = Bukkit.getServer().createBossBar(
            "Ожидание игроков", BarColor.WHITE, BarStyle.SEGMENTED_12
    );

    public PlayerEvent(String config) {
        createMaps = new CreateMaps(config);
    }

    @EventHandler
    public void join(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        Collection<? extends Player> players = Bukkit.getOnlinePlayers();

        bar.addPlayer(player);

        Bukkit.broadcastMessage(player.getName() + " присоединился к игре. [" + players.size() + "/2]");

        if (players.size() == 1) {
            createMaps.putting();

            map = createMaps.getMap();
            item = createMaps.getItems();

            locationIterator = map.get("spawn").iterator();
        }
        Location teleport = locationIterator.next();

        player.getInventory().setContents(new ItemStack[]{});
        player.setHealth(20);
        player.setSaturation(20);

        teleport.getBlock().setType(Material.AIR);
        player.teleport(teleport);
        player.setGameMode(GameMode.SURVIVAL);

        if (players.size() == 2) {
            bar.removeAll();

            Bukkit.getScheduler().runTaskLater(Main.getPlugin(Main.class), () -> {
                Set<Location> locations = map.get("spawn");

                for (Location location : locations) {
                    new Location(
                            location.getWorld(),
                            location.getX(),
                            location.getY() - 1,
                            location.getZ()
                    ).getBlock().setType(Material.AIR);
                }
            }, 20);

            Bukkit.getScheduler().runTaskTimer(Main.getPlugin(Main.class), () -> {
                Bukkit.broadcastMessage(ChatColor.YELLOW + "сундуки заполнены");

                Random random = new Random();

                for (int i = 1; i <= 3; i++) {
                    Set<Location> locations = map.get("chest_" + i);
                    List<ItemStack> items = item.get("item_" + i);

                    for (Location location : locations) {
                        Chest chest = (Chest) location.getBlock().getState();
                        Inventory inventory = chest.getBlockInventory();

                        inventory.setContents(new ItemStack[]{});

                        int bound = random.nextInt(10) + 5;

                        for (int j = 0; j < bound; j += random.nextInt(3) + 1) {
                            inventory.setItem(j, items.get(random.nextInt(items.size())));
                        }
                    }
                }
            }, 20, 2400);
        }
    }

    @EventHandler
    public void death(PlayerDeathEvent event) {
        Collection<? extends Player> players = Bukkit.getOnlinePlayers();

        event.getEntity().setGameMode(GameMode.SPECTATOR);

        boolean one = false;
        Player alive = event.getEntity();

        for (Player player : players) {
            if (player.getGameMode() != GameMode.SPECTATOR) {
                one = true;
                alive = player;
                continue;
            }
            if (one && player.getGameMode() != GameMode.SPECTATOR) {
                return;
            }
        }

        Bukkit.broadcastMessage(ChatColor.DARK_GREEN + alive.getName() + " победил");

        Bukkit.getScheduler().runTaskLater(Main.getPlugin(Main.class), () -> {
            for (Player player : players) {

            }

            MyWorldEdit.pasteSchematic(
                    "skywars_solo_1", new Location(
                            event.getEntity().getWorld(), 12, 27, 8
                    ), false, Main.getPlugin(Main.class)
            );
        }, 200);
    }
}