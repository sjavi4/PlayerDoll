package me.autobot.playerdoll.api.doll;

import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import org.bukkit.permissions.PermissionAttachment;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class DollStorage {

    public static final Map<UUID, Doll> ONLINE_DOLLS = new ConcurrentHashMap<>();

    public static final Map<UUID, ExtendPlayer> ONLINE_TRANSFORMS = new ConcurrentHashMap<>();

    public static final Map<UUID, Integer> PLAYER_CREATION_COUNTS = new Object2IntOpenHashMap<>();

    public static final Map<UUID, PermissionAttachment> DOLL_PERMISSIONS = new HashMap<>();
}
