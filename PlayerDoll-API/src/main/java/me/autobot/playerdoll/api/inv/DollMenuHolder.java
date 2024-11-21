package me.autobot.playerdoll.api.inv;

import me.autobot.playerdoll.api.doll.Doll;
import me.autobot.playerdoll.api.doll.DollConfig;
import me.autobot.playerdoll.api.doll.DollSetting;
import me.autobot.playerdoll.api.inv.button.PersonalFlagButton;
import me.autobot.playerdoll.api.inv.gui.*;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.HumanEntity;
import org.bukkit.inventory.Inventory;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class DollMenuHolder {

    public static final Map<UUID, DollMenuHolder> HOLDERS = new ConcurrentHashMap<>();
    public final Map<Class<? extends AbstractMenu>, List<AbstractMenu>> inventoryStorage = new HashMap<>();

    public final Map<OfflinePlayer, List<DollPSetMenu>> pSetStorage = new HashMap<>();

    private final Doll doll;
    public final UUID dollUUID;



    public DollMenuHolder(Doll doll) {
        this.doll = doll;
        this.dollUUID = doll.getBukkitPlayer().getUniqueId();
        HOLDERS.put(dollUUID, this);
        int settingPages = (int) Math.ceil((double) DollSetting.SETTINGS.size() /36);
        int gSetPages = (int) Math.ceil((double) PersonalFlagButton.getButtons().values().stream().filter(b -> b instanceof PersonalFlagButton).count() /36);

        inventoryStorage.put(DollBackpackMenu.class, List.of(new DollBackpackMenu(doll, this)));
        inventoryStorage.put(DollDataMenu.class, List.of(new DollDataMenu(doll, this)));
        inventoryStorage.put(DollInfoMenu.class, List.of(new DollInfoMenu(doll, this)));

        List<AbstractMenu> dollSetMenus = new ArrayList<>();
        inventoryStorage.put(DollSetMenu.class, dollSetMenus);
        for (int i = 0; i < settingPages; i++) {
            dollSetMenus.add(new DollSetMenu(doll, this, i));
        }
        List<AbstractMenu> dollGSetMenus = new ArrayList<>();
        inventoryStorage.put(DollGSetMenu.class, dollGSetMenus);
        for (int i = 0; i < gSetPages; i++) {
            dollGSetMenus.add(new DollGSetMenu(doll, this, i));
        }

    }

    public DollPSetMenu getPSetMenu(OfflinePlayer offlinePlayer) {
        List<DollPSetMenu> menus = pSetStorage.get(offlinePlayer);
        if (menus == null || menus.isEmpty()) {
            List<PersonalFlagButton> buttons = PersonalFlagButton.getButtons().values().stream().filter(b -> b instanceof PersonalFlagButton).map(b -> (PersonalFlagButton)b).toList();
            int pSetPages = (int) Math.ceil((double) buttons.size() /36);
            // Setup all default value for new player


            LinkedHashMap<PersonalFlagButton, Boolean> defaultMap = new LinkedHashMap<>();
            buttons.forEach(personalFlagType -> defaultMap.put(personalFlagType, false));
            DollConfig.getOnlineConfig(dollUUID).playerSetting.put(offlinePlayer.getUniqueId(), defaultMap);

            List<DollPSetMenu> dollPSetMenus = new ArrayList<>();
            pSetStorage.put(offlinePlayer, dollPSetMenus);
            for (int i = 0; i < pSetPages; i++) {
                dollPSetMenus.add(new DollPSetMenu(doll, this, offlinePlayer, i));
            }
            return dollPSetMenus.get(0);
        }
        return menus.get(0);
    }


    public void onDisconnect() {
        DollMenuHolder holder = HOLDERS.remove(this.dollUUID);
        if (holder == null) {
            return;
        }
        // Close all viewer's inventory
        holder.inventoryStorage.values().forEach(menus -> menus.forEach(menu -> new ArrayList<>(menu.getInventory().getViewers()).forEach(HumanEntity::closeInventory)));
        holder.pSetStorage.values().forEach(menus -> menus.forEach(menu -> new ArrayList<>(menu.getInventory().getViewers()).forEach(HumanEntity::closeInventory)));
        inventoryStorage.clear();
        pSetStorage.clear();
    }

    public AbstractMenu searchMenu(Inventory inventory) {
        for (List<AbstractMenu> list : inventoryStorage.values()) {
            for (AbstractMenu menu : list) {
                if (menu.getInventory() == inventory) {
                    return menu;
                }
            }
        }
        for (List<DollPSetMenu> list : pSetStorage.values()) {
            for (AbstractMenu menu : list) {
                if (menu.getInventory() == inventory) {
                    return menu;
                }
            }
        }
        return null;
    }
}
