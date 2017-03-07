package io.github.CharacterCraft;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.boss.BarFlag;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * This is the main class for the plugin. It does a number of things
 * like storing player info to `.yml` files, handles events, etc.
 * Basically, this is the class that processes the information
 * needed to get the plugin to work correctly. It extends `JavaPlugin`,
 * to allow access to methods that edits configuration files, sends
 * information back to the console, and more. It implements `Listener`
 * to be able to handle events.
 * Created by staticCoffee on 2/15/2017.
 */
public class CharacterCraft extends JavaPlugin implements Listener{
    // Establishing configuration files.
    private File configf, userCharf;
    private FileConfiguration config, userChar;

    /*
     * Establishes the plugin variable, so that it can be
     * returned, via a method, for other classes to access
     * `JavaPlugin` if needed.
     */
    private static Plugin plugin;

    /*
     * These lists are used to store certain item names
     * for reference in certain events.
     */

    public ArrayList<String> pickaxeList;
    public ArrayList<String> oreList;
    public ArrayList<String> rareList;
    public ArrayList<String> axeList;
    public ArrayList<String> logList;


    /**
     * A main method. This exists to allow for jar creation.
     * @param args A list of arguments.
     */
    public static void main(final String[] args) {

    }

    /**
     * Processes start up information that is used in the plugin,
     * e.g. configuration files, adding item names to lists, etc.
     */
    @SuppressWarnings("unchecked")
    @Override
    public void onEnable() {
        plugin = this;

        BossBar stats = Bukkit.createBossBar(
                "Statistics",
                BarColor.BLUE,
                BarStyle.SOLID
        );

        stats.setVisible(true);


        // Sets up a register of events for event listening.
        getServer().getPluginManager().registerEvents(this, this);

        // Sets up default configuration file.
        getConfig().options().copyDefaults(true);
        saveDefaultConfig();

        // Check if data folder exists, and if not, create one.
        File dir = getDataFolder();
        if (!dir.exists())
            if (!dir.mkdir())
                getLogger().info("Could not create directory for StaticCraft");

        createConfigFiles();

        // Initiate item lists.
        pickaxeList = new ArrayList<String>();
        oreList = new ArrayList<String>();
        rareList = new ArrayList<String>();
        axeList = new ArrayList<String>();
        logList = new ArrayList<String>();

        // Add items to certain lists.
        pickaxeList.add("WOOD_PICKAXE");
        pickaxeList.add("STONE_PICKAXE");
        pickaxeList.add("IRON_PICKAXE");
        pickaxeList.add("GOLD_PICKAXE");
        pickaxeList.add("DIAMOND_PICKAXE");

        oreList.add("COAL_ORE");
        oreList.add("IRON_ORE");
        oreList.add("GOLD_ORE");
        oreList.add("QUARTZ_ORE");
        oreList.add("EMERALD_ORE");
        oreList.add("REDSTONE_ORE");
        oreList.add("DIAMOND_ORE");
        oreList.add("LAPIS_ORE");

        rareList.add("EMERALD_ORE");
        rareList.add("REDSTONE_ORE");
        rareList.add("DIAMOND_ORE");
        rareList.add("LAPIS_ORE");
        rareList.add("GOLD_ORE");

        axeList.add("WOOD_AXE");
        axeList.add("STONE_AXE");
        axeList.add("IRON_AXE");
        axeList.add("GOLD_AXE");
        axeList.add("DIAMOND_AXE");

        logList.add("LOG");
        logList.add("LOG_2");
    }

    /**
     * Creates configuration files to store plugin and player data.
     */
    public void createConfigFiles() {
        configf = new File(getDataFolder(), "config.yml");
        userCharf = new File(getDataFolder(), "user.yml");

        if (!configf.exists()) {
            configf.getParentFile().mkdirs();
            saveResource("config.yml", false);
        }
        if (!userCharf.exists()) {
            userCharf.getParentFile().mkdirs();
            saveResource("user.yml", false);
        }

        config = new YamlConfiguration();
        userChar= new YamlConfiguration();

        try {
            config.load(configf);
            userChar.load(userCharf);

        }catch (IOException e) {
            e.printStackTrace();

        }catch(InvalidConfigurationException e) {
            e.printStackTrace();
        }
    }

