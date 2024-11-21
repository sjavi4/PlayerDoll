package me.autobot.playerdoll.api.inv.button;

import org.bukkit.persistence.PersistentDataAdapterContext;
import org.bukkit.persistence.PersistentDataType;

import javax.annotation.Nonnull;

public class PDCButtonType implements PersistentDataType<String, InvButton> {
    @Override
    @Nonnull
    public Class<String> getPrimitiveType() {
        return String.class;
    }

    @Override
    @Nonnull
    public Class<InvButton> getComplexType() {
        return InvButton.class;
    }

    @Override
    @Nonnull
    public String toPrimitive(InvButton invButton, PersistentDataAdapterContext persistentDataAdapterContext) {
        return invButton.registerName();
    }

    @Override
    @Nonnull
    public InvButton fromPrimitive(String s, PersistentDataAdapterContext persistentDataAdapterContext) {
        return InvButton.getButtons().values().stream().filter(invButton -> invButton.registerName().equalsIgnoreCase(s)).findAny().orElseThrow();
    }
}
