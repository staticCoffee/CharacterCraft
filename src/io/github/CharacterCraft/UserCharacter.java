package io.github.CharacterCraft;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;

/**
 * This class stores a number of important variables
 * that are used as modifiers to the in-game player's attributes.
 * It also stores HashMaps containing the player's available skills
 * and abilities.
 */
public class UserCharacter {
    public HashMap<String, Double> attributes;

    // Declare public class variables:
    public String characterName;
    public String characterClass;
    public String characterRace;

    public int level;
    public boolean canLevelUp;

    public double experience;
    public double neededXpForLvl;

    public double skillPoints;

    public double manaPoints;
    public double maxManaPoints;

    // Sub-skills;
    // Increases rate at which stone and ore blocks are destroyed with a pick-axe.
    public double mining;

    // Increases rate at which wood blocks are destroyed with an axe.
    public double woodcutting;

    /*
     * Increases amount of hunger replenished by food, and
     * decreases the likelihood that cooked food turns into rotten flesh.
     */
    public double cooking;

    // Increases likelihood of catching a fish.
    public double fishing;

    // Increases sword durability.
    public double swordsmanship;

    // Increases arrow damage.
    public double marksmanship;

    // Increases chance enemy's attack does no damage.
    public double block;

    // Increases durability of armor.
    public double armorer;

    // Increases walk speed and jump height.
    public double athleticism;

    // Increases length that abilities last.
    public double concentration;

    // Binds a percentages of this skill to current minecraft level.
    public double enchanting;

    // Increases length that destructive spells last.
    public double destruction;

    // Increases length that healing spells last.
    public double restoration;

    // Increases length that alteration spells last.
    public double alteration;

    // Declare lists and maps for spells and abilities.
    public HashMap<String, String> abilityBinds;
    public ArrayList<String> abilityList;

    public ArrayList<String> spellList;
    public HashMap<String, String> spellBinds;

    public ArrayList<String> coolDowns;

    /**
     * Constructor for the UserCharacterCharacter class. It contains
     * in-game player attribute modifiers as variables.
     * @param charClass The name of the player's selected character class.
     * @param charRace The name of the player's selected character race.
     * @param mana The amount of mana points given to the player based on
     * 				 their respective character class.
     * @param VIT Default vitality.
     * @param STR Default strength.
     * @param END Default endurance.
     * @param DEX Default dexterity.
     * @param DEF Default defense.
     * @param INT Default intelligence.
     * @param WIS Default wisdom.
     * @param FAI Default faith.
     * @param LUC default luck.
     */
    public UserCharacter(String charRace, String charClass, double mana,
                         double VIT, double STR, double END, double DEX, double DEF, double INT,
                         double WIS, double FAI, double LUC) {
        // Set default values:
        characterName = null;
        characterClass = charClass;
        characterRace = charRace;

        level = 1;
        canLevelUp = false;

        experience = 0.0;

        skillPoints = 0.0;

        manaPoints = mana;
        maxManaPoints = mana;

        mining = 0.0;
        woodcutting = 0.0;
        cooking = 0.0;
        fishing = 0.0;
        swordsmanship = 0.0;
        marksmanship = 0.0;
        block = 0.0;
        armorer = 0.0;
        athleticism = 0.0;
        concentration = 0.0;
        enchanting = 0.0;
        destruction = 0.0;
        restoration = 0.0;
        alteration = 0.0;

        attributes = new HashMap<String, Double>();
        attributes.put("vitality", VIT);
        attributes.put("strength", STR);
        attributes.put("endurance", END);
        attributes.put("dexterity", DEX);
        attributes.put("defense", DEF);
        attributes.put("intelligence", INT);
        attributes.put("wisdom", WIS);
        attributes.put("faith", FAI);
        attributes.put("luck", LUC);

        spellList = new ArrayList<String>();
        spellBinds = new HashMap<String, String>();

        abilityList = new ArrayList<String>();
        abilityBinds = new HashMap<String, String>();

        coolDowns = new ArrayList<String>();

        neededXpForLvl = (level +
                (attributes.get("vitality") * 4) +
                (attributes.get("strength") * 4) +
                (attributes.get("endurance") * 4) +
                (attributes.get("dexterity") * 4) +
                (attributes.get("defense") * 4) +
                (attributes.get("intelligence") *4) +
                (attributes.get("faith") * 4) +
                (attributes.get("luck") + (115 * level)) - (attributes.get("wisdom") * 2));
    }

