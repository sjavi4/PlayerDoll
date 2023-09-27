package me.autobot.playerdoll.Command;

import me.autobot.playerdoll.Command.SubCommand.actions.*;
import me.autobot.playerdoll.Command.SubCommand.operations.*;
import me.autobot.playerdoll.Command.SubCommand.utils.helps;
import me.autobot.playerdoll.Command.SubCommand.utils.list;
import me.autobot.playerdoll.Command.SubCommand.utils.reload;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;


public class CommandManager implements CommandExecutor {

    public static Map<String,SubCommandHandler> subCommandMap = new HashMap<>();
    public static Map<String,SubCommandHandler> directCommandMap = new HashMap<>();


    public CommandManager() {
        subCommandMap.put("stop",new stop());
        subCommandMap.put("dismount",new dismount());
        subCommandMap.put("use", new use());
        subCommandMap.put("attack", new attack());
        subCommandMap.put("tp", new tp());
        subCommandMap.put("slot", new slot());
        subCommandMap.put("copy", new copy());
        subCommandMap.put("mount", new mount());
        subCommandMap.put("move", new move());
        subCommandMap.put("sneak", new sneak());
        subCommandMap.put("turn", new turn());
        subCommandMap.put("look", new look());
        subCommandMap.put("lookat", new lookat());
        subCommandMap.put("drop", new drop());
        subCommandMap.put("swap", new swap());
        //subCommandMap.put("sprint", new sprint());
        //subCommandMap.put("strafe", new strafe());
        subCommandMap.put("jump", new jump());

        subCommandMap.put("remove",new remove());
        subCommandMap.put("set", new set());
        subCommandMap.put("despawn",new despawn());
        subCommandMap.put("share", new share());
        subCommandMap.put("unshare", new unshare());
        subCommandMap.put("spawn",new spawn());

        //directCommandMap.put("limits", new limits());
        directCommandMap.put("reload", new reload());
        directCommandMap.put("helps", new helps());
        //directCommandMap.put("flags", new flags());
        directCommandMap.put("list", new list());
    }


    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {


        //command.tabComplete(sender, "doll", (String[]) PlayerDoll.dollManagerMap.keySet().toArray());

        if (!(sender instanceof Player player)) {
            System.out.println("Must perform the command by a player");
            return true;
        }

        if (args == null || args.length == 0) {
            new helps().perform(player, null,null);
            return true;
        }

        switch (args.length) {
            case 1 -> {
                if (!directCommandMap.containsKey(args[0].toLowerCase())) {
                    new helps().perform(player, null,null);
                    return true;
                }
                directCommandMap.get(args[0].toLowerCase()).perform(player, null, null);
            }

        }
        if (args.length >= 2) {
            if (!subCommandMap.containsKey(args[1].toLowerCase())) {
                new helps().perform(player, null,null);
                return true;
            }
            subCommandMap.get(args[1].toLowerCase()).perform(player, args[0], Arrays.copyOfRange(args, 1, args.length));
        }
        return true;
    }
}
