package me.autobot.playerdoll.Command.SubCommand.actions;

import me.autobot.playerdoll.Command.SubCommandHandler;
import me.autobot.playerdoll.Dolls.DollManager;
import net.minecraft.core.Direction;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class look implements SubCommandHandler {

    private final String[] directions = new String[]{"north", "east", "south", "west", "up", "down"};
    @Override
    public void perform(Player player, String dollName, String[] args) {
        String[] _args = Arrays.copyOf(args,3);

        DollManager doll = SubCommandHandler.checkDoll(player, dollName);
        if (doll == null) {return;}

        String arg1 = _args[1] == null ? "0.0" : _args[1];
        String arg2 = _args[2] == null ? "0.0" : _args[2];

        if (Arrays.stream(directions).anyMatch(d -> d.equalsIgnoreCase(arg1))) {
            doll.getActionPack().look(Direction.byName(arg1.toLowerCase()));
        } else if (arg1.matches("[-+]?[0-9]*\\.?[0-9]+") && arg2.matches("[-+]?[0-9]*\\.?[0-9]+")) {
            doll.getActionPack().look(Float.parseFloat(arg1),Float.parseFloat(arg2));
        } else if (Bukkit.getPlayer(arg1) != null) {
            doll.getActionPack().look(doll.server.getPlayerList().getPlayerByName(arg1.toLowerCase()).getYRot(),doll.server.getPlayerList().getPlayerByName(arg1.toLowerCase()).getXRot());
        }
    }

    @Override
    public List<List<String>> commandList() {
        List<String> list = new ArrayList<>();
        list.add("0 0");
        list.add("0.0 0.0");
        list.addAll(List.of(directions));
        list.addAll(Bukkit.getOnlinePlayers().stream().map(Player::getName).toList());
        return List.of(list);
    }

}
