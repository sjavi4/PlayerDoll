package me.autobot.playerdoll.config;

import me.autobot.playerdoll.configkey.FlagKey;
import me.autobot.playerdoll.persistantdatatype.Button;
import me.autobot.playerdoll.util.ConfigLoader;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;

import java.util.Arrays;
import java.util.EnumMap;
import java.util.Map;

public class FlagConfig extends AbstractConfig {
    private static FlagConfig INSTANCE;

    //public static final Map<String, Material> GLOBAL_FLAG_MAP = new LinkedHashMap<>();
    //public static final Map<String, Material> PERSONAL_FLAG_MAP = new LinkedHashMap<>();
    private final Map<GlobalFlagType, FlagKey<FlagConfig>> globalFlags = new EnumMap<>(GlobalFlagType.class);
    private final Map<PersonalFlagType, FlagKey<FlagConfig>> personalFlags = new EnumMap<>(PersonalFlagType.class);
    /*
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

     */
    public FlagConfig(YamlConfiguration config) {
        super(config);
        Arrays.stream(GlobalFlagType.values())
                .forEach(flagType -> globalFlags.put(flagType, new FlagKey<>(FlagConfig.this, flagType.path, flagType.material)));
        Arrays.stream(PersonalFlagType.values())
                .forEach(flagType -> personalFlags.put(flagType, new FlagKey<>(FlagConfig.this, flagType.path, flagType.material)));


        /*
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

 */

        ConfigLoader.get().saveConfig(this.yamlConfiguration, ConfigLoader.ConfigType.FLAG);
    }
/*
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

 */
    
    public static FlagConfig get() {
        return INSTANCE == null? INSTANCE = new FlagConfig(ConfigLoader.get().getConfig(ConfigLoader.ConfigType.FLAG)) : INSTANCE;
    }

    public Map<GlobalFlagType, FlagKey<FlagConfig>> getGlobalFlags() {
        return globalFlags;
    }

    public Map<PersonalFlagType, FlagKey<FlagConfig>> getPersonalFlags() {
        return personalFlags;
    }

    @Override
    public String getName() {
        return "Flag Config";
    }
    public enum GlobalFlagType implements Button {
        //ECHEST("playerdoll.globalflag.echest", "Global-Flag.echest", Material.ENDER_EYE),
        GLOW("playerdoll.globalflag.glow", "Global-Flag.glow", Material.GLOW_INK_SAC),
        GRAVITY("playerdoll.globalflag.gravity", "Global-Flag.gravity", Material.GRAVEL),
        HOSTILITY("playerdoll.globalflag.hostility", "Global-Flag.hostility", Material.TARGET),
        //INV("playerdoll.globalflag.inv", "Global-Flag.inv", Material.CHEST_MINECART),
        INVULNERABLE("playerdoll.globalflag.invulnerable", "Global-Flag.invulnerable", Material.TOTEM_OF_UNDYING),
        JOIN_AT_START("playerdoll.globalflag.join_at_start", "Global-Flag.join_at_start", Material.RECOVERY_COMPASS),
        LARGE_STEP_SIZE("playerdoll.globalflag.large_step_size", "Global-Flag.large_step_size", Material.STONE_STAIRS),
        PHANTOM("playerdoll.globalflag.phantom", "Global-Flag.phantom", Material.PHANTOM_MEMBRANE),
        PICKABLE("playerdoll.globalflag.pickable", "Global-Flag.pickable", Material.HOPPER),
        PUSHABLE("playerdoll.globalflag.pushable", "Global-Flag.pushable", Material.ARMOR_STAND),
        REAL_PLAYER_TICK_UPDATE("playerdoll.globalflag.real_player_tick_update", "Global-Flag.real_player_tick_update", Material.FISHING_ROD),
        REAL_PLAYER_TICK_ACTION("playerdoll.globalflag.real_player_tick_action", "Global-Flag.real_player_tick_action", Material.CARROT_ON_A_STICK);
        private final String permission;
        private final String path;
        private final Material material;

        GlobalFlagType(String permission, String path, Material material) {
            this.permission = permission;
            this.path = path;
            this.material = material;
        }

        public String getPermission() {
            return permission;
        }

        public String getPath() {
            return path;
        }
        public Material getMaterial() {
            return material;
        }

