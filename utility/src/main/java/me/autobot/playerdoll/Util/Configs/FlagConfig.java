package me.autobot.playerdoll.Util.Configs;

import me.autobot.playerdoll.Util.ConfigLoader;
import me.autobot.playerdoll.Util.Keys.FlagKey;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;

import java.lang.reflect.Field;
import java.util.LinkedHashMap;
import java.util.Map;

public class FlagConfig extends AbstractConfig {
    private static FlagConfig INSTANCE;
    public static final Map<String, Material> GLOBAL_FLAG_MAP = new LinkedHashMap<>();
    public static final Map<String, Material> PERSONAL_FLAG_MAP = new LinkedHashMap<>();
    public final FlagKey<FlagConfig> globalFlag_EChest;
    public final FlagKey<FlagConfig> globalFlag_Glow;
    public final FlagKey<FlagConfig> globalFlag_Gravity;
    //public final FlagKey<FlagConfig> globalFlag_Hide_From_List;
    public final FlagKey<FlagConfig> globalFlag_Hostility;
    public final FlagKey<FlagConfig> globalFlag_Inv;
    public final FlagKey<FlagConfig> globalFlag_Invulnerable;
    public final FlagKey<FlagConfig> globalFlag_Join_At_Start;
    public final FlagKey<FlagConfig> globalFlag_Large_Step_Size;
    public final FlagKey<FlagConfig> globalFlag_Phantom;
    public final FlagKey<FlagConfig> globalFlag_Pickable;
    public final FlagKey<FlagConfig> globalFlag_Pushable;
    public final FlagKey<FlagConfig> globalFlag_Real_Player_Tick_Update;
    public final FlagKey<FlagConfig> globalFlag_Real_Player_Tick_Action;
    public final FlagKey<FlagConfig> PersonalFlag_Admin;
    public final FlagKey<FlagConfig> PersonalFlag_Attack;
    public final FlagKey<FlagConfig> PersonalFlag_Copy;
    public final FlagKey<FlagConfig> PersonalFlag_Despawn;
    public final FlagKey<FlagConfig> PersonalFlag_Dismount;
    public final FlagKey<FlagConfig> PersonalFlag_Drop;
    public final FlagKey<FlagConfig> PersonalFlag_EChest;
    public final FlagKey<FlagConfig> PersonalFlag_Exp;
    public final FlagKey<FlagConfig> PersonalFlag_GSet;
    public final FlagKey<FlagConfig> PersonalFlag_Info;
    public final FlagKey<FlagConfig> PersonalFlag_Inv;
    public final FlagKey<FlagConfig> PersonalFlag_Jump;
    public final FlagKey<FlagConfig> PersonalFlag_Look;
    public final FlagKey<FlagConfig> PersonalFlag_LookAt;
    public final FlagKey<FlagConfig> PersonalFlag_Menu;
    public final FlagKey<FlagConfig> PersonalFlag_Mount;
    public final FlagKey<FlagConfig> PersonalFlag_Move;
    public final FlagKey<FlagConfig> PersonalFlag_PSet;
    public final FlagKey<FlagConfig> PersonalFlag_Set;
    public final FlagKey<FlagConfig> PersonalFlag_Slot;
    public final FlagKey<FlagConfig> PersonalFlag_Sneak;
    public final FlagKey<FlagConfig> PersonalFlag_Spawn;
    public final FlagKey<FlagConfig> PersonalFlag_Sprint;
    public final FlagKey<FlagConfig> PersonalFlag_Stop;
    public final FlagKey<FlagConfig> PersonalFlag_Strafe;
    public final FlagKey<FlagConfig> PersonalFlag_Swap;
    public final FlagKey<FlagConfig> PersonalFlag_TP;
    public final FlagKey<FlagConfig> PersonalFlag_Turn;
    public final FlagKey<FlagConfig> PersonalFlag_Use;
    public FlagConfig(YamlConfiguration config) {
        super(config);
        this.globalFlag_EChest = new FlagKey<>(this,"Global-Flag.echest",Material.ENDER_EYE);
        this.globalFlag_Glow = new FlagKey<>(this,"Global-Flag.glow", Material.GLOW_INK_SAC);
        this.globalFlag_Gravity = new FlagKey<>(this,"Global-Flag.gravity", Material.GRAVEL);
        //this.globalFlag_Hide_From_List = new FlagKey<>(this,"Global-Flag.hide_from_list", Material.TINTED_GLASS);
        this.globalFlag_Hostility = new FlagKey<>(this,"Global-Flag.hostility", Material.TARGET);
        this.globalFlag_Inv = new FlagKey<>(this,"Global-Flag.inv", Material.CHEST_MINECART);
        this.globalFlag_Invulnerable = new FlagKey<>(this,"Global-Flag.invulnerable", Material.TOTEM_OF_UNDYING);
        this.globalFlag_Join_At_Start = new FlagKey<>(this,"Global-Flag.join_at_start", Material.RECOVERY_COMPASS);
        this.globalFlag_Large_Step_Size = new FlagKey<>(this,"Global-Flag.large_step_size", Material.STONE_STAIRS);
        this.globalFlag_Phantom = new FlagKey<>(this,"Global-Flag.phantom", Material.PHANTOM_MEMBRANE);
        this.globalFlag_Pickable = new FlagKey<>(this,"Global-Flag.pickable", Material.HOPPER);
        this.globalFlag_Pushable = new FlagKey<>(this,"Global-Flag.pushable", Material.ARMOR_STAND);
        this.globalFlag_Real_Player_Tick_Update = new FlagKey<>(this, "Global-Flag.real_player_tick_update", Material.FISHING_ROD);
        this.globalFlag_Real_Player_Tick_Action = new FlagKey<>(this, "Global-Flag.real_player_tick_action", Material.CARROT_ON_A_STICK);

        this.PersonalFlag_Admin = new FlagKey<>(this,"Personal-Flag.admin", Material.WOODEN_PICKAXE);
        this.PersonalFlag_Attack = new FlagKey<>(this,"Personal-Flag.attack", Material.WOODEN_SWORD);
        this.PersonalFlag_Copy = new FlagKey<>(this,"Personal-Flag.copy", Material.RED_SAND);
        this.PersonalFlag_Despawn = new FlagKey<>(this,"Personal-Flag.despawn", Material.CRYING_OBSIDIAN);
        this.PersonalFlag_Dismount = new FlagKey<>(this,"Personal-Flag.dismount", Material.ACTIVATOR_RAIL);
        this.PersonalFlag_Drop = new FlagKey<>(this,"Personal-Flag.drop", Material.DROPPER);
        this.PersonalFlag_EChest = new FlagKey<>(this,"Personal-Flag.echest", Material.ENDER_EYE);
        this.PersonalFlag_Exp = new FlagKey<>(this,"Personal-Flag.exp", Material.EXPERIENCE_BOTTLE);
        this.PersonalFlag_GSet = new FlagKey<>(this,"Personal-Flag.gset", Material.CRAFTING_TABLE);
        this.PersonalFlag_Info = new FlagKey<>(this,"Personal-Flag.info", Material.BOOK);
        this.PersonalFlag_Inv = new FlagKey<>(this,"Personal-Flag.inv", Material.CHEST_MINECART);
        this.PersonalFlag_Jump = new FlagKey<>(this,"Personal-Flag.jump", Material.RABBIT_FOOT);
        this.PersonalFlag_Look = new FlagKey<>(this,"Personal-Flag.look", Material.SPYGLASS);
        this.PersonalFlag_LookAt = new FlagKey<>(this,"Personal-Flag.lookat", Material.OBSERVER);
        this.PersonalFlag_Menu = new FlagKey<>(this,"Personal-Flag.menu", Material.BARREL);
        this.PersonalFlag_Mount = new FlagKey<>(this,"Personal-Flag.mount", Material.MINECART);
        this.PersonalFlag_Move = new FlagKey<>(this,"Personal-Flag.move", Material.POWERED_RAIL);
        this.PersonalFlag_PSet = new FlagKey<>(this,"Personal-Flag.pset", Material.FURNACE);
        this.PersonalFlag_Set = new FlagKey<>(this,"Personal-Flag.set", Material.LECTERN);
        this.PersonalFlag_Slot = new FlagKey<>(this,"Personal-Flag.slot", Material.HOPPER);
        this.PersonalFlag_Sneak = new FlagKey<>(this,"Personal-Flag.sneak", Material.MAGMA_BLOCK);
        this.PersonalFlag_Spawn = new FlagKey<>(this,"Personal-Flag.spawn", Material.RED_BED);
        this.PersonalFlag_Sprint = new FlagKey<>(this,"Personal-Flag.sprint", Material.ICE);
        this.PersonalFlag_Stop = new FlagKey<>(this,"Personal-Flag.stop", Material.BLAZE_POWDER);
        this.PersonalFlag_Strafe = new FlagKey<>(this,"Personal-Flag.strafe", Material.OAK_BOAT);
        this.PersonalFlag_Swap = new FlagKey<>(this,"Personal-Flag.swap", Material.DAYLIGHT_DETECTOR);
        this.PersonalFlag_TP = new FlagKey<>(this,"Personal-Flag.tp", Material.ENDER_PEARL);
        this.PersonalFlag_Turn = new FlagKey<>(this,"Personal-Flag.turn", Material.IRON_TRAPDOOR);
        this.PersonalFlag_Use = new FlagKey<>(this,"Personal-Flag.use", Material.OAK_BUTTON);
        addToMap();
    }

    @SuppressWarnings("unchecked")
    private void addToMap() {
        GLOBAL_FLAG_MAP.clear();
        for (Field field : this.getClass().getFields()) {
            if (field.getType() == FlagKey.class) {
                try {
                    FlagKey<FlagConfig> get = (FlagKey<FlagConfig>) field.get(this);
                    String[] split = get.getPath().split("\\.");
                    if (split[0].equalsIgnoreCase("Global-Flag")) {
                        GLOBAL_FLAG_MAP.put(split[1],get.getValue());
                    } else if (split[0].equalsIgnoreCase("Personal-Flag")) {
                        PERSONAL_FLAG_MAP.put(split[1], get.getValue());
                    }
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }
    
    public static FlagConfig get() {
        return INSTANCE == null? INSTANCE = new FlagConfig(ConfigLoader.get().getConfig(ConfigLoader.ConfigType.FLAG)) : INSTANCE;
    }
    @Override
    public String getName() {
        return "Flag Config";
    }
}
