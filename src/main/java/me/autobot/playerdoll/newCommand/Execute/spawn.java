package me.autobot.playerdoll.newCommand.Execute;

import com.mojang.authlib.GameProfile;
import me.autobot.playerdoll.Configs.LangFormatter;
import me.autobot.playerdoll.Configs.YAMLManager;
import me.autobot.playerdoll.Dolls.DollManager;
import me.autobot.playerdoll.PlayerDoll;
import me.autobot.playerdoll.newCommand.SubCommand;
import net.minecraft.core.UUIDUtil;
import net.minecraft.server.level.ServerPlayer;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import oshi.util.tuples.Pair;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.List;


public class spawn extends SubCommand {
    Player player;
    GameProfile profile;
    String dollName;
    boolean inGrid;
    public spawn(CommandSender sender, Object doll, Object args) {
        super(MinPermission.Share,false);
        String dollName = (String) doll;
        player = (Player) sender;
        if (!checkPermission(sender, dollName)) return;
        if (PlayerDoll.dollManagerMap.containsKey(dollName)) {
            player.sendMessage(LangFormatter.YAMLReplace("InUseDollName",'&', new Pair<>("%a%",dollName)));
            return;
        }
        File file = new File(PlayerDoll.getDollDirectory(),dollName+".yml");
        if (!file.exists()) {
            player.sendMessage(LangFormatter.YAMLReplace("DollNotExist",'&'));
            return;
        }
        var config = YAMLManager.loadConfig(file,dollName,false).getConfig();
        String name = PlayerDoll.getDollPrefix() + dollName;
        this.dollName = dollName;

        player = (Player) sender;
        profile = new GameProfile(UUIDUtil.createOfflinePlayerUUID(name), name);
        inGrid = args != null && ((String[]) args)[0].equalsIgnoreCase("inGrid");
        if (config.getBoolean("Remove")) {
            player.sendMessage(LangFormatter.YAMLReplace("PlayerSpawnRemovedDoll",'&'));
            return;
        }
        if (!player.isOp() && Bukkit.getMaxPlayers() <= Bukkit.getOnlinePlayers().size()) {
            player.sendMessage(LangFormatter.YAMLReplace("ServerReachMaxPlayer",'&'));
            return;
        }
        execute();
    }
    @Override
    public void execute() {
        try {
            Class<?> craftPlayerClass = Class.forName("org.bukkit.craftbukkit."+ PlayerDoll.version +".entity.CraftPlayer");
            ServerPlayer serverPlayer = (ServerPlayer) player.getClass().asSubclass(craftPlayerClass).getDeclaredMethod("getHandle").invoke(player);

            DollManager dolls = new DollManager(serverPlayer.server,serverPlayer.serverLevel(),profile,serverPlayer);
            PlayerDoll.dollManagerMap.put(dollName, dolls);
            if (inGrid) {
                var pos = player.getLocation();
                dolls.setPos(Math.round(pos.getX() * 2) / 2.0, pos.getBlockY(), Math.round(pos.getZ() * 2) / 2.0);
            }
        } catch (ClassNotFoundException | InvocationTargetException | IllegalAccessException | NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }
    public static List<Object> tabSuggestion() {
        return List.of("inGrid");
    }
}
