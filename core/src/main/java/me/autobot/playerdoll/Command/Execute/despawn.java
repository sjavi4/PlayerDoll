package me.autobot.playerdoll.Command.Execute;

import me.autobot.playerdoll.Dolls.IDoll;
import me.autobot.playerdoll.PlayerDoll;
import me.autobot.playerdoll.Command.Helper.DollDataValidator;
import me.autobot.playerdoll.Command.SubCommand;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class despawn extends SubCommand {
    Player player;
    IDoll doll;
    String dollName;
    public despawn() {}
    public despawn(CommandSender sender, Object doll, Object args) {
        setPermission(MinPermission.Share, false);
        dollName = checkDollName(doll);
        if (!checkPermission(sender, dollName, "Despawn")) return;
        player = (Player) sender;
        DollDataValidator validator = new DollDataValidator(player, dollName, dollName.substring(1));
        if (validator.isDollOffline()) return;
        this.doll = PlayerDoll.dollManagerMap.get(dollName);
        execute();
    }

    @Override
    public void execute() {
        if (PlayerDoll.isFolia) IDoll.foliaDisconnect(false, Bukkit.getPlayer(dollName),doll);
        else doll._disconnect();
    }

}
