package me.autobot.playerdoll.command;

import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.tree.ArgumentCommandNode;
import com.mojang.brigadier.tree.LiteralCommandNode;
import me.autobot.playerdoll.api.LangFormatter;
import me.autobot.playerdoll.api.PlayerDollAPI;
import me.autobot.playerdoll.api.action.ActionTypeHelper;
import me.autobot.playerdoll.api.action.pack.ActionPack;
import me.autobot.playerdoll.api.command.*;
import me.autobot.playerdoll.api.command.argument.GameProfileArgument;
import me.autobot.playerdoll.api.command.argument.RotationArgument;
import me.autobot.playerdoll.api.command.argument.Vec3Argument;
import me.autobot.playerdoll.api.command.subcommand.SubCommand;
import me.autobot.playerdoll.api.command.subcommand.builtin.*;
import me.autobot.playerdoll.api.command.subcommand.builtin.actionpack.ActionStop;
import me.autobot.playerdoll.api.doll.DollConfig;
import me.autobot.playerdoll.api.doll.DollNameUtil;
import me.autobot.playerdoll.api.inv.button.GlobalFlagButton;
import me.autobot.playerdoll.api.inv.button.PersonalFlagButton;
import me.autobot.playerdoll.api.wrapper.builtin.WDirection;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.UUID;
import java.util.function.Function;

import static com.mojang.brigadier.builder.LiteralArgumentBuilder.literal;
import static com.mojang.brigadier.builder.RequiredArgumentBuilder.argument;


public class BuiltinCommandBuilder implements CommandBuilderAPI {
    private final CommandBuilder builder = PlayerDollAPI.getCommandBuilder();
    private final LiteralCommandNode<Object> root;
    public final GameProfileArgument argGameProfile;
    public final RotationArgument argRotation;
    public final Vec3Argument argVec3;
    
    public BuiltinCommandBuilder(LiteralCommandNode<Object> root) {
        this.root = root;
        argGameProfile = CommandArgUtil.getArgumentImpl(GameProfileArgument.class);
        argRotation = CommandArgUtil.getArgumentImpl(RotationArgument.class);
        argVec3 = CommandArgUtil.getArgumentImpl(Vec3Argument.class);

        setActionNodes();
        setCreate();
        setDespawn();
        setDismount();
        setEChest();
        setEXP();
        setGive();
        setGSet();
        setInfo();
        setInv();
        setLook();
        setMenu();
        setMount();
        setMove();
        setPSet();
        setRemove();
        setSet();
        setSlot();
        setSneak();
        setSpawn();
        setSprint();
        setStop();
        setTp();
        setTurn();
        setUnSneak();
        setUnSprint();

    }

