package io.github.CharacterCraft;

import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarFlag;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;

import java.util.List;

public class MenuUI {

    public MenuUI() {
        Bukkit.createBossBar(
              "Statistics",
               BarColor.BLUE,
                BarStyle.SOLID,
                null
        );
    }

}
