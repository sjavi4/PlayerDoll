package me.autobot.playerdoll.persistantdatatype;

import org.bukkit.persistence.PersistentDataAdapterContext;
import org.bukkit.persistence.PersistentDataType;

public class ButtonType implements PersistentDataType<String, Button> {
    @Override
    public Class<String> getPrimitiveType() {
        return String.class;
    }

    @Override
    public Class<Button> getComplexType() {
        return Button.class;
    }

    @Override
    public String toPrimitive(Button button, PersistentDataAdapterContext context) {
        return button.getCommand().toLowerCase();
    }

    @Override
    public Button fromPrimitive(String s, PersistentDataAdapterContext context) {
        return Button.valueOf(s);
    }
}
