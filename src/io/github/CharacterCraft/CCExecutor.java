package io.github.CharacterCraft;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;
import java.util.Stack;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

/**
 * Executes in-game commands.
 */
public class CCExecutor {
    private final CharacterCraft characterCraft;

    public CCExecutor(CharacterCraft characterCraft) {
        this.characterCraft = characterCraft;
    }

    /**
     * Receives a command from the sender, -- who is generally
     * the in-game player, but sometimes console -- and performs
     * a certain task based on the given command. If the command is
     * valid, return true. Otherwise, return false.
     *
     * @param sender Source of the command.
     * @param cmd    The command that was executed.
     * @param label  Alias of the command used.
     * @param args   Passed command arguments.
     * @return boolean: true if the command is valid. Otherwise, false.
     */
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        final Player player = (Player) sender;

        final ArrayList<String> classList = new ArrayList<String>();
        classList.add("warrior");
        classList.add("druid");
        classList.add("archer");
        classList.add("rogue");

        if (cmd.getName().equalsIgnoreCase("cc")) {
			/*
			 * Check if the command was issued by a player.
			 * This is to better detail what commands should
			 * and should not be issued via console.
			 */
            if (sender instanceof Player) {
                // Initiates player as the sender of the command.


                // Utility commands:
                // Version number:
                if (args.length == 0) {
                    player.sendMessage(ChatColor.DARK_AQUA + "characterCraft" + ChatColor.WHITE + " inDev | v.(1.0.7)");
                    return true;

                    // Help:
                } else if (args[0].equalsIgnoreCase("help") && args.length == 1) {
                    // TODO List commands.
                    return true;

                } else if (args[0].equalsIgnoreCase("help") && args[1].equalsIgnoreCase("stats") && args.length == 3) {
					/*
					 *  TODO: Based on args[2], print a help statement relating to a specific skill.
					 */
                    return true;

                } else if (args[0].equalsIgnoreCase("help") && args[1].equalsIgnoreCase("spells") && args.length == 3) {
					/*
					 *  TODO: Based on args[2], print a help statement relating to a specific spell.
					 */
                    return true;

                } else if (args[0].equalsIgnoreCase("help") && args[1].equalsIgnoreCase("abilities") && args.length == 3) {
					/*
					 *  TODO: Based on args[2], print a help statement relating to a specific ability.
					 */
                    return true;

                } else if (args[0].equalsIgnoreCase("help") && args[1].equalsIgnoreCase("classes") && args.length == 3) {
                    String className = args[2].toLowerCase();
                    if (classList.contains(className)) {
                        // TODO: Write logic to give different info on each class.
                        return true;
                    } else {
                        player.sendMessage(ChatColor.RED + "The class described in your command does not exist.");
                        return false;
                    }

                } else if (args[0].equalsIgnoreCase("help") && args[1].equalsIgnoreCase("races") && args.length == 3) {
					/*
					 *  TODO: Based on args[2], print a help statement relating to a specific race.
					 */
                    return true;

					/*
					 * Admin test command. Issues a high stat class for spell and ability testing.
					 */

                } else if (args[0].equalsIgnoreCase("set") && args[1].equalsIgnoreCase("name") && args.length > 2) {
                    Object userSave = characterCraft.getUserConfig().get(player.getUniqueId() + ".userClass");
                    UserCharacter user = (UserCharacter) userSave;

                    String listName = player.getPlayerListName();
                    user.characterName = ChatColor.translateAlternateColorCodes('&',
                            "[" + "&3" + user.characterRace + "&f]" + " " + "[&6" + user.characterClass + "&f] " + listName + " "
                    );

                    user.characterName = user.characterName.substring(0, user.characterName.length() - (listName.length() + 1));

                    int n = args.length;
                    for (int i = args.length; i > 2; i--) {
                        this.characterCraft.getLogger().info(n + "");
                        n--;
                        user.characterName += args[n] + " ";
                        this.characterCraft.getLogger().info(user.characterName);
                    }

                    player.setDisplayName(user.characterName);

                    // Save player data to the HashMap.
					/*
					characterCraft.characterSave.put(player.getUniqueId(), user);

					characterCraft.saveHashMap(characterCraft.characterSave, new File(characterCraft.getDataFolder(), "user.dat"));
					*/
                    characterCraft.getUserConfig().set(player.getUniqueId().toString() + ".userClass", user);
                    try {
                        characterCraft.getUserConfig().save(new File(characterCraft.getDataFolder(), "user.yml"));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    return true;


                } else if (args[0].equalsIgnoreCase("roll") && args.length == 2) {
                    Random rand = new Random();
                    int num = Integer.parseInt(args[1]);
                    int diceroll = rand.nextInt(num) + 1;

                    sender.sendMessage(player.getDisplayName() + "rolled " + diceroll);
                    return true;

                } else if (args[0].equalsIgnoreCase("spells")) {
                    Object userSave = characterCraft.getUserConfig().get(player.getUniqueId() + ".userClass");
                    UserCharacter user = (UserCharacter) userSave;
                    for (String s : user.spellList) {
                        player.sendMessage(s);
                    }
                    return true;

                    // Player statistics:
                } else if (args[0].equalsIgnoreCase("stats") && args.length == 1) {
                    if (characterCraft.getUserConfig().getKeys(false).contains(player.getUniqueId().toString())) {
                        Object userSave = characterCraft.getUserConfig().get(player.getUniqueId() + ".userClass");
                        UserCharacter user = (UserCharacter) userSave;
                        player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&6Class : &f" + user.characterClass));
                        player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&6Level : &f" + user.level));
                        player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&6Skill Points for Leveling : &f" + user.skillPoints));
                        player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&6XP : &f" + user.experience + " / " + user.neededXpForLvl));
                        player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&6Mana : &f" + user.manaPoints + " / " + user.maxManaPoints));

                        player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&3Base stats:"));
                        player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&6Vitality : &f" + user.attributes.get("vitality")));
                        player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&6Strength : &f" + user.attributes.get("strength")));
                        player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&6Endurance : &f" + user.attributes.get("endurance")));
                        player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&6Dexterity : &f" + user.attributes.get("dexterity")));
                        player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&6Defense : &f" + user.attributes.get("defense")));
                        player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&6Intelligence : &f" + user.attributes.get("intelligence")));
                        player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&6Wisdom : &f" + user.attributes.get("wisdom")));
                        player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&6Faith : &f" + user.attributes.get("faith")));
                        player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&6Luck : &f" + user.attributes.get("luck")));

                        player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&3Sub-skills:"));
                        player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&6Mining : &f" + user.mining));
                        player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&6Woodcutting : &f" + user.woodcutting));
                        player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&6Cooking : &f" + user.cooking));
                        player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&6Fishing : &f" + user.fishing));
                        player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&6Swordsmanship : &f" + user.swordsmanship));
                        player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&6Marksmanship : &f" + user.marksmanship));
                        player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&6Block : &f" + user.block));
                        player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&6Armorer : &f" + user.armorer));
                        player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&6Athleticism : &f" + user.athleticism));
                        player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&6Concentration : &f" + user.concentration));
                        player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&6Enchanting : &f" + user.enchanting));
                        return true;
                    } else {
                        player.sendMessage(ChatColor.RED + "You must create a class first in order to view stats!");
                    }

                    // Character creation commands:
                } else if (args[0].equalsIgnoreCase("set") && args[1].equalsIgnoreCase("character")) {
                    if (args.length == 5) {
                        if (args[4].equalsIgnoreCase("admin")) {
                            String className = args[3].toLowerCase();
                            if (classList.contains(className)) {
                                UserCharacter user = new UserCharacter(args[2], args[3], 5000, 100, 100, 100, 100, 100, 100, 100, 100, 100);
									/*
									if (characterCraft.characterSave.containsKey(player.getUniqueId())) {
										characterCraft.characterSave.remove(player.getUniqueId());
									}
									*/
                                // Retrieve listed MineCraft name.
                                String oldName = player.getPlayerListName();

                                // Assign custom name to show race and class.
                                String newName = ChatColor.translateAlternateColorCodes('&',
                                        "[" + "&3" + args[2].substring(0, 1).toUpperCase() + args[2].substring(1) +
                                                "&f]" + " " + "[&6" + args[3].substring(0, 1).toUpperCase() + args[3].substring(1) + "&f] " + oldName
                                );

                                // Set in-game MineCraft name to the name described above.
                                player.setDisplayName(newName);

                                // Set the User instance's name to match display name.
                                user.characterName = newName;

                                user.level = 100;
                                user.spellList.add("blink");
                                user.spellList.add("smallfireball");
                                user.spellList.add("largefireball");
                                user.spellList.add("dragonfireball");
                                user.spellList.add("lightning");
                                user.spellList.add("fly");
                                user.abilityList.add("heartofgod");
                                user.abilityList.add("heardprayer");
                                user.abilityList.add("berserk");
                                user.abilityList.add("focus");
                                user.abilityList.add("observation");
                                user.abilityList.add("invisibility");

                                // Send confirmation message to the player.
                                player.sendMessage(ChatColor.translateAlternateColorCodes('&',
                                        "&fThe realms have agreed to give you an admin class titled &6" +
                                                user.characterClass + "&f."));

                                characterCraft.getUserConfig().set(player.getUniqueId().toString() + ".userClass", user);
                                try {
                                    characterCraft.getUserConfig().save(new File(characterCraft.getDataFolder(), "user.yml"));
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }

									/*
									characterCraft.characterSave.put(player.getUniqueId(), user);

									// Save player data to the HashMap.
									characterCraft.saveHashMap(characterCraft.characterSave, new File(characterCraft.getDataFolder(), "user.dat"));
									*/
                                return true;
                            } else {
                                player.sendMessage(ChatColor.RED + "Incorrect class name.");
                                return false;
                            }
                        }
                    } else {

                        // Determine race passives:
                        //TODO: Write function to assign race passives to variables and return them.

                        // Create character object.
							/*
							 * TODO: Add the mana buff, returned from function mentioned above,
							 * to last argument passed to the instance of User.
							 */
							/*
							if (characterCraft.characterSave.containsKey(player.getUniqueId())) {
								characterCraft.characterSave.remove(player.getUniqueId());
							}
							*/

                        String className = args[3].toLowerCase();
                        if (classList.contains(className)) {
                            UserCharacter user = new UserCharacter(args[2], args[3], 5, 0, 0, 0, 0, 0, 0, 0, 0, 0);


                            // Retrieve listed MineCraft name.
                            String oldName = player.getPlayerListName();

                            // Assign custom name to show race and class.
                            String newName = ChatColor.translateAlternateColorCodes('&',
                                    "[" + "&3" + args[2].substring(0, 1).toUpperCase() + args[2].substring(1) +
                                            "&f]" + " " + "[&6" + args[3].substring(0, 1).toUpperCase() + args[3].substring(1) + "&f] " + oldName
                            );

                            // Set in-game MineCraft name to the name described above.
                            player.setDisplayName(newName);

                            // Set the User instance's name to match display name.
                            user.characterName = newName;

                            // Add a mana buff dependent on the player's race.
                            user.applyRacialManaBuff(user.characterRace);

                            // Add certain spells and abilities depending on class.
                            if (user.characterClass.equalsIgnoreCase("mage")) {
                                user.spellList.add("blink");
                            } else if (user.characterClass.equalsIgnoreCase("warrior")) {
                                user.abilityList.add("berserk");
                            } else if (user.characterClass.equalsIgnoreCase("archer")) {
                                user.abilityList.add("focus");
                            } else if (user.characterClass.equalsIgnoreCase("rogue")) {
                                user.abilityList.add("invisibility");
                            }

                            // TODO: Add passive abilities based on race.

                            // Send confirmation message to the player.
                            player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&fThe realms have agreed to title you &6" + user.characterClass + "&f."));
							/*
							characterCraft.characterSave.put(player.getUniqueId(), user);

							// Save player data to the HashMap.
							characterCraft.saveHashMap(characterCraft.characterSave, new File(characterCraft.getDataFolder(), "user.dat"));
							*/
                            characterCraft.getUserConfig().set(player.getUniqueId().toString() + ".userClass", user);
                            try {
                                characterCraft.getUserConfig().save(new File(characterCraft.getDataFolder(), "user.yml"));
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            return true;
                        }
                    }

                    // Leveling commands:
                } else if (args[0].equalsIgnoreCase("level") && args.length == 3) {
                    Object userSave = characterCraft.getUserConfig().get(player.getUniqueId() + ".userClass");
                    UserCharacter user = (UserCharacter) userSave;
                    if (user.canLevelUp == true) {
                        if (user.skillPoints > 0) {
                            double amt = Double.parseDouble(args[2]);
                            user.levelSkill(args[1].toLowerCase(), amt);
                            player.sendMessage(ChatColor.YELLOW + "Points have been distributed and skills updated. ");
                        }
                        return true;
                    } else {
                        player.sendMessage(ChatColor.RED + "You cannot level up at this time.");
                    }
                    // Spit command:

				/*
				 * Unable to launch this projectile due to a bug in the new Spigot build.
				 *
				 * }else if (args[0].equalsIgnoreCase("spit") && args.length == 1) {
				 *     player.launchProjectile(LlamaSpit.class);
				 */

                    // Spell and ability cast commands:
                    // Spell casting.
                } else if (args[0].equalsIgnoreCase("cast") && args[1].equalsIgnoreCase("spell") && args.length >= 3) {
                    Object userSave = characterCraft.getUserConfig().get(player.getUniqueId() + ".userClass");
                    UserCharacter user = (UserCharacter) userSave;

                    if (characterCraft.getUserConfig().getLong(player.getUniqueId().toString() + ".spells.cooldowns." + args[2]) > player.getPlayerTime()) {
                        long coolDown = characterCraft.getUserConfig().getLong(player.getUniqueId().toString() + ".spells.cooldowns." + args[2]) - player.getPlayerTime();
                        player.sendMessage(ChatColor.RED + "You must wait " + coolDown + " more seconds until you can cast this again.");
                        return false;
                    }

                    // Check if the player can cast this spell.
                    if (user.spellList.contains(args[2])) {
                        Spell spell = new Spell(characterCraft);
                        int time = (int) player.getPlayerTime();

                        int spellCoolDown = 0;
                        if (args[2].equalsIgnoreCase("blink")) {
                            spellCoolDown = 100;
                        } else if (args[2].equalsIgnoreCase("blink"))

                            player.sendMessage(time + "");
                        characterCraft.getUserConfig().set(player.getUniqueId().toString() + ".spells.cooldowns." + args[2], time + 10);
                        user.coolDowns.add(args[2]);
                        try {
                            characterCraft.getUserConfig().save(new File(characterCraft.getDataFolder(), "user.yml"));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        spell.castSpell(args[2], 25.0, 3.0, player, user);
						/*
						}else if (args[2].equalsIgnoreCase("fly")) {
								spell.castSpell(characterCraft.flyCoolDownTime, characterCraft.flyCoolDownTask, args[2], 35.0, 360.0, 5, player, user,
										true, 5.0);
								try {
									characterCraft.flyCoolDownTask.get(player.getUniqueId()).runTaskTimer(this.characterCraft, 20, 20);
								}catch (IllegalStateException e) {
									// Prevents error flooding and stops the restart of the cool down timer.
								}

						}else if (args[2].equalsIgnoreCase("smallfireball")) {
							spell.castSpell(characterCraft.smFBallCoolDownTime, characterCraft.smFBallCoolDownTask, args[2],
									50.0, 120.0, 10, player, user, false, 0.0);
							try {
								characterCraft.smFBallCoolDownTask.get(player.getUniqueId()).runTaskTimer(this.characterCraft, 20, 20);
							}catch (IllegalStateException e) {
								// Prevents error flooding and stops the restart of the cool down timer.
							}

						}else if (args[2].equalsIgnoreCase("largefireball")) {
							spell.castSpell(characterCraft.lrgFBallCoolDownTime, characterCraft.lrgFBallCoolDownTask, args[2],
									100.0, 270.0, 25, player, user, false, 0.0);
							try {
								characterCraft.lrgFBallCoolDownTask.get(player.getUniqueId()).runTaskTimer(this.characterCraft, 20, 20);
							}catch (IllegalStateException e) {
								// Prevents error flooding and stops the restart of the cool down timer.
							}

						}else if (args[2].equalsIgnoreCase("dragonfireball")) {
							spell.castSpell(characterCraft.dragFBallCoolDownTime, characterCraft.dragFBallCoolDownTask, args[2],
									150.0, 570.0, 50, player, user, false, 0.0);
							try {
								characterCraft.dragFBallCoolDownTask.get(player.getUniqueId()).runTaskTimer(this.characterCraft, 20, 20);
							}catch (IllegalStateException e) {
								// Prevents error flooding and stops the restart of the cool down timer.
							}
						}else if (args[2].equalsIgnoreCase("lightning")) {
							spell.castSpell(characterCraft.lightningCoolDownTime, characterCraft.lightningCoolDownTask, args[2],
									150.0, 570.0, 35, player, user, false, 0.0);
							try {
								characterCraft.lightningCoolDownTask.get(player.getUniqueId()).runTaskTimer(this.characterCraft, 20, 20);
							}catch (IllegalStateException e) {
								// Prevents error flooding and stops the restart of the cool down timer.
							}*/
                    }

                } else {
                    player.sendMessage(ChatColor.RED + "You have not learned this spell!");
                }

            } else if (args[0].equalsIgnoreCase("bind") && args.length == 3) {
                Object userSave = characterCraft.getUserConfig().get(player.getUniqueId() + ".userClass");
                UserCharacter user = (UserCharacter) userSave;
                if (user.spellList.contains(args[2])) {
                    ItemStack item = player.getInventory().getItemInMainHand();

                    String itemType = item.getType().toString();

                    if (itemType == "GOLD_HOE") {
                        ItemMeta itemMeta = item.getItemMeta();
                        itemMeta.setDisplayName("Golden Staff");
                        item.setItemMeta(itemMeta);

                        user.spellBinds.put(args[1], args[2]);

							/*
							characterCraft.characterSave.put(player.getUniqueId(), user);

							characterCraft.saveHashMap(characterCraft.characterSave, new File(characterCraft.getDataFolder(), "user.dat"));
							*/
                        characterCraft.getUserConfig().set(player.getUniqueId().toString() + ".userClass", user);
                        try {
                            characterCraft.getUserConfig().save(new File(characterCraft.getDataFolder(), "user.yml"));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        player.sendMessage("The spell " + args[2] + " has been bound to your " + args[1] + " click.");
                    }
                }

                // Ability casting
            } else if (args[0].equalsIgnoreCase("ability") && args[1].equalsIgnoreCase("cast") && args.length == 3) {
                Object userSave = characterCraft.getUserConfig().get(player.getUniqueId() + ".userClass");
                UserCharacter user = (UserCharacter) userSave;
                // Check if the player can cast this ability.
                if (user.abilityList.contains(args[2])) {

						/*
						if (args[2].equalsIgnoreCase("heartofgod")) {
							ability.castAbility(characterCraft.heartOfGodCoolDownTime, characterCraft.heartOfGodCoolDownTask, args[2],
									30.0, 400.0, 2, player, user, false, 0.0, characterCraft.characterSave);
							try {

								characterCraft.heartOfGodCoolDownTask.get(player).runTaskTimer(this.characterCraft, 20, 20);
							}catch (IllegalStateException e) {
								// Prevents error flooding and stops the restart of the cool down timer.
							}

						}else if (args[2].equalsIgnoreCase("heardPrayer")) {
							ability.castAbility(characterCraft.heardPrayerCoolDownTime, characterCraft.heardPrayerCoolDownTask, args[2],
									25.0, 300.0, 2, player, user, false, 0.0, characterCraft.characterSave);
							try {
								characterCraft.heardPrayerCoolDownTask.get(player).runTaskTimer(this.characterCraft, 20, 20);
							}catch (IllegalStateException e) {
								// Prevents error flooding and stops the restart of the cool down timer.
							}

						}else if (args[2].equalsIgnoreCase("berserk")) {
							ability.castAbility(characterCraft.berserkCoolDownTime, characterCraft.berserkCoolDownTask, args[2],
									25.0, 300.0, 2, player, user, false, 0.0, characterCraft.characterSave);
							try {
								characterCraft.berserkCoolDownTask.get(player).runTaskTimer(this.characterCraft, 20, 20);
							}catch (IllegalStateException e) {
								// Prevents error flooding and stops the restart of the cool down timer.
							}

						}else if (args[2].equalsIgnoreCase("focus")) {
							ability.castAbility(characterCraft.focusCoolDownTime, characterCraft.focusCoolDownTask, args[2],
									25.0, 300.0, 2, player, user, false, 0.0, characterCraft.characterSave);
							try {
								characterCraft.focusCoolDownTask.get(player).runTaskTimer(this.characterCraft, 20, 20);
							}catch (IllegalStateException e) {
								// Prevents error flooding and stops the restart of the cool down timer.
							}

						}else if (args[2].equalsIgnoreCase("observation")) {
							ability.castAbility(characterCraft.observationCoolDownTime, characterCraft.observationCoolDownTask, args[2],
									25.0, 300.0, 2, player, user, false, 0.0, characterCraft.characterSave);
							try {
								characterCraft.observationCoolDownTask.get(player).runTaskTimer(this.characterCraft, 20, 20);
							}catch (IllegalStateException e) {
								// Prevents error flooding and stops the restart of the cool down timer.
							}
						}else if (args[2].equalsIgnoreCase("invisibility")) {
							ability.castAbility(characterCraft.invisibilityCoolDownTime, characterCraft.invisibilityCoolDownTask, args[2],
									25.0, 300.0, 2, player, user, true, 10.0, characterCraft.characterSave);
							try {
								characterCraft.invisibilityCoolDownTask.get(player).runTaskTimer(this.characterCraft, 20, 20);
							}catch (IllegalStateException e) {
								// Prevents error flooding and stops the restart of the cool down timer.
							}
						}*/

                } else {
                    player.sendMessage(ChatColor.RED + "You have not learned this ability!");
                    return false;
                }
            } else {
                player.sendMessage(ChatColor.RED + "The realms are unfamiliar with this command.");
                return false;
            }
        }
        return false;
    }
}


