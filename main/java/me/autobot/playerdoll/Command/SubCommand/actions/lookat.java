package me.autobot.playerdoll.Command.SubCommand.actions;

import me.autobot.playerdoll.Command.SubCommandHandler;
import me.autobot.playerdoll.Dolls.DollManager;
import net.minecraft.world.phys.Vec3;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class lookat implements SubCommandHandler {

    @Override
    public void perform(Player player, String dollName, String[] args) {
        String[] _args = Arrays.copyOf(args,4);

        DollManager doll = SubCommandHandler.checkDoll(player, dollName);
        if (doll == null) {return;}

        String arg1 = _args[1] == null ? "0.0" : _args[1];
        String arg2 = _args[2] == null ? "0.0" : _args[2];
        String arg3 = _args[3] == null ? "0.0" : _args[3];

        if (arg1.matches("[-+]?[0-9]*\\.?[0-9]+") && arg2.matches("[-+]?[0-9]*\\.?[0-9]+") && arg3.matches("[-+]?[0-9]*\\.?[0-9]+")) {
            doll.getActionPack().lookAt(new Vec3(Float.parseFloat(arg1), Float.parseFloat(arg2), Float.parseFloat(arg3)));
        } else if (Bukkit.getPlayer(arg1) != null) {
            doll.getActionPack().lookAt(doll.server.getPlayerList().getPlayerByName(arg1.toLowerCase()).getEyePosition());
        }
    }

    @Override
    public List<List<String>> commandList() {
        List<String> list = new ArrayList<>();
        list.add("0 0 0");
        list.add("0.0 0.0 0.0");
        list.addAll(Bukkit.getOnlinePlayers().stream().map(Player::getName).toList());

        return List.of(list);
    }
}