    private void setActionNodes() {
        LiteralCommandNode<Object> attack = literal("attack")
                .requires(o -> builder.testStaticPermission(o, player -> player.hasPermission("playerdoll.command.attack")))
                .then(builder.complexAction(ActionTypeHelper.Defaults.ATTACK, PersonalFlagButton.ATTACK))
                .build();

        LiteralCommandNode<Object> jump = literal("jump")
                .requires(o -> builder.testStaticPermission(o, player -> player.hasPermission("playerdoll.command.jump")))
                .then(builder.complexAction(ActionTypeHelper.Defaults.JUMP, PersonalFlagButton.JUMP))
                .build();

        LiteralCommandNode<Object> drop = literal("drop")
                .requires(o -> builder.testStaticPermission(o, player -> player.hasPermission("playerdoll.command.drop")))
                .then(builder.complexAction(ActionTypeHelper.Defaults.DROP_ITEM, PersonalFlagButton.DROP))
                .build();

        LiteralCommandNode<Object> dropStack = literal("dropStack")
                .requires(o -> builder.testStaticPermission(o, player -> player.hasPermission("playerdoll.command.drop")))
                .then(builder.complexAction(ActionTypeHelper.Defaults.DROP_STACK, PersonalFlagButton.DROP))
                .build();

        LiteralCommandNode<Object> lookAt = literal("lookAt")
                .requires(o -> builder.testStaticPermission(o, player -> player.hasPermission("playerdoll.command.look")))
                .then(builder.complexAction(ActionTypeHelper.Defaults.LOOK_AT, PersonalFlagButton.LOOK))
                .build();

        LiteralCommandNode<Object> swap = literal("swapHands")
                .requires(o -> builder.testStaticPermission(o, player -> player.hasPermission("playerdoll.command.swap")))
                .then(builder.complexAction(ActionTypeHelper.Defaults.SWAP_HANDS, PersonalFlagButton.SWAP))
                .build();

        LiteralCommandNode<Object> use = literal("use")
                .requires(o -> builder.testStaticPermission(o, player -> player.hasPermission("playerdoll.command.use")))
                .then(builder.complexAction(ActionTypeHelper.Defaults.USE, PersonalFlagButton.USE))
                .build();

        // Drop & DropStack commands
        ArgumentCommandNode<Object, String> onlineDollsWithSelf_Drop = argument("target", StringArgumentType.word())
                .suggests((commandContext, suggestionsBuilder) -> {
                    if (builder.convertPlayer()) {
                        suggestionsBuilder.suggest(builder.getSelfIndicator(), () -> "self");
                    }
                    builder.setDollSuggestion(suggestionsBuilder, builder::getHoverHandItems);
                    return suggestionsBuilder.buildFuture();
                })
                .then(literal("all").executes(commandContext -> builder.actionDropExecute(commandContext, -2)))
                .then(literal("mainhand").executes(commandContext -> builder.actionDropExecute(commandContext, -1)))
                .then(literal("offhand").executes(commandContext -> builder.actionDropExecute(commandContext, 40)))
                .then(literal("helmet").executes(commandContext -> builder.actionDropExecute(commandContext, 39)))
                .then(literal("chestplate").executes(commandContext -> builder.actionDropExecute(commandContext, 38)))
                .then(literal("leggings").executes(commandContext -> builder.actionDropExecute(commandContext, 37)))
                .then(literal("boots").executes(commandContext -> builder.actionDropExecute(commandContext, 36)))
                .then(argument("slot", IntegerArgumentType.integer(0,40))
                        .executes(commandContext -> builder.actionDropExecute(commandContext, IntegerArgumentType.getInteger(commandContext, "slot"))))
                .build();


        drop.addChild(onlineDollsWithSelf_Drop);
        dropStack.addChild(onlineDollsWithSelf_Drop);

        root.addChild(attack);
        root.addChild(jump);
        root.addChild(drop);
        root.addChild(dropStack);
        root.addChild(lookAt);
        root.addChild(swap);
        root.addChild(use);
    }
    
    private void setCreate() {
        LiteralCommandNode<Object> create = literal("create")
                .requires(o -> builder.testStaticPermission(o, player -> player.hasPermission("playerdoll.command.create")))
                .then(argument("name", StringArgumentType.word())
                        .executes(commandContext -> {
                            String target = StringArgumentType.getString(commandContext, "name");
                            return DollCommandSource.execute(commandContext, new Create(target));
                        })
                        .requires(o -> builder.testStaticPermission(o, player -> player.hasPermission("playerdoll.argument.create.skin")))
                        .then(literal("extern").then(argument("skin", StringArgumentType.word())

                                .executes(commandContext -> {
                                    String target = StringArgumentType.getString(commandContext, "name");
                                    String extern = StringArgumentType.getString(commandContext, "skin");
                                    return DollCommandSource.execute(commandContext, new Create(target, extern));
                                }))
                        )
                        .then(literal("local").then(argument("skin", argGameProfile.getGameProfileArgument())
                                .suggests(builder.suggestOnlinePlayer())
                                .executes(commandContext -> {
                                    String target = StringArgumentType.getString(commandContext, "name");
                                    Collection<GameProfile> profiles = argGameProfile.getGameProfiles(commandContext, "skin");
                                    return DollCommandSource.execute(commandContext, new Create(target, profiles));
                                }))))
                .build();
        root.addChild(create);
    }
    // Despawn
    private void setDespawn() {
        LiteralCommandNode<Object> despawn = literal("despawn")
                .requires(o -> builder.testStaticPermission(o, player -> player.hasPermission("playerdoll.command.despawn")))
                .then(builder.getDollTarget(player -> {
                    Location loc = player.getLocation();
                    return LangFormatter.YAMLReplace("cmd-hover.despawn", loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());
                })
                        .executes(commandContext -> builder.performCommand(commandContext, Despawn::new)))
                .build();
        root.addChild(despawn);
    }
    