        @Override
        public boolean isToggleable() {
            return true;
        }

        @Override
        public String getCommand() {
            return name();
        }
    }
    public enum PersonalFlagType implements Button {
        ADMIN("playerdoll.personalflag.admin", "Personal-Flag.admin", Material.WOODEN_PICKAXE),
        ATTACK("playerdoll.personalflag.attack", "Personal-Flag.attack", Material.WOODEN_SWORD),
        //COPY("playerdoll.personalflag.copy", "Personal-Flag.copy", Material.RED_SAND),
        DESPAWN("playerdoll.personalflag.despawn", "Personal-Flag.despawn", Material.CRYING_OBSIDIAN),
        DISMOUNT("playerdoll.personalflag.dismount", "Personal-Flag.dismount", Material.MINECART),
        DROP("playerdoll.personalflag.drop", "Personal-Flag.drop", Material.DROPPER),
        ECHEST("playerdoll.personalflag.echest", "Personal-Flag.echest", Material.ENDER_EYE),
        EXP("playerdoll.personalflag.exp", "Personal-Flag.exp", Material.EXPERIENCE_BOTTLE),
        GSET("playerdoll.personalflag.gset", "Personal-Flag.gset", Material.CRAFTING_TABLE),
        INFO("playerdoll.personalflag.info", "Personal-Flag.info", Material.BOOK),
        INV("playerdoll.personalflag.inv", "Personal-Flag.inv", Material.CHEST_MINECART),
        JUMP("playerdoll.personalflag.jump", "Personal-Flag.jump", Material.RABBIT_FOOT),
        LOOK("playerdoll.personalflag.look", "Personal-Flag.look", Material.SPYGLASS),
        LOOKAT("playerdoll.personalflag.lookat", "Personal-Flag.lookat", Material.OBSERVER),
        MENU("playerdoll.personalflag.menu", "Personal-Flag.menu", Material.BARREL),
        MOUNT("playerdoll.personalflag.mount", "Personal-Flag.mount", Material.MINECART),
        MOVE("playerdoll.personalflag.move", "Personal-Flag.move", Material.POWERED_RAIL),
        PSET("playerdoll.personalflag.pset", "Personal-Flag.pset", Material.FURNACE),
        SET("playerdoll.personalflag.set", "Personal-Flag.set", Material.LECTERN),
        SLOT("playerdoll.personalflag.slot", "Personal-Flag.slot", Material.HOPPER),
        SNEAK("playerdoll.personalflag.sneak", "Personal-Flag.sneak", Material.MAGMA_BLOCK),
        SPAWN("playerdoll.personalflag.spawn", "Personal-Flag.spawn", Material.RED_BED),
        SPRINT("playerdoll.personalflag.sprint", "Personal-Flag.sprint", Material.ICE),
        STOP("playerdoll.personalflag.stop", "Personal-Flag.stop", Material.BLAZE_POWDER),
        //STRAFE("playerdoll.personalflag.strafe", "Personal-Flag.strafe", Material.OAK_BOAT),
        SWAP("playerdoll.personalflag.swap", "Personal-Flag.swap", Material.DAYLIGHT_DETECTOR),
        TP("playerdoll.personalflag.tp", "Personal-Flag.tp", Material.ENDER_PEARL),
        TURN("playerdoll.personalflag.turn", "Personal-Flag.turn", Material.IRON_TRAPDOOR),
        UNSNEAK("playerdoll.personalflag.unsneak", "Personal-Flag.unsneak", Material.MAGMA_BLOCK),
        UNSPRINT("playerdoll.personalflag.unsprint", "Personal-Flag.unsprint", Material.ICE),
        USE("playerdoll.personalflag.use", "Personal-Flag.use", Material.OAK_BUTTON);

        private final String permission;
        private final String path;
        private final Material material;
        PersonalFlagType(String permission, String path, Material material) {
            this.permission = permission;
            this.path = path;
            this.material = material;
        }

        public String getPermission() {
            return permission;
        }

        public String getPath() {
            return path;
        }

        public Material getMaterial() {
            return material;
        }

        @Override
        public boolean isToggleable() {
            return true;
        }

        @Override
        public String getCommand() {
            return name();
        }
    }
}
