package me.autobot.addonDoll.listener;

import me.autobot.addonDoll.connection.DollLoginListener;
import me.autobot.playerdoll.api.event.SetConvertPlayerCheckProtocolEvent;
import me.autobot.playerdoll.api.event.SetDollLoginListenerEvent;
import net.minecraft.network.ConnectionProtocol;
import net.minecraft.server.network.ServerLoginPacketListenerImpl;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class APIEvents implements Listener {
    @EventHandler
    private void onSetLogin(SetDollLoginListenerEvent event) {
        event.setConstructor(DollLoginListener.class.getConstructors()[0]);
        try {
            Class.forName("me.autobot.addonDoll.connection.PlayerLoginListener");
        } catch (ClassNotFoundException ignored) {
        }
    }

    @EventHandler
    private void onSetConvertPlayer(SetConvertPlayerCheckProtocolEvent event) {
        event.setCheckProtocol((listener) -> listener != null && ((ServerLoginPacketListenerImpl)listener).protocol() == ConnectionProtocol.LOGIN);
    }
}
