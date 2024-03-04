package me.autobot.playerdoll.GUIs.Doll;

import me.autobot.playerdoll.GUIs.Menus.*;
import me.autobot.playerdoll.PlayerDoll;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DollInvStorage {
    private final String dollName;
    private final Inventory infoPage;
    private final Inventory settingPage;
    private final Inventory inventoryPage;
    private final Inventory backpackInventory;
    private final Inventory enderchestInventory;
    private final Inventory GSetPage;
    private final Map<OfflinePlayer,Inventory> pSetPages = new HashMap<>();
    public static DollInvStorage offlineInstance(String dollName) {
        return new DollInvStorage(dollName);
    }
    private DollInvStorage(String doll) {
        this.dollName = doll;
        this.infoPage = null;
        this.settingPage = new SettingPage(doll).getInventory();
        this.inventoryPage = null;
        this.backpackInventory = null;
        this.enderchestInventory = null;
        this.GSetPage = new PlayerSettingPage(dollName, null).getInventory();
        PlayerDoll.dollInvStorage.put(doll, this);
    }
    public DollInvStorage(Player doll) {
        this.dollName = doll.getName();
        this.infoPage = new InformationPage(doll).getInventory();
        this.settingPage = new SettingPage(dollName).getInventory();
        this.inventoryPage = new InventoryPage(doll).getInventory();
        this.backpackInventory = new BackpackInventory(doll).getInventory();
        this.enderchestInventory = new EnderChestInventory(doll).getInventory();
        this.GSetPage = new GeneralSettingPage(dollName).getInventory();
        PlayerDoll.dollInvStorage.put(doll.getName(), this);
    }

    public Inventory getInfoPage() {
        return this.infoPage;
    }
    public Inventory getSettingPage() {
        return this.settingPage;
    }
    public Inventory getInventoryPage() {
        return this.inventoryPage;
    }
    public Inventory getBackpackInventory() {
        return this.backpackInventory;
    }
    public Inventory getEnderchestInventory() {
        return this.enderchestInventory;
    }
    public Inventory getGSetPage() {
        return this.GSetPage;
    }
    public Inventory getPSetPage(OfflinePlayer offlinePlayer) {
        if (this.pSetPages.containsKey(offlinePlayer)) {
            return this.pSetPages.get(offlinePlayer);
        } else {
            Inventory i = new PlayerSettingPage(dollName,offlinePlayer.getName()).getInventory();
            this.pSetPages.put(offlinePlayer,i);
            return i;
        }
    }
    public void closeAllInv() {
        closeInv(infoPage);
        closeInv(settingPage);
        closeInv(inventoryPage);
        closeInv(backpackInventory);
        closeInv(enderchestInventory);
        closeInv(GSetPage);
        pSetPages.values().forEach(this::closeInv);
        pSetPages.clear();
    }

    public void closeOfflineInv() {
        closeInv(settingPage);
        closeInv(GSetPage);
        pSetPages.values().forEach(this::closeInv);
        pSetPages.clear();
    }
    private void closeInv(Inventory inv) {
        if (inv == null) {
            return;
        }
        List<HumanEntity> viewer = List.copyOf(inv.getViewers());
        if (viewer == null) return;
        for (HumanEntity h : viewer) {
            h.closeInventory();
        }
    }
}