    /**
     * This is a mock method of `JavaPlugin`'s `getConfig()`.
     * @return The user configuration file.
     */
    public FileConfiguration getUserConfig() {
        return this.userChar;
    }

    /**
     * Access the plugin variable from other classes.
     * @return The `JavaPlugin` for this class.
     */
    public static Plugin getPlugin() {
        return plugin;
    }

    /**
     * Saves information in a HashMap
     * @param o HashMap object file.
     * @param f File to save to, usually w/ extension `.dat`.
     */
    @Deprecated
    public void saveHashMap(Object o, File f) {
        try {
            // Check if file exists, if not, create it.
            if (!f.exists())
                f.createNewFile();

            // Save information to file.
            ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(f));
            oos.writeObject(o);
            oos.flush();
            oos.close();

        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Loads and returns a HashMap from a given file.
     * @param f Name of the file to retrieve the has map from.
     *
     * @return Object A loaded HashMap from specified file.
     */
    @Deprecated
    public Object loadHashMap(File f) {
        try{
            ObjectInputStream ois = new ObjectInputStream(new FileInputStream(f));
            Object result = ois.readObject();
            ois.close();
            return result;

        }catch (Exception e) {
            return null;
        }
    }

    /**
     * Listens for the player to interact with the world.
     * This is used for spell binds currently, but in the future
     * this method will also be used for skills like mining and wood cutting.
     * @param interactEvent The `PlayerInteractEvent` object from the Bukkit api.
     */
    @EventHandler
    public void onInteract(PlayerInteractEvent interactEvent) {
        // Spell binds
        Player player = interactEvent.getPlayer();
        Object userSave = getUserConfig().get(player.getUniqueId() + ".userClass");
        UserCharacter user = (UserCharacter) userSave;
        ItemStack item = player.getInventory().getItemInMainHand();
        ItemMeta itemMeta = item.getItemMeta();

        if (itemMeta.getDisplayName() == "Golden Staff") {
            if (interactEvent.getAction() == Action.RIGHT_CLICK_BLOCK || interactEvent.getAction() == Action.RIGHT_CLICK_AIR) {
                player.chat("/sc spell cast " + user.spellBinds.get("right"));
            }else if (interactEvent.getAction() == Action.LEFT_CLICK_BLOCK || interactEvent.getAction() == Action.LEFT_CLICK_AIR) {
                player.chat("/sc spell cast " + user.spellBinds.get("left"));
            }
        }
    }

    /**
     * Checks for certain blocks being broken with certain items in hand.
     * This levels respective user skills based on the block break.
     * @param blockEvent The event class used to track when a block breaks.
     */
    @EventHandler
    public void onBlockBreak(BlockBreakEvent blockEvent) {
        Player player = blockEvent.getPlayer();

        Object userSave = getUserConfig().get(player.getUniqueId() + ".userClass");
        UserCharacter user = (UserCharacter) userSave;

        ItemStack item = player.getInventory().getItemInMainHand();
        // Mining skill
        if (pickaxeList.contains(item.getType().toString())) {
            if (oreList.contains(blockEvent.getBlock().getType().toString())) {
                if (rareList.contains(blockEvent.getBlock().getType().toString())) {
                    user.mining += 0.05;
                    user.experience += (0.05 + user.attributes.get("wisdom") / 5);
                    int xp = (int) player.getExp();
                    player.giveExp(xp + 1);
                }else {
                    user.mining += 0.01;
                    user.experience += (0.01 + user.attributes.get("wisdom") / 5);
                }

				/*
				characterSave.put(player.getUniqueId(), user);

				saveHashMap(characterSave, new File(getDataFolder(), "user.dat"));
				*/
                getUserConfig().set(player.getUniqueId().toString() + ".userClass", user);
                try {
                    getUserConfig().save(new File(getDataFolder(), "user.yml"));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }else if (axeList.contains(item.getType().toString())) {
            if (logList.contains(blockEvent.getBlock().getType().toString())) {
                user.woodcutting += 0.01;
                int xp = (int) player.getExp();
                user.experience += (0.01 + user.attributes.get("wisdom") / 5);
            }
			/*
			characterSave.put(player.getUniqueId(), user);

			saveHashMap(characterSave, new File(getDataFolder(), "user.dat"));
			*/
            getUserConfig().set(player.getUniqueId().toString() + ".userClass", user);
            try {
                getUserConfig().save(new File(getDataFolder(), "user.yml"));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

	/*
	 * Cannot implement this until limitations on NMS are worked around.
	 * As of today, the projectile `LlamaSpit` can only be generated by llamas.
	 */
	/*
	public void onSpitDamage() {
		// Check if the entity being spit on is a player.
		if (e.getEntity() instanceof Player) {

			LlamaSpit llamaSpit = (LlamaSpit) e.getDamager();

			// Check if the entity spitting is a player.
			if (llamaSpit.getShooter() instanceof Player){

				// Check if the projectile is LlamaSpit
				if (e.getDamager() instanceof LlamaSpit) {

					// If so, remove damage.
					e.setDamage(0.0);
				}
			}
		}
	}*/


    /**
     * Adds experience points to User's experience variable
     * based on in-game experience gain.
     * @param xpEvent The event class that handles player experience changes.
     */
    @EventHandler
    public void onXPGain(PlayerExpChangeEvent xpEvent) {
        Player player = xpEvent.getPlayer();
        Object userSave = getUserConfig().get(player.getUniqueId() + ".userClass");
        UserCharacter user = (UserCharacter) userSave;
        player.setExp(player.getExp() + 1);
        user.experience += (xpEvent.getAmount() + user.attributes.get("wisdom") / 5);
        user.checkLevel(player);
    }

    /**
     * Increases the amount of XP dropped when fishing.
     * @param fishEvent The player fish event class from Bukkit.
     */
    @EventHandler
    public void onFish(PlayerFishEvent fishEvent) {
        Player player = fishEvent.getPlayer();
        Object userSave = getUserConfig().get(player.getUniqueId() + ".userClass");
        UserCharacter user = (UserCharacter) userSave;
        if (fishEvent.getCaught().toString() != null) {
            int xp = (int) user.fishing / 5;
            fishEvent.setExpToDrop(fishEvent.getExpToDrop() + xp);
            user.fishing += 0.1;
            user.experience += (1 + user.attributes.get("wisdom") / 5);
        }
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent joinEvent) {
		/*
		characterSave = (HashMap<Object, User>) loadHashMap(new File(getDataFolder(), "user.dat"));
		if (characterSave == null) {
			characterSave = new HashMap<Object, User>();
		}
		*/

    }

    @EventHandler
    public void onLeave(PlayerQuitEvent quitEvent) {
        Player player = quitEvent.getPlayer();
        Object userSave = getUserConfig().get(player.getUniqueId() + ".userClass");
        UserCharacter user = (UserCharacter) userSave;

		/*
		characterSave.put(player.getUniqueId(), user);
		saveHashMap(characterSave, new File(getDataFolder(), "user.dat"));
		*/
        getUserConfig().set(player.getUniqueId().toString() + ".userClass", user);
        try {
            getUserConfig().save(new File(getDataFolder(), "user.yml"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Saves character map and disables plugin.
     */
    @Override
    public void onDisable() {
        // saveHashMap(characterSave, new File(getDataFolder(), "user.dat"));
        plugin = null;
    }
}

