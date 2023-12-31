package me.autobot.playerdoll.Command.Execute;

import me.autobot.playerdoll.CarpetMod.EntityPlayerActionPack;
import me.autobot.playerdoll.Configs.PermissionManager;
import me.autobot.playerdoll.Dolls.IDoll;
import me.autobot.playerdoll.PlayerDoll;
import me.autobot.playerdoll.Command.Helper.ArgumentHelper;
import me.autobot.playerdoll.Command.Helper.DollDataValidator;
import me.autobot.playerdoll.Command.SubCommand;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.RayTraceResult;

import java.util.*;

public class lookat extends SubCommand {
    Player player;
    IDoll doll;
    String[] args;
    public lookat() {}
    public lookat(CommandSender sender, Object doll, Object args) {
        setPermission(MinPermission.Share, false);
        String dollName = checkDollName(doll);
        if (!checkPermission(sender, dollName, "Lookat")) return;
        player = (Player) sender;
        DollDataValidator validator = new DollDataValidator(player, dollName, dollName.substring(1));
        if (validator.isDollOffline()) return;
        this.doll = PlayerDoll.dollManagerMap.get(dollName);
        this.args = args == null? new String[]{""} : (String[]) args;
        execute();
    }

    @Override
    public void execute() {
        if (args.length >= 1) {
            if (args[0].equalsIgnoreCase("target")) {
                Location loc = player.getEyeLocation();
                RayTraceResult result = player.getWorld().rayTraceEntities(loc,loc.getDirection(), 3.5, entity -> !(entity instanceof Player));
                if (result != null && result.getHitEntity() != null) {
                    doll.getActionPack().lookAt(result.getHitEntity());
                    if (args.length >= 2) {
                        ArgumentHelper<Integer> argumentHelper = new ArgumentHelper<>();
                        ArrayList<Integer> intArgs = argumentHelper.castTo(args,Integer.class);
                        if (!player.isOp() && intArgs.size() > 0) {
                            PermissionManager perm = PermissionManager.getInstance(player);
                            intArgs.set(0,Math.max(perm.minLookatInterval,intArgs.get(0)));
                        }
                        EntityPlayerActionPack.Action action = ActionHandler.action(args[1].toLowerCase(), intArgs);
                        doll.getActionPack().start(EntityPlayerActionPack.ActionType.LOOK_AT, action);
                    }
                }
            } else {
                doll.getActionPack().lookAt(Bukkit.getPlayer(args[0]));
            }
        }
        // Coords
        if (args.length >= 3) {
            try {
                ArgumentHelper<Float> argumentHelper = new ArgumentHelper<>();
                ArrayList<Float> floatArgs = argumentHelper.castTo(args, Float.class);
                doll.getActionPack().lookAt(floatArgs.get(0), floatArgs.get(1), floatArgs.get(2));
            } catch (IndexOutOfBoundsException ignored) {
            }
        }
        /*
        switch (args.length) {
            // Player Name / Entity UUID
            case 1, 2, 4 -> {
                // Get ray Trace Entity
                if (args[0].equalsIgnoreCase("target")) {
                    Location loc = player.getEyeLocation();
                    RayTraceResult result = player.getWorld().rayTraceEntities(loc,loc.getDirection(), 3.5, entity -> !(entity instanceof Player));
                    if (result != null && result.getHitEntity() != null) {
                        doll.getActionPack().lookAt(result.getHitEntity());
                        if (args.length == 4) {
                            ArgumentHelper<Integer> argumentHelper = new ArgumentHelper<>();
                            ArrayList<Integer> intArgs = argumentHelper.castTo(args,Integer.class);
                            EntityPlayerActionPack.Action action = ActionHandler.action(args[1].toLowerCase(), intArgs);
                            doll.getActionPack().start(EntityPlayerActionPack.ActionType.LOOK_AT, action);
                        }
                    }
                } else {
                    doll.getActionPack().lookAt(Bukkit.getPlayer(args[0]));
                }
            }
            // Coords
            case 3 -> {
                ArgumentHelper<Float> argumentHelper = new ArgumentHelper<>();
                ArrayList<Float> floatArgs = argumentHelper.castTo(args, Float.class);
                try {
                    doll.getActionPack().lookAt(floatArgs.get(0), floatArgs.get(1), floatArgs.get(2));
                } catch (ArrayIndexOutOfBoundsException ignored) {
                }
            }
            // Invalid actions
            default -> {}
        }

         */
        /*
        if (Bukkit.getPlayer(args[0]) != null) {
            doll.getActionPack().lookAt(args[0]);
            //doll.getActionPack().lookAt(doll.server.getPlayerList().getPlayerByName(args[0]).getEyePosition());
        } else if (args.length >= 3) {
            ArgumentHelper<Float> argumentHelper = new ArgumentHelper<>();
            ArrayList<Float> floatArgs = argumentHelper.castTo(args, Float.class);
            doll.getActionPack().lookAt(floatArgs.get(0), floatArgs.get(1), floatArgs.get(2));
        }

         */
    }

    private void coords() {

    }
}