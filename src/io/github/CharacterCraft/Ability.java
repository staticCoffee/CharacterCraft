package io.github.CharacterCraft;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

/*
 * TODO: Recreate ability class to match the Spell class.
 */

/**
 * This class holds various methods that modify either the player and their
 * attributes, or the world around them.
 */
public class Ability {
    private final CharacterCraft characterCraft;

    /**
     * Ties the main class to this one.
     * @param characterCraft The base class for this plugin.
     */
    public Ability(CharacterCraft characterCraft) {
        this.characterCraft = characterCraft;
    }

    /**
     * Cast a spell for a certain amount of time.
     * @param abilityName The name of the spell being cast.
     * @param manaCost The amount that this spell will cost the player in mana.
     * @param lvlToCast How high a player's intelligence must be to cast this spell.
     * @param player The player to put into the HashMaps.
     * @param user The User object representing this player.
     * @return boolean true or false
     */
    public boolean castAbility(
            String abilityName, double manaCost, double lvlToCast,
            Player player, UserCharacter user
    ) {
		/*
		 * TODO: Check if player is in the correct class to cast this spell,
		 * or if they're apart of the ascended class.
		 */

        // Check if user intelligence high enough to cast this spell
        if (user.attributes.get("faith") < lvlToCast) {
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

        if (abilityName.equalsIgnoreCase("heartofgod")) {
            heartOfGod(user);
        }else if (abilityName.equalsIgnoreCase("heardprayer")) {
            heardPrayer(user);
        }else if (abilityName.equalsIgnoreCase("berserk")) {
            berserk(user);
        }else if (abilityName.equalsIgnoreCase("foocus")) {
            focus(user);
        }else if (abilityName.equalsIgnoreCase("observation")) {
            observation(user);
        }

        // Define cool down time and mana drain.
        user.manaPoints -= (manaCost - user.attributes.get("intelligence") / 2);
        ArrayList<String> l = new ArrayList<String>();
        l.addAll(user.coolDowns);
        BukkitRunnable runnable = new BukkitRunnable() {
            public void run() {
                for (String n : characterCraft.getUserConfig().getKeys(false)) {
                    for (String s : l) {
                        if (characterCraft.getUserConfig().getLong(n + ".abilities.cooldowns." + s) > player.getPlayerTime()) {
                            characterCraft.getUserConfig().set(n + ".abilities.cooldowns." + s,
                                    characterCraft.getUserConfig().getLong(n + ".abilities.cooldowns." + s) - 1);
                        }

                        if (abilityName.equalsIgnoreCase("invisibility")) {
                            int spellLength = (int) (user.concentration) + 5;

                            int coolTime = characterCraft.getUserConfig().getInt(player.getUniqueId().toString() + ".abilities.cooldowns." + abilityName);
                            int killTime = (int) (coolTime - (user.attributes.get("concentration") + 3));

                            if (player.getPlayerTime() == (player.getPlayerTime() + killTime)) {
                                visibility(player);
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
     * Calculates appropriate length for an ability to last.
     * @param defaultTime The default amount of time an ability should last.
     * @param userConcentration Should only take the User sub-skill `concentration`.
     * @return int The appropriate length for the ability to last.
     */
    public static int getAbilityCastTime(int defaultTime, double userConcentration) {
        int abilityCastTime = (int) (defaultTime + userConcentration);
        return abilityCastTime;
    }

    /**
     * Calculates how much a base statistic or sub-skill should be affected by an ability.
     * @param defaultEffect The default amount added to a base statistic or sub-skill.
     * @param userFaith Should only take the base statistic `faith` from user attributes.
     * @return double The amount a base statistic or sub-skill will be modified.
     */
    public static double getAbilityEffectiveness(double defaultEffect, double userFaith) {
        double abilityEffectiveness = defaultEffect + (userFaith / 10);
        return abilityEffectiveness;
    }

    /**
     * Increases player vitality.
     * @param user The UserCharacter object of a given player.
     */
    public static void heartOfGod(UserCharacter user) {
        double buff = getAbilityEffectiveness(2.0, user.attributes.get("faith"));
        user.attributes.replace("vitality", user.attributes.get("vitality") + buff);
    }

    /**
     * Increases player faith.
     * @param user The UserCharacter object of a given player.
     */
    public static void heardPrayer(UserCharacter user) {
        double buff = getAbilityEffectiveness(1.0, user.attributes.get("faith"));
        user.attributes.replace("faith", user.attributes.get("faith") + buff);
    }

    /**
     * Increases player strength.
     * @param user The UserCharacter object of a given player.
     */
    public static void berserk(UserCharacter user) {
        double buff = getAbilityEffectiveness(1.0, user.attributes.get("faith"));
        user.attributes.replace("strength", user.attributes.get("strength") + buff);
    }

    /**
     * Increases player dexterity.
     * @param user The UserCharacter object of a given player.
     */
    public static void focus(UserCharacter user) {
        double buff = getAbilityEffectiveness(1.0, user.attributes.get("faith"));
        user.attributes.replace("dexterity", user.attributes.get("dexterity") + buff);
    }

    /**
     * Increases player intelligence.
     * @param user The UserCharacter object of a given player.
     */
    public static void observation(UserCharacter user) {
        double buff = getAbilityEffectiveness(1.0, user.attributes.get("faith"));
        user.attributes.replace("intelligence", user.attributes.get("intelligence") + buff);
    }


    /**
     * Hides player from everyone else on the server.
     * @param player The player to hide.
     */
    public static void invisibility(Player player) {
        // Check if the correct Player and User objects have been passed to this method.
        for (Player p : Bukkit.getOnlinePlayers()) {
            p.hidePlayer(player);
        }
    }

    /**
     * Shows player to everyone else on the server.
     * @param player The player to show.
     */

    public static void visibility(Player player) {
        // Check if the correct Player and User objects have been passed to this method.
            for (Player p : Bukkit.getOnlinePlayers()) {
                p.showPlayer(player);
            }
        }
}
