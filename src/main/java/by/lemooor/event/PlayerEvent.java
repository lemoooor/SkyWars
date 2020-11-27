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
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class PlayerEvent implements Listener {
    private CreateMaps createMaps;
    private Iterator<Location> iterator;

    private Map<String, HashSet<Location>> map;
    private Map<String, ArrayList<ItemStack>> item;

    private HashSet<UUID> players = new HashSet<>();

    private BossBar bar = Bukkit.getServer().createBossBar(
            "Ожидание игроков", BarColor.WHITE, BarStyle.SEGMENTED_12
    );

    public PlayerEvent(String config) {
        createMaps = new CreateMaps(config);
    }

    @EventHandler
    public void join(PlayerJoinEvent event) {
        Player player = event.getPlayer();


        bar.addPlayer(player);
        players.add(player.getUniqueId());

        Bukkit.broadcastMessage(player.getName() + " присоединился к игре. [" + players.size() + "/2]");

        if (players.size() == 1) {
            createMaps.putting();

            map = createMaps.getMap();
            item = createMaps.getItems();

            iterator = map.get("spawn").iterator();
        }
        Location teleport = iterator.next();

        player.getInventory().setContents(new ItemStack[]{});
        player.setHealth(20);
        player.setSaturation(20);

        teleport.getBlock().setType(Material.AIR);
        player.teleport(teleport);
        player.setGameMode(GameMode.SURVIVAL);

        if (players.size() == 2) {
            bar.removeAll();

            Bukkit.getScheduler().runTaskLater(Main.getPlugin(Main.class), () -> {
                for (Location location : map.get("spawn")) {
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
                    HashSet<Location> locations = map.get("chest_" + i);
                    ArrayList<ItemStack> items = item.get("item_" + i);

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
        event.getEntity().setGameMode(GameMode.SPECTATOR);
        boolean one = false;

        for (Player player : Bukkit.getOnlinePlayers()) {
            if (event.getEntity().getGameMode() != GameMode.SPECTATOR) {
                one = true;
                continue;
            }
            if (one && event.getEntity().getGameMode() != GameMode.SPECTATOR) {
                return;
            }
        }

        Iterator<UUID> iterator = players.iterator();

        while (iterator.hasNext()) {
            Bukkit.getPlayer(iterator.next()).kickPlayer("gg");
        }

        players.clear();
    }

    @EventHandler
    public void leave(PlayerQuitEvent event) {
        if (event.getPlayer().getServer().getOnlinePlayers().size() == 1) {
            MyWorldEdit.pasteSchematic(
                    "skywars_solo_1", new Location(
                            event.getPlayer().getWorld(), 12, 27, 8
                    ), false, Main.getPlugin(Main.class)
            );
        }
    }
}