    // Dismount
    private void setDismount() {
        LiteralCommandNode<Object> dismount = literal("dismount")
                .requires(o -> builder.testStaticPermission(o, player -> player.hasPermission("playerdoll.command.mount")))
                .then(builder.getDollTarget(player -> LangFormatter.YAMLReplace("cmd-hover.dismount", player.getVehicle() != null))
                        .executes(commandContext -> builder.simpleActionExecute(commandContext, ActionPack::dismount, PersonalFlagButton.MOUNT)))
                .build();
        root.addChild(dismount);
    }
    // EChest
    private void setEChest() {
        LiteralCommandNode<Object> eChest = literal("echest")
                .requires(o -> builder.testStaticPermission(o, player -> player.hasPermission("playerdoll.command.echest")))
                .then(builder.getDollTarget()
                        .executes(commandContext -> builder.performCommand(commandContext, EChest::new)))
                .build();
        root.addChild(eChest);
    }
    // Exp
    private void setEXP() {
        LiteralCommandNode<Object> exp = literal("exp")
                .requires(o -> builder.testStaticPermission(o, player -> player.hasPermission("playerdoll.command.exp")))
                .then(builder.getDollTarget(player -> LangFormatter.YAMLReplace("cmd-hover.exp", player.getLevel()))
                        .executes(commandContext -> builder.performCommand(commandContext, player -> new Exp(player, 1, false)))
                        .then(literal("asOrb").executes(commandContext -> builder.performCommand(commandContext, player -> new Exp(player, 1, true))))
                        .then(literal("all")
                                .executes(commandContext -> builder.performCommand(commandContext, player -> new Exp(player, -1, false)))
                                .then(literal("asOrb").executes(commandContext -> builder.performCommand(commandContext, player -> new Exp(player, -1, true))))
                        )
                        .then(argument("levels", IntegerArgumentType.integer(1))
                                .executes(commandContext -> builder.performCommand(commandContext, player -> new Exp(player, IntegerArgumentType.getInteger(commandContext, "levels"), false)))
                                .then(literal("asOrb")
                                        .executes(commandContext -> builder.performCommand(commandContext, player -> new Exp(player, IntegerArgumentType.getInteger(commandContext, "levels"), true))))
                        ))
                //.executes(commandContext -> performExp(commandContext, IntegerArgumentType.getInteger(commandContext, "levels")))))
                .build();
        root.addChild(exp);
    }


    // Give
    private void setGive() {
        LiteralCommandNode<Object> give = literal("give")
                .requires(o -> builder.testStaticPermission(o, player -> player.hasPermission("playerdoll.command.give")))
                .then(argument("target", StringArgumentType.word())
                        .suggests((commandContext, suggestionsBuilder) -> {
                            String[] fileNames = builder.getAllDollNames();
                            if (fileNames == null) {
                                return suggestionsBuilder.buildFuture();
                            }
                            for (String fileName : fileNames) {
                                String dollName = fileName.substring(0, fileName.length() - ".yml".length());
                                DollConfig config = DollConfig.getTemporaryConfig(dollName);
                                if (DollConfig.DOLL_CONFIGS.containsKey(UUID.fromString(config.dollUUID.getValue()))) {
                                    // Filter Online Doll
                                    continue;
                                }
                                if (builder.testRuntimePermission(commandContext.getSource(), builder.canSuggestsDoll(player -> SubCommand.isOwnerOrOp(player, config)))) {
                                    suggestionsBuilder.suggest(builder.convertQuotedDollName(dollName), () -> LangFormatter.YAMLReplace("cmd-hover.give", config.ownerName.getValue()));
                                }
                            }
                            return suggestionsBuilder.buildFuture();
                        })
                        .then(argument("players", argGameProfile.getGameProfileArgument())
                                .suggests(builder.suggestOnlinePlayer())
                                .executes(commandContext -> {
                                    String target = StringArgumentType.getString(commandContext, "target");
                                    Collection<GameProfile> profiles = argGameProfile.getGameProfiles(commandContext, "players");
                                    return DollCommandSource.execute(commandContext, new Give(DollNameUtil.dollShortName(target), profiles));
                                })))
                .build();
        root.addChild(give);
    }
    // GSet
    private void setGSet() {
        LiteralCommandNode<Object> gSet = literal("gset")
                .requires(o -> builder.testStaticPermission(o, player -> player.hasPermission("playerdoll.command.gset")))
                .then(builder.getDollTarget(player -> {
                    DollConfig dollConfig = DollConfig.getOnlineConfig(player.getUniqueId());
                    StringBuilder onFlagBuilder = new StringBuilder(LangFormatter.YAMLReplace("cmd-hover.gset"));
                    dollConfig.generalSetting.forEach((flagType, toggle) -> {
                        String commandName = LangFormatter.YAMLReplace("set-menu." + flagType.registerName().toLowerCase() + ".name");
                        if (toggle) {
                            onFlagBuilder.append(" ").append(commandName);
                        }
                    });
                    return onFlagBuilder.toString();
                })
                        .then(playerSetOption(PersonalFlagButton.ADMIN, false))
                        .then(playerSetOption(PersonalFlagButton.ATTACK, false))
                        .then(playerSetOption(PersonalFlagButton.DESPAWN, false))
                        .then(playerSetOption(PersonalFlagButton.DROP, false))
                        .then(playerSetOption(PersonalFlagButton.ECHEST, false))
                        .then(playerSetOption(PersonalFlagButton.EXP, false))
                        .then(playerSetOption(PersonalFlagButton.GSET, false))
                        .then(playerSetOption(PersonalFlagButton.HIDDEN, false))
                        .then(playerSetOption(PersonalFlagButton.INV, false))
                        .then(playerSetOption(PersonalFlagButton.JUMP, false))
                        .then(playerSetOption(PersonalFlagButton.LOOK, false))
                        .then(playerSetOption(PersonalFlagButton.LOOKAT, false))
                        .then(playerSetOption(PersonalFlagButton.MENU, false))
                        .then(playerSetOption(PersonalFlagButton.MOUNT, false))
                        .then(playerSetOption(PersonalFlagButton.MOVE, false))
                        .then(playerSetOption(PersonalFlagButton.PSET, false))
                        .then(playerSetOption(PersonalFlagButton.SET, false))
                        .then(playerSetOption(PersonalFlagButton.SLOT, false))
                        .then(playerSetOption(PersonalFlagButton.SNEAK, false))
                        .then(playerSetOption(PersonalFlagButton.SPAWN, false))
                        .then(playerSetOption(PersonalFlagButton.SPRINT, false))
                        .then(playerSetOption(PersonalFlagButton.STOP, false))
                        .then(playerSetOption(PersonalFlagButton.SWAP, false))
                        .then(playerSetOption(PersonalFlagButton.TP, false))
                        .then(playerSetOption(PersonalFlagButton.TURN, false))
                        .then(playerSetOption(PersonalFlagButton.UNSNEAK, false))
                        .then(playerSetOption(PersonalFlagButton.UNSPRINT, false))
                        .then(playerSetOption(PersonalFlagButton.USE, false))
                        .executes(commandContext -> builder.performCommand(commandContext, GSet::new)))
                .build();
        root.addChild(gSet);
    }
    // Info
    private void setInfo() {
        LiteralCommandNode<Object> info = literal("info")
                .requires(o -> builder.testStaticPermission(o, player -> player.hasPermission("playerdoll.command.info")))
                .then(argument("target", StringArgumentType.word())
                        .suggests((commandContext, suggestionsBuilder) -> {
                            String[] fileNames = builder.getAllDollNames();
                            if (fileNames == null) {
                                return suggestionsBuilder.buildFuture();
                            }
                            for (String fileName : fileNames) {
                                String dollName = fileName.substring(0, fileName.length() - ".yml".length());
                                DollConfig config = DollConfig.getTemporaryConfig(dollName);
                                if (builder.testRuntimePermission(commandContext.getSource(), builder.canSuggestsDoll(player -> SubCommand.isOwnerOrOp(player, config)))) {
                                    suggestionsBuilder.suggest(builder.convertQuotedDollName(dollName));
                                    continue;
                                }
                                if (config.dollSetting.get(GlobalFlagButton.HIDE_FROM_LIST).getValue()) {
                                    // Filter Hide From List
                                    continue;
                                }
                                suggestionsBuilder.suggest(builder.convertQuotedDollName(dollName));
                            }
                            return suggestionsBuilder.buildFuture();
                        })
                        .executes(commandContext -> {
                            String target = StringArgumentType.getString(commandContext, "target");
                            return DollCommandSource.execute(commandContext, new Info(DollNameUtil.dollFullName(target)));
                        }))
                .build();
        root.addChild(info);
    }
    // Inv
    private void setInv() {
        LiteralCommandNode<Object> inv = literal("inv")
                .requires(o -> builder.testStaticPermission(o, player -> player.hasPermission("playerdoll.command.inv")))
                .then(builder.getDollTarget()
                        .executes(commandContext -> builder.performCommand(commandContext, Inv::new)))
                .build();
        root.addChild(inv);
    }
    // Look
    private void setLook() {
        PersonalFlagButton type = PersonalFlagButton.LOOK;
        LiteralCommandNode<Object> look = literal("look")
                .requires(o -> builder.testStaticPermission(o, player -> player.hasPermission("playerdoll.command.look")))
                .then(builder.getDollTarget(player -> LangFormatter.YAMLReplace("cmd-hover.look", player.getFacing().name()))
                        .then(literal("up").executes(commandContext -> builder.simpleActionExecute(commandContext, action -> action.look(WDirection.Direction.UP), type)))
                        .then(literal("down").executes(commandContext -> builder.simpleActionExecute(commandContext, action -> action.look(WDirection.Direction.DOWN), type)))
                        .then(literal("north").executes(commandContext -> builder.simpleActionExecute(commandContext, action -> action.look(WDirection.Direction.NORTH), type)))
                        .then(literal("east").executes(commandContext -> builder.simpleActionExecute(commandContext, action -> action.look(WDirection.Direction.EAST), type)))
                        .then(literal("south").executes(commandContext -> builder.simpleActionExecute(commandContext, action -> action.look(WDirection.Direction.SOUTH), type)))
                        .then(literal("west").executes(commandContext -> builder.simpleActionExecute(commandContext, action -> action.look(WDirection.Direction.WEST), type)))
                        .then(literal("at")
                                .then(argument("position", argVec3.getVec3Argument())
                                .executes(commandContext -> builder.simpleActionExecute(commandContext, action -> action.lookAt(argVec3.getVec3(commandContext, "position")), type))))
                                        .then(argument("direction", argRotation.getRotationArgument())
                                                .executes(commandContext -> builder.simpleActionExecute(commandContext, action -> action.look(argRotation.getRotation(commandContext, "direction")), type)))

                )
                .build();
        root.addChild(look);
    }
    // Menu
    private void setMenu() {
        LiteralCommandNode<Object> menu = literal("menu")
                .requires(o -> builder.testStaticPermission(o, player -> player.hasPermission("playerdoll.command.menu")))
                .then(builder.getDollTarget().executes(commandContext -> builder.performCommand(commandContext, Menu::new)))
                .build();
        root.addChild(menu);
    }
    // Mount
    private void setMount() {
        LiteralCommandNode<Object> mount = literal("mount")
                .requires(o -> builder.testStaticPermission(o, player -> player.hasPermission("playerdoll.command.mount")))
                .then(builder.getDollTarget(player -> LangFormatter.YAMLReplace("cmd-hover.mount", player.getVehicle() != null))
                        .executes(commandContext -> builder.simpleActionExecute(commandContext, action -> action.mount(true), PersonalFlagButton.MOUNT)))
                .build();
        root.addChild(mount);
    }
    // Move
    private void setMove() {
        PersonalFlagButton type = PersonalFlagButton.MOVE;
        LiteralCommandNode<Object> move = literal("move")
                .requires(o -> builder.testStaticPermission(o, player -> player.hasPermission("playerdoll.command.move")))
                .then(builder.getDollTarget()
                        .executes(commandContext -> builder.simpleActionExecute(commandContext, ActionPack::stopMovement, type))
                        .then(literal("forward").executes(commandContext -> builder.simpleActionExecute(commandContext, action -> action.setForward(1), type)))
                        .then(literal("backward").executes(commandContext -> builder.simpleActionExecute(commandContext, action -> action.setForward(-1), type)))
                        .then(literal("left").executes(commandContext -> builder.simpleActionExecute(commandContext, action -> action.setStrafing(1), type)))
                        .then(literal("right").executes(commandContext -> builder.simpleActionExecute(commandContext, action -> action.setStrafing(-1), type))))
                .build();
        root.addChild(move);
    }
    // PSet
    private void setPSet() {
        LiteralCommandNode<Object> pSet = literal("pset")
                .requires(o -> builder.testStaticPermission(o, player -> player.hasPermission("playerdoll.command.pset")))
                .then(builder.getDollTarget(player -> {
                    DollConfig dollConfig = DollConfig.getOnlineConfig(player.getUniqueId());
                    StringBuilder playerNameBuilder = new StringBuilder(LangFormatter.YAMLReplace("cmd-hover.pset"));
                    dollConfig.playerSetting.keySet().forEach(uuid -> playerNameBuilder.append(" ").append(Bukkit.getOfflinePlayer(uuid).getName()));
                    return playerNameBuilder.toString();
                })
                        .then(argument("players", argGameProfile.getGameProfileArgument())
                                .suggests(builder.suggestOnlinePlayer())
                                .then(playerSetOption(PersonalFlagButton.ADMIN, true))
                                .then(playerSetOption(PersonalFlagButton.ATTACK, true))
                                .then(playerSetOption(PersonalFlagButton.DESPAWN, true))
                                .then(playerSetOption(PersonalFlagButton.DISMOUNT, true))
                                .then(playerSetOption(PersonalFlagButton.DROP, true))
                                .then(playerSetOption(PersonalFlagButton.ECHEST, true))
                                .then(playerSetOption(PersonalFlagButton.EXP, true))
                                .then(playerSetOption(PersonalFlagButton.GSET, true))
                                .then(playerSetOption(PersonalFlagButton.HIDDEN, true))
                                .then(playerSetOption(PersonalFlagButton.INV, true))
                                .then(playerSetOption(PersonalFlagButton.JUMP, true))
                                .then(playerSetOption(PersonalFlagButton.LOOK, true))
                                .then(playerSetOption(PersonalFlagButton.LOOKAT, true))
                                .then(playerSetOption(PersonalFlagButton.MENU, true))
                                .then(playerSetOption(PersonalFlagButton.MOUNT, true))
                                .then(playerSetOption(PersonalFlagButton.MOVE, true))
                                .then(playerSetOption(PersonalFlagButton.PSET, true))
                                .then(playerSetOption(PersonalFlagButton.SET, true))
                                .then(playerSetOption(PersonalFlagButton.SLOT, true))
                                .then(playerSetOption(PersonalFlagButton.SNEAK, true))
                                .then(playerSetOption(PersonalFlagButton.SPAWN, true))
                                .then(playerSetOption(PersonalFlagButton.SPRINT, true))
                                .then(playerSetOption(PersonalFlagButton.STOP, true))
                                .then(playerSetOption(PersonalFlagButton.SWAP, true))
                                .then(playerSetOption(PersonalFlagButton.TP, true))
                                .then(playerSetOption(PersonalFlagButton.TURN, true))
                                .then(playerSetOption(PersonalFlagButton.UNSNEAK, true))
                                .then(playerSetOption(PersonalFlagButton.UNSPRINT, true))
                                .then(playerSetOption(PersonalFlagButton.USE, true))
                                .executes(commandContext -> {
                                    Collection<GameProfile> profiles = argGameProfile.getGameProfiles(commandContext, "players");
                                    return builder.performCommand(commandContext, player -> new PSet(player, profiles));
                                })))
                .build();
        root.addChild(pSet);
    }
    // Remove
    private void setRemove() {
        LiteralCommandNode<Object> remove = literal("remove")
                .requires(o -> builder.testStaticPermission(o, player -> player.hasPermission("playerdoll.command.remove")))
                .then(argument("target", StringArgumentType.word())
                        .suggests((commandContext, suggestionsBuilder) -> {
                            String[] fileNames = builder.getAllDollNames();
                            if (fileNames == null) {
                                return suggestionsBuilder.buildFuture();
                            }
                            for (String fileName : fileNames) {
                                String dollName = fileName.substring(0, fileName.length() - ".yml".length());
                                DollConfig config = DollConfig.getTemporaryConfig(dollName);
                                if (builder.testRuntimePermission(commandContext.getSource(), builder.canSuggestsDoll(player -> SubCommand.isOwnerOrOp(player, config)))) {
                                    suggestionsBuilder.suggest(builder.convertQuotedDollName(dollName), () -> LangFormatter.YAMLReplace("cmd-hover.remove", config.ownerName.getValue()));
                                }
                            }
                            return suggestionsBuilder.buildFuture();
                        })
                        .executes(commandContext -> {
                            String target = StringArgumentType.getString(commandContext, "target");
                            //Player targetPlayer = Bukkit.getPlayerExact(DollManager.dollFullName(target));
                            return DollCommandSource.execute(commandContext, new Remove(DollNameUtil.dollFullName(target)));
                        }))
                .build();
        root.addChild(remove);
    }

    private void setSet() {
        LiteralCommandNode<Object> set = literal("set")
                .requires(o -> builder.testStaticPermission(o, player -> player.hasPermission("playerdoll.command.set")))
                .then(builder.getDollTarget(player -> {
                    DollConfig dollConfig = DollConfig.getOnlineConfig(player.getUniqueId());
                    StringBuilder onFlagBuilder = new StringBuilder(LangFormatter.YAMLReplace("cmd-hover.set"));
                    dollConfig.dollSetting.forEach((flagType, configKey) -> {
                        String commandName = LangFormatter.YAMLReplace("set-menu." + flagType.registerName().toLowerCase() + ".name");
                        if (configKey.getValue()) {
                            onFlagBuilder.append(" ").append(commandName);
                        }
                    });
                    return onFlagBuilder.toString();
                })
                        .then(builder.dollSetOption(GlobalFlagButton.GLOW))
                        .then(builder.dollSetOption(GlobalFlagButton.GRAVITY))
                        .then(builder.dollSetOption(GlobalFlagButton.HOSTILITY))
                        .then(builder.dollSetOption(GlobalFlagButton.HIDE_FROM_LIST))
                        .then(builder.dollSetOption(GlobalFlagButton.INVULNERABLE))
                        .then(builder.dollSetOption(GlobalFlagButton.JOIN_AT_START))
                        .then(builder.dollSetOption(GlobalFlagButton.LARGE_STEP_SIZE))
                        .then(builder.dollSetOption(GlobalFlagButton.PHANTOM))
                        .then(builder.dollSetOption(GlobalFlagButton.PICKABLE))
                        .then(builder.dollSetOption(GlobalFlagButton.PUSHABLE))
                        .then(builder.dollSetOption(GlobalFlagButton.REAL_PLAYER_TICK_UPDATE))
                        .then(builder.dollSetOption(GlobalFlagButton.REAL_PLAYER_TICK_ACTION))
                        .executes(commandContext -> builder.performCommand(commandContext, Set::new)))
                .build();
        root.addChild(set);
    }
    // Slot
    private void setSlot() {
        LiteralCommandNode<Object> slot = literal("slot")
                .requires(o -> builder.testStaticPermission(o, player -> player.hasPermission("playerdoll.command.slot")))
                .then(builder.getDollTarget(player -> LangFormatter.YAMLReplace("cmd-hover.slot", player.getInventory().getHeldItemSlot() + 1))
                        .then(argument("slots", IntegerArgumentType.integer(1,9))
                                .executes(commandContext -> builder.simpleActionExecute(commandContext, action -> action.setSlot(IntegerArgumentType.getInteger(commandContext, "slots")), PersonalFlagButton.SLOT))))
                .build();
        root.addChild(slot);
    }
    // Sneak
    private void setSneak() {
        LiteralCommandNode<Object> sneak = literal("sneak")
                .requires(o -> builder.testStaticPermission(o, player -> player.hasPermission("playerdoll.command.sneak")))
                .then(builder.getDollTarget(player -> LangFormatter.YAMLReplace("cmd-hover.sneak", player.isSneaking()))
                        .executes(commandContext -> builder.simpleActionExecute(commandContext, action -> action.setSneaking(true), PersonalFlagButton.SNEAK)))
                .build();
        root.addChild(sneak);
    }
    // Spawn
    private void setSpawn() {
        LiteralCommandNode<Object> spawn = literal("spawn")
                .requires(o -> builder.testStaticPermission(o, player -> player.hasPermission("playerdoll.command.spawn")))
                .then(argument("target", StringArgumentType.word())
                        .suggests((commandContext, suggestionsBuilder) -> {
                            String[] fileNames = builder.getAllDollNames();
                            if (fileNames == null) {
                                return suggestionsBuilder.buildFuture();
                            }
                            for (String fileName : fileNames) {
                                String dollName = fileName.substring(0, fileName.length() - ".yml".length());
                                DollConfig config = DollConfig.getTemporaryConfig(dollName);
                                if (DollConfig.DOLL_CONFIGS.containsKey(UUID.fromString(config.dollUUID.getValue()))) {
                                    // Filter Online Doll
                                    continue;
                                }
                                if (builder.testRuntimePermission(commandContext.getSource(), builder.canSuggestsDoll(player -> SubCommand.hasDollPermission(player, config, PersonalFlagButton.SPAWN)))) {
                                    suggestionsBuilder.suggest(builder.convertQuotedDollName(dollName), () -> LangFormatter.YAMLReplace("cmd-hover.spawn", config.ownerName.getValue()));
                                }
                            }
                            return suggestionsBuilder.buildFuture();
                        })
                        .executes(commandContext -> {
                            String target = StringArgumentType.getString(commandContext, "target");
                            return DollCommandSource.execute(commandContext, new Spawn(DollNameUtil.dollShortName(target)));
                        }))
                .build();
        root.addChild(spawn);
    }

    // Sprint
    private void setSprint() {
        LiteralCommandNode<Object> sprint = literal("sprint")
                .requires(o -> builder.testStaticPermission(o, player -> player.hasPermission("playerdoll.command.sprint")))
                .then(builder.getDollTarget(player -> LangFormatter.YAMLReplace("cmd-hover.sprint", player.isSprinting()))
                        .executes(commandContext -> builder.simpleActionExecute(commandContext, action -> action.setSprinting(true), PersonalFlagButton.SPRINT)))
                .build();
        root.addChild(sprint);
    }
    // Stop
    private void setStop() {
        LiteralCommandNode<Object> stop = literal("stop")
                .requires(o -> builder.testStaticPermission(o, player -> player.hasPermission("playerdoll.command.stop")))
                .then(argument("target", StringArgumentType.word())
                        .suggests((commandContext, suggestionsBuilder) -> {
                            if (builder.convertPlayer()) {
                                suggestionsBuilder.suggest(builder.getSelfIndicator(), () -> "self");
                            }
                            return builder.setDollSuggestion(suggestionsBuilder, "doll");
                        })
                        .executes(commandContext -> builder.performCommandSelf(commandContext, (ActionStop::new))))
                .build();
        root.addChild(stop);
    }
    // Tp
    private void setTp() {
        LiteralCommandNode<Object> tp = literal("tp")
                .requires(o -> builder.testStaticPermission(o, player -> player.hasPermission("playerdoll.command.tp")))
                .then(builder.getDollTarget(player -> {
                    Location loc = player.getLocation();
                    return LangFormatter.YAMLReplace("cmd-hover.tp", loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());
                })
                        .executes(commandContext -> builder.performCommand(commandContext, player -> new Tp(player, false)))
                        .then(literal("center").executes(commandContext -> builder.performCommand(commandContext, player -> new Tp(player, false)))))
                .build();
        root.addChild(tp);
    }
    // Turn
    private void setTurn() {
        PersonalFlagButton type = PersonalFlagButton.TURN;
        LiteralCommandNode<Object> turn = literal("turn")
                .requires(o -> builder.testStaticPermission(o, player -> player.hasPermission("playerdoll.command.turn")))
                .then(builder.getDollTarget(player -> LangFormatter.YAMLReplace("cmd-hover.turn", player.getFacing().name()))
                        .then(literal("back").executes(commandContext -> builder.simpleActionExecute(commandContext, action -> action.turn(180, 0), type)))
                        .then(literal("left").executes(commandContext -> builder.simpleActionExecute(commandContext, action -> action.turn(-90, 0), type)))
                        .then(literal("right").executes(commandContext -> builder.simpleActionExecute(commandContext, action -> action.turn(90, 0), type)))
                        .then(argument("rotation", argRotation.getRotationArgument())
                                .executes(commandContext -> builder.simpleActionExecute(commandContext, action -> action.turn(argRotation.getRotation(commandContext, "rotation")), type))))
                .build();
        root.addChild(turn);
    }

