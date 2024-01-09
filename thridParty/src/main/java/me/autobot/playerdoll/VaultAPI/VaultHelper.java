package me.autobot.playerdoll.VaultAPI;

import me.autobot.playerdoll.Util.LangFormatter;
import me.autobot.playerdoll.Util.PermissionManager;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import me.autobot.playerdoll.Util.Pair;

import static org.bukkit.Bukkit.getServer;

public class VaultHelper {
    private Economy econ = null;
    public VaultHelper() {
        if (!setupEconomy() ) {
            System.out.println("Vault Not Found, related methods will not be executed.");
        } else {
            System.out.println("Vault Founded.");
        }
    }
    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        econ = rsp.getProvider();
        return econ != null;
    }

    public boolean dollCreation(Player player) {
        if (this.econ == null) {
            //player.sendMessage(LangFormatter.YAMLReplaceMessage("VaultNotFound"));
            return true;
        }
        return withdraw(player, PermissionManager.getInstance(player).costPerCreation);
    }

    private boolean withdraw(Player player, double cost) {
        EconomyResponse response = econ.withdrawPlayer(player, cost);
        if (response.transactionSuccess()) {
            player.sendMessage(LangFormatter.YAMLReplaceMessage("TradeSuccess",new Pair<>("%a%",Double.toString(response.balance))));
            return true;
        } else {
            player.sendMessage(LangFormatter.YAMLReplaceMessage("NotEnoughBalance",new Pair<>("%a%",Double.toString(response.amount))));
            return false;
        }
    }
}
