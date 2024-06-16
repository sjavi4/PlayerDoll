package me.autobot.playerdoll.gui;

import me.autobot.playerdoll.config.FlagConfig;
import me.autobot.playerdoll.doll.Doll;
import me.autobot.playerdoll.doll.config.DollConfig;
import me.autobot.playerdoll.gui.menu.*;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import java.util.*;

public class DollGUIHolder {
    public static final Map<UUID, DollGUIHolder> DOLL_GUI_HOLDERS = new HashMap<>();

    public static DollGUIHolder getGUIHolder(Doll doll) {
        return new DollGUIHolder(doll);
    }
    public final Map<MenuType, Menu> menus = new EnumMap<>(MenuType.class);
    public final Map<OfflinePlayer, Menu> psetMenus = new HashMap<>();
    private final Doll doll;
    private final DollConfig dollConfig;
    private DollGUIHolder(Doll doll) {
        this.doll = doll;
        DOLL_GUI_HOLDERS.put(doll.getBukkitPlayer().getUniqueId(), this);
        menus.put(MenuType.BACKPACK, new DollBackpackMenu(doll));
        menus.put(MenuType.INFO, new DollInfoMenu(doll));
        menus.put(MenuType.DATA, new DollDataMenu(doll));
        menus.put(MenuType.GSETTING, new DollGSetMenu(doll));
        menus.put(MenuType.SETTING, new DollSetMenu(doll));

        dollConfig = DollConfig.getOnlineDollConfig(doll.getBukkitPlayer().getUniqueId());
        dollConfig.playerSetting.keySet().forEach(playerUUID -> {
            OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(playerUUID);
            psetMenus.put(offlinePlayer, new DollPSetMenu(doll, offlinePlayer));
        });
    }

    public DollPSetMenu getPSetMenu(OfflinePlayer offlinePlayer) {
        DollPSetMenu menu = (DollPSetMenu) psetMenus.get(offlinePlayer);
        if (menu == null) {
            // Setup all default value for new player
            EnumMap<FlagConfig.PersonalFlagType, Boolean> enumMap = new EnumMap<>(FlagConfig.PersonalFlagType.class);
            Arrays.stream(FlagConfig.PersonalFlagType.values())
                            .forEach(personalFlagType -> enumMap.put(personalFlagType, false));
            dollConfig.playerSetting.put(offlinePlayer.getUniqueId(), enumMap);
            DollPSetMenu pSetMenu = new DollPSetMenu(doll, offlinePlayer);
            psetMenus.put(offlinePlayer, pSetMenu);
            return pSetMenu;
        }
        return menu;
    }


    public enum MenuType {
        BACKPACK, INFO, DATA, SETTING, PSETTING, GSETTING;
    }
}