    // Un-Sneak
    private void setUnSneak() {
        LiteralCommandNode<Object> unSneak = literal("unSneak")
                .requires(o -> builder.testStaticPermission(o, player -> player.hasPermission("playerdoll.command.sneak")))
                .then(builder.getDollTarget(player -> LangFormatter.YAMLReplace("cmd-hover.sneak", player.isSneaking()))
                        .executes(commandContext -> builder.simpleActionExecute(commandContext, action -> action.setSneaking(false), PersonalFlagButton.SNEAK)))
                .build();
        root.addChild(unSneak);
    }
    // Un-Sprint
    private void setUnSprint() {
        LiteralCommandNode<Object> unSprint = literal("unSprint")
                .requires(o -> builder.testStaticPermission(o, player -> player.hasPermission("playerdoll.command.sprint")))
                .then(builder.getDollTarget(player -> LangFormatter.YAMLReplace("cmd-hover.sprint", player.isSprinting()))
                        .executes(commandContext -> builder.simpleActionExecute(commandContext, action -> action.setSprinting(false), PersonalFlagButton.SPRINT)))
                .build();
        root.addChild(unSprint);
    }

    private LiteralArgumentBuilder<Object> playerSetOption(PersonalFlagButton flagType, boolean pset) {
        return literal(flagType.registerName().toLowerCase())
                .requires(o -> builder.testStaticPermission(o, player -> player.hasPermission(flagType.getPermission())))
                .then(argument("toggle", BoolArgumentType.bool())
                        .executes(commandContext -> {
                            Function<Player, DollCommandExecutor> function;
                            if (pset) {
                                Collection<GameProfile> profiles = argGameProfile.getGameProfiles(commandContext, "players");
                                function = player -> new PSet(player, profiles, flagType, BoolArgumentType.getBool(commandContext, "toggle"));
                            } else {
                                function = player -> new GSet(player, flagType, BoolArgumentType.getBool(commandContext, "toggle"));
                            }
                            return builder.performCommand(commandContext, function);
                        })
                );
    }
}
