package io.github.CharacterCraft;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Set;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.DragonFireball;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.Player;
import org.bukkit.entity.SmallFireball;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * This class holds various methods that modify either the player and their
 * attributes, or the world around them.
 */
public class Spell {
    private final CharacterCraft characterCraft;

    /**
     * Ties the main class to this one.
     * @param characterCraft The base class for this plugin.
     */
    public Spell(CharacterCraft characterCraft) {
        this.characterCraft = characterCraft;
    }

    /**
     * Cast a spell for a certain amount of time.
     * @param spellName The name of the spell being cast.
     * @param manaCost The amount that this spell will cost the player in mana.
     * @param lvlToCast How high a player's intelligence must be to cast this spell.
     * @param player The player to put into the HashMaps.
     * @param user The User object representing this player.
     * @return boolean true or false
     */
    public boolean castSpell(
            String spellName, double manaCost, double lvlToCast,
            Player player, UserCharacter user
    ) {
		/*
		 * TODO: Check if player is in the correct class to cast this spell,
		 * or if they're apart of the ascended class. 
		 */

        // Check if user intelligence high enough to cast this spell
        if (user.attributes.get("intelligence") < lvlToCast) {
            player.sendMessage(ChatColor.RED + "You must have intelligence higher than level " + lvlToCast + " to cast this spell!");
            return false;
        }

        // Determine how much mana the spell should cost.
        if (user.manaPoints < manaCost) {
            player.sendMessage(ChatColor.RED + "You are out of mana!");
            return false;
        }

		/*
		// Check if player is in cool down, and if so, block them from casting this spell.
		if (timeMap.containsKey(player.getUniqueId())) {
			player.sendMessage(ChatColor.RED + "You must wait for " + timeMap.get(player.getUniqueId()) + " seconds to cast this spell again.");
			return false;
		}
		*/

        if (spellName.equalsIgnoreCase("blink")) {
            teleport(player);
        }else if (spellName.equalsIgnoreCase("fly")) {
            fly(player, true);
        }else if (spellName.equalsIgnoreCase("smallfireball")) {
            shootSmallFireball(player);
        }else if (spellName.equalsIgnoreCase("largefireball")) {
            shootLargeFireball(player);
        }else if (spellName.equalsIgnoreCase("dragonfireball")) {
            shootDragonFireball(player);
        }else if (spellName.equalsIgnoreCase("lightning")) {
            lightningBolt(player);
        }

        // Define cool down time and mana drain.
        user.manaPoints -= (manaCost - user.attributes.get("intelligence") / 2);
        ArrayList<String> l = new ArrayList<String>();
        l.addAll(user.coolDowns);
        BukkitRunnable runnable = new BukkitRunnable() {
            public void run() {
                for (String n : characterCraft.getUserConfig().getKeys(false)) {
                    for (String s : l) {
                        if (characterCraft.getUserConfig().getLong(n + ".spells.cooldowns." + s) > player.getPlayerTime()) {
                            characterCraft.getUserConfig().set(n + ".spells.cooldowns." + s,
                                    characterCraft.getUserConfig().getLong(n + ".spells.cooldowns." + s) - 1);
                        }

                        if (spellName.equalsIgnoreCase("fly")) {
                            int coolTime = characterCraft.getUserConfig().getInt(player.getUniqueId().toString()+".spells.cooldowns."+spellName);
                            int killTime = (int) (coolTime - (user.attributes.get("concentration") + 3));

                            if (player.getPlayerTime() == (player.getPlayerTime() + killTime)) {
                                fly(player, false);
                            }
                        }

                        try {
                            characterCraft.getUserConfig().save(new File(characterCraft.getDataFolder(), "user.yml"));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        if (characterCraft.getUserConfig().getLong(n + ".spells.cooldowns." + s) <= player.getPlayerTime()) {
                            user.coolDowns.removeAll(l);
                            cancel();
                        }
                    }
                }
            }
        };

		try {
            runnable.runTaskTimer(characterCraft, 20, 20);
        } catch (IllegalStateException e) {
                e.printStackTrace();
        }

		return true;
        }

        /**
         * Allows the player to fly.
         * @param player The current, in-game player to apply the spell to.
         * @param allowFlight A true of false value that determines if a player
         * 		  can fly or not.
         */
    public static void fly(Player player, Boolean allowFlight) {
        player.setAllowFlight(allowFlight);
    }

    /**
     * Shoots lightning in the direction the player is looking.
     * @param player The current, in-game player to apply the spell to.
     */
    public static void lightningBolt(Player player) {
        player.getWorld().strikeLightning(player.getTargetBlock((Set<Material>) null, 200).getLocation());
    }

    /**
     * Launches a small fireball in the direction the player is looking.
     * @param player The current, in-game player to apply the spell to.
     */
    public static void shootSmallFireball(Player player) {
        player.launchProjectile(SmallFireball.class);
    }

    /**
     * Launches a fireball in the direction the player is looking.
     * @param player The current, in-game player to apply the spell to.
     */
    public static void shootLargeFireball(Player player) {
        player.launchProjectile(Fireball.class);
    }

    /**
     * Launches a dragon fireball in the direction the player is looking.
     * @param player The current, in-game player to apply the spell to.
     */
    public static void shootDragonFireball(Player player) {
        player.launchProjectile(DragonFireball.class);
    }

    /**
     * Launches a certain number of arrows in the direction the player is looking.
     * @param player The current, in-game player to apply the spell to.
     * @param user The User object that this player is bound to.
     */
    public static void ghostArrows(Player player, UserCharacter user) {
        for (int loops = user.attributes.get("intelligence").intValue() / 2; loops > 0; loops--) {
            player.launchProjectile(Arrow.class);
        }
    }

    /**
     * Changes a player's location to the block he/she is looking at.
     * @param player The current, in-game player to apply the spell to.
     */
    public static void teleport(Player player) {
        Block block = player.getTargetBlock((Set<Material>) null, 200);
        Location loc = block.getLocation();
        player.teleport(loc);
    }
}
