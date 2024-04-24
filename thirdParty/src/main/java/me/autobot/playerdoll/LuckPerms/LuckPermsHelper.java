package me.autobot.playerdoll.LuckPerms;

import net.luckperms.api.LuckPerms;
import net.luckperms.api.model.user.User;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;


public class LuckPermsHelper {
    private LuckPerms api = null;
    public LuckPermsHelper() {
        RegisteredServiceProvider<LuckPerms> luckPermsAPI = Bukkit.getServicesManager().getRegistration(LuckPerms.class);
        if (luckPermsAPI != null) {
            api = luckPermsAPI.getProvider();
            Bukkit.getLogger().log(Level.INFO,"[PlayerDoll] LuckPerms is present.");
            //System.out.println("LuckPerms is present.");
        } else {
            Bukkit.getLogger().log(Level.INFO,"[PlayerDoll] LuckPerms not Found.");
            //System.out.println("LuckPerms not Found.");
        }
    }

    public String getPlayerGroupName(Player player) {
        return api.getPlayerAdapter(Player.class).getUser(player).getPrimaryGroup();
    }
    public String getPlayerGroupName(UUID uuid) {
        User user = api.getUserManager().getUser(uuid);
        return user == null ? null : user.getPrimaryGroup();
    }
    public String getOfflinePlayerGroupName(UUID uuid) {
        CompletableFuture<User> userFuture = api.getUserManager().loadUser(uuid);
        try {
            return userFuture.get(1, TimeUnit.SECONDS).getPrimaryGroup();
        } catch (ExecutionException | TimeoutException | InterruptedException e) {
            return null;
        }
    }
/*
    public boolean playerBelongToPermissionGroup(Player player, String groupName) {

        return player.hasPermission("group." + group);
    }

 */

}
