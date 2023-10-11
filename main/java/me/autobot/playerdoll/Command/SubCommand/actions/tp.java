package me.autobot.playerdoll.Command.SubCommand.actions;

import me.autobot.playerdoll.Command.SubCommandHandler;
import me.autobot.playerdoll.Dolls.DollManager;
import net.minecraft.server.level.ServerPlayer;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_20_R2.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class tp implements SubCommandHandler {
    @Override
    public void perform(Player player, String dollName, String[] args) {
        String[] _args = Arrays.copyOf(args,2);

        DollManager doll = SubCommandHandler.checkDoll(player, dollName);
        if (doll == null) {return;}
        ServerPlayer serverPlayer = ((CraftPlayer)player).getHandle();
        doll.setPose(serverPlayer.getPose());

        doll.setDollLookAt();
        //Location pos = player.getLocation();
        String align = _args[1] == null ? "" : _args[1];
        if (doll.serverLevel() == serverPlayer.serverLevel()) {
            Location pos = player.getLocation();
            if (align.equalsIgnoreCase("gridded")) {
                doll.setPos(Math.round(pos.getX() * 2) / 2.0, pos.getBlockY(), Math.round(pos.getZ() * 2) / 2.0);
            } else {
                doll.setPos(pos.getX(), pos.getBlockY(), pos.getZ());
            }
            //doll.getBukkitEntity().teleport(player, PlayerTeleportEvent.TeleportCause.PLUGIN);
            //doll.getBukkitEntity().teleport(new Location())
            //doll.teleportTo(serverPlayer.serverLevel(),serverPlayer.);
            //doll.moveTo(serverPlayer.position());
        } else {
            doll.dollRespawn();
        }
    }

    @Override
    public List<List<String>> commandList() {
        return List.of(Collections.singletonList("gridded"));
    }


}
