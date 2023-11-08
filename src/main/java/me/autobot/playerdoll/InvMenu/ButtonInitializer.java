package me.autobot.playerdoll.InvMenu;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.function.BiFunction;
import java.util.function.Consumer;

public class ButtonInitializer {
    private BiFunction<Player, Player, ItemStack> buttonFunction;
    private Consumer<InventoryClickEvent> clickEventConsumer;

    public ButtonInitializer creator(BiFunction<Player, Player, ItemStack> buttonFunction) {
        this.buttonFunction = buttonFunction;
        return this;
    }

    public ButtonInitializer consumer(Consumer<InventoryClickEvent> clickEventConsumer) {
        this.clickEventConsumer = clickEventConsumer;
        return this;
    }

    public Consumer<InventoryClickEvent> getClickEventConsumer() {
        return this.clickEventConsumer;
    }

    public BiFunction<Player, Player, ItemStack> getButtonFunction() {
        return this.buttonFunction;
    }
}