    /**
     * Increases class level by one, resets experience,
     * and allows players to distribute skill points.
     * @param newLevel Sets the `canLevelUp` boolean to this value,
     * 					 and determines whether or not to reset experience and
     * 					 give the player skill points.
     * @param player The player sender object within the BukkitAPI that is receiving the level up.
     */
    public void levelUp(boolean newLevel, Player player) {
        // Enables the player to distribute skill points.
        canLevelUp = newLevel;

        if (newLevel == true) {
            // Resets the experience for the new level.
            experience = 0.0;

            // Gives points that the player can distributes.
            skillPoints = 3.0;

            // Adds one level to the current class.
            level += 1;

            player.sendMessage("You have leveled up! You can now advance your skills.");
        }
    }

	/*
	 * TODO: Create method to check for certain level and skill conditions.
	 * If those requirements are met, allow players to specialize in different classes.
	 */

    /**
     * Checks if the current player sender object can level up.
     * This is generally invoked after the player receives experience.
     * @param player The player sender object within the BukkitAPI that is receiving the level up.
     * @return boolean This is true or false, dependent on whether or not the player can level up.
     */
    public boolean checkLevel(Player player) {
        if ((experience >= neededXpForLvl) && (canLevelUp == false)) {
            levelUp(true, player);
            if (canLevelUp == true) {
                return true;
            }
        }
        return false;
    }

    /**
     * Adds a fraction of a skill point to a selected base skill in the EnumMap.
     * @param attr - Desired attribute to level.
     * @param amount - Desired amount of skill points to distribute to the selected skill.
     */
    public void levelSkill(String attr, double amount) {
        attributes.put(attr, attributes.get(attr) + (amount / 3));
        skillPoints -= amount;
        if (skillPoints <= 0) {
            canLevelUp = false;
        }
    }

    /**
     * Applies certain skills to in-game player attributes.
     * Other skills are applied during actions, while these are
     * somewhat passive skills.
     * @param player The player sender object within the BukkitAPI that this class is bound to.
     */
    public void applyAttributes(Player player) {
        applyVitality(player);
        applyDexterity(player);
        applyStrength(player);
        applyAthleticism(player);
        applyArmorer(player);
    }

    /**
     * Increases the player's max health.
     * @param player The player sender object within the BukkitAPI that this class is bound to.
     */
    public void applyVitality(Player player) {
        double health = 18.0;
        player.setHealthScale(health + (attributes.get("vitality") / 3));
    }

    /**
     * Modifies the player's swing speed.
     * @param player The player sender object within the BukkitAPI that this class is bound to.
     */
    public void applyDexterity(Player player) {
        double attackSpeed = 4.0;
        double speedModifier = attributes.get("dexterity") / 3;
        player.getAttribute(Attribute.GENERIC_ATTACK_SPEED).setBaseValue(attackSpeed + speedModifier);
    }

    /**
     * Increases the player's damage.
     * @param player The player sender object within the BukkitAPI that this class is bound to.
     */
    public void applyStrength(Player player) {
        double attackDamage = 2.0;
        double damageModifier = attributes.get("strength") / 3;
        player.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE).setBaseValue(attackDamage + damageModifier);
    }

    /**
     * Called when the player receives damage. It adds a small health buff.
     * @param player The player sender object within the BukkitAPI that this class is bound to.
     */
    public void applyDefense(Player player) {
        double currentHealth = player.getHealth();
        double healthModifier = attributes.get("defense") / 2;
        player.setHealth(currentHealth + healthModifier);
    }

    /**
     * Increases player walk speed.
     * @param player The player sender object within the BukkitAPI that this class is bound to.
     */
    public void applyAthleticism(Player player) {
        if (athleticism >= 3) {
            double walkSpeed = 0.5;
            double walkModifier = athleticism / 3;
            player.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).setBaseValue(walkSpeed + walkModifier);
        }
    }

    /**
     * Increases armor protection. TODO: move this ability to the defense skill.
     * @param player The player sender object within the BukkitAPI that this class is bound to.
     */
    public void applyArmorer(Player player) {
        if (armorer >= 3) {
            double armorModifier = armorer / 3;
            player.getAttribute(Attribute.GENERIC_ARMOR).setBaseValue(armorModifier);
        }
    }

    /**
     * Applies extra mana to the character if they're a certain in-game race.
     * @param race The player's in-game race.
     */
    public void applyRacialManaBuff(String race) {
        if (race.equalsIgnoreCase("human")) {
            manaPoints += 5;
        }else if (race.equalsIgnoreCase("elf")) {
            manaPoints += 10;
        }else if (race.equalsIgnoreCase("undead")) {
            manaPoints += 5;
        }
    }

    /**
     * Applies certain passive and active abilities to a player
     * depending on their in-game race.
     * @param race The player's in-game race.
     */
    public void applyAbilities(String race) {
		/*
		 * TODO: Based on certain races, give the player certain
		 * passive and active abilities, and save them to the `abilityBinds` HashMap.
		 */
    }
}
