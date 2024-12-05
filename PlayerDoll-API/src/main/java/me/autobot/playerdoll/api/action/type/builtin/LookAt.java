package me.autobot.playerdoll.api.action.type.builtin;

import me.autobot.playerdoll.api.action.Action;
import me.autobot.playerdoll.api.action.pack.ActionPack;
import me.autobot.playerdoll.api.action.type.AbsActionType;
import me.autobot.playerdoll.api.doll.BaseEntity;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.util.BoundingBox;

public class LookAt extends AbsActionType {
    public LookAt(boolean preventSpectator) {
        super(preventSpectator);
    }

    @Override
    public String registerName() {
        return "look_at";
    }

    @Override
    public boolean execute(BaseEntity player, Action action) {
        ActionPack ap = player.getActionPack();
        Entity entity = Bukkit.getEntity(ap.lookingAtEntity);
        if (entity != null) {
            BoundingBox box = entity.getBoundingBox();
            ap.lookAt(box.getCenterX(), box.getCenterY(), box.getCenterZ());
        }
        return false;
    }
}
