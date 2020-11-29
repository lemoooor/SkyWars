package by.lemooor.creator;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class CreateMaps {
    private final String config;

    private Map<String, Set<Location>> allLocations = new HashMap<>();
    private Map<String, List<ItemStack>> items = new HashMap<>();

    public CreateMaps(String config) {
        this.config = config;
    }

    public void putting() {
        World world = Bukkit.getServer().getWorld("world");

        MongoClient client = new MongoClient(
                new MongoClientURI(config)
        );
        MongoDatabase database = client.getDatabase("skyWars");
        MongoCollection<Document> collection = database.getCollection("points");

        for (String name : new String[]{
                "spawn 13", "chest_1 6", "chest_2 4",
                "chest_3 4", "item_1", "item_2", "item_3"
        }) {
            List<String> list = getDoc(collection, name.split(" ")[0]);

            if (name.split(" ").length != 1) {
                Set<Location> set = new HashSet<>();

                for (String loc : list) {
                    Location point = new Location(world,
                            Double.parseDouble(loc.split(" ")[0]),
                            Double.parseDouble(name.split(" ")[1]),
                            Double.parseDouble(loc.split(" ")[1])
                    );
                    set.add(point);

                    point.getBlock().setType(Material.CHEST);
                }

                allLocations.put(name.split(" ")[0], set);
            } else {
                List<ItemStack> arrayList = new ArrayList<>();

                for (String id : list) {
                    ItemStack item = new ItemStack(
                            Integer.parseInt(id.split(" ")[0])
                    );

                    if (id.split(" ").length != 1) {
                        item.setAmount(
                                Integer.parseInt(id.split(" ")[1])
                        );
                    }
                    arrayList.add(item);
                }

                items.put(name, arrayList);
            }
        }
    }

    private List<String> getDoc(MongoCollection<Document> collection, String name) {
        return collection.find(new Document("name", "points")).first().getList(name, String.class);
    }

    public Map<String, Set<Location>> getMap() {
        return allLocations;
    }

    public Map<String, List<ItemStack>> getItems() {
        return items;
    }
}