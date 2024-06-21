package me.autobot.playerdoll.brigadier;

import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import com.mojang.brigadier.tree.ArgumentCommandNode;
import com.mojang.brigadier.tree.LiteralCommandNode;
import me.autobot.playerdoll.carpetmod.EntityPlayerActionPack;
import me.autobot.playerdoll.command.DollCommandExecutor;
import me.autobot.playerdoll.command.SubCommand;
import me.autobot.playerdoll.command.subcommand.*;
import me.autobot.playerdoll.command.subcommand.actionpack.ActionCommand;
import me.autobot.playerdoll.command.subcommand.actionpack.ActionDrop;
import me.autobot.playerdoll.command.subcommand.actionpack.ActionStop;
import me.autobot.playerdoll.command.subcommand.actionpack.SimpleActionCommand;
import me.autobot.playerdoll.config.BasicConfig;
import me.autobot.playerdoll.config.FlagConfig;
import me.autobot.playerdoll.doll.DollManager;
import me.autobot.playerdoll.doll.config.DollConfig;
import me.autobot.playerdoll.util.FileUtil;
import me.autobot.playerdoll.wrapper.argument.WrapperGameProfileArgument;
import me.autobot.playerdoll.wrapper.argument.WrapperRotationArgument;
import me.autobot.playerdoll.wrapper.argument.WrapperVec3Argument;
import me.autobot.playerdoll.wrapper.block.WrapperDirection;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.ServerOperator;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.function.Predicate;

import static com.mojang.brigadier.builder.LiteralArgumentBuilder.literal;
import static com.mojang.brigadier.builder.RequiredArgumentBuilder.argument;
import static me.autobot.playerdoll.brigadier.DollCommandSource.toCommandSender;

public class CommandBuilder {

    public static final List<LiteralCommandNode<Object>> COMMANDS = new ArrayList<>();
    public static final LiteralCommandNode<Object> builtRoot;
    public static final String SELF_INDICATION = "_";

    // Root Node
    static {
        builtRoot = literal("doll")
                .requires(o -> testPermission(o, player -> player.hasPermission("playerdoll.doll")))
                .build();

        COMMANDS.add(builtRoot);
    }

    // Action Nodes
    static {
        LiteralCommandNode<Object> attack = literal("attack")
                .requires(o -> testPermission(o, player -> player.hasPermission("playerdoll.command.attack")))
                .then(complexAction(EntityPlayerActionPack.ActionType.ATTACK, FlagConfig.PersonalFlagType.ATTACK))
                .build();

        LiteralCommandNode<Object> jump = literal("jump")
                .requires(o -> testPermission(o, player -> player.hasPermission("playerdoll.command.jump")))
                .then(complexAction(EntityPlayerActionPack.ActionType.JUMP, FlagConfig.PersonalFlagType.JUMP))
                .build();

        LiteralCommandNode<Object> drop = literal("drop")
                .requires(o -> testPermission(o, player -> player.hasPermission("playerdoll.command.drop")))
                .then(complexAction(EntityPlayerActionPack.ActionType.DROP_ITEM, FlagConfig.PersonalFlagType.DROP))
                .build();

        LiteralCommandNode<Object> dropStack = literal("dropStack")
                .requires(o -> testPermission(o, player -> player.hasPermission("playerdoll.command.drop")))
                .then(complexAction(EntityPlayerActionPack.ActionType.DROP_STACK, FlagConfig.PersonalFlagType.DROP))
                .build();

        LiteralCommandNode<Object> lookAt = literal("lookAt")
                .requires(o -> testPermission(o, player -> player.hasPermission("playerdoll.command.look")))
                .then(complexAction(EntityPlayerActionPack.ActionType.LOOK_AT, FlagConfig.PersonalFlagType.LOOK))
                .build();

        LiteralCommandNode<Object> swap = literal("swapHands")
                .requires(o -> testPermission(o, player -> player.hasPermission("playerdoll.command.swap")))
                .then(complexAction(EntityPlayerActionPack.ActionType.SWAP_HANDS, FlagConfig.PersonalFlagType.SWAP))
                .build();

        LiteralCommandNode<Object> use = literal("use")
                .requires(o -> testPermission(o, player -> player.hasPermission("playerdoll.command.use")))
                .then(complexAction(EntityPlayerActionPack.ActionType.USE, FlagConfig.PersonalFlagType.USE))
                .build();

        // Drop & DropStack commands
        ArgumentCommandNode<Object, String> onlineDollsWithSelf_Drop = argument("target", StringArgumentType.word())
                .suggests((commandContext, suggestionsBuilder) -> {
                    if (BasicConfig.get().convertPlayer.getValue()) {
                        suggestionsBuilder.suggest(SELF_INDICATION, () -> "Self");
                    }
                    setDollSuggestion(suggestionsBuilder);
                    return suggestionsBuilder.buildFuture();
                })
                .then(literal("all").executes(commandContext -> actionDropExecute(commandContext, -2)))
                .then(literal("mainhand").executes(commandContext -> actionDropExecute(commandContext, -1)))
                .then(literal("offhand").executes(commandContext -> actionDropExecute(commandContext, 40)))
                .then(literal("helmet").executes(commandContext -> actionDropExecute(commandContext, 39)))
                .then(literal("chestplate").executes(commandContext -> actionDropExecute(commandContext, 38)))
                .then(literal("leggings").executes(commandContext -> actionDropExecute(commandContext, 37)))
                .then(literal("boots").executes(commandContext -> actionDropExecute(commandContext, 36)))
                .then(argument("slot", IntegerArgumentType.integer(0,40))
                        .executes(commandContext -> actionDropExecute(commandContext, IntegerArgumentType.getInteger(commandContext, "slot"))))
                .build();


        drop.addChild(onlineDollsWithSelf_Drop);
        dropStack.addChild(onlineDollsWithSelf_Drop);

        builtRoot.addChild(attack);
        builtRoot.addChild(jump);
        builtRoot.addChild(drop);
        builtRoot.addChild(dropStack);
        builtRoot.addChild(lookAt);
        builtRoot.addChild(swap);
        builtRoot.addChild(use);

    }
/*
    // Copy
    static {
        LiteralCommandNode<Object> copy = literal("copy")
                .requires(o -> testPermission(o, player -> player.hasPermission("playerdoll.command.copy")))
                .then(getDollTarget()
                        .then(argument("who", StringArgumentType.word())
                                .suggests((commandContext, suggestionsBuilder) -> addOnlineDoll(suggestionsBuilder))
                                .executes(commandContext -> {
                                    String target = StringArgumentType.getString(commandContext, "target");
                                    String who = StringArgumentType.getString(commandContext, "who");
                                    Player targetPlayer = Bukkit.getPlayerExact(DollManager.dollFullName(target));
                                    Player copyFrom = Bukkit.getPlayerExact(DollManager.dollFullName(who));
                                    return DollCommandSource.execute(commandContext, new Copy(targetPlayer, copyFrom));
                                })))
                .build();
        builtRoot.addChild(copy);
    }

 */

    // Create
    static {
        LiteralCommandNode<Object> create = literal("create")
                .requires(o -> testPermission(o, player -> player.hasPermission("playerdoll.command.create")))
                .then(argument("name", StringArgumentType.word())
                        .executes(commandContext -> {
                            String target = StringArgumentType.getString(commandContext, "name");
                            return DollCommandSource.execute(commandContext, new Create(target));
                        })
                        .then(argument("skin", WrapperGameProfileArgument.gameProfile)
                                .requires(o -> testPermission(o, player -> player.hasPermission("playerdoll.argument.create.skin")))
                                .suggests(suggestOnlinePlayer())
                                .executes(commandContext -> {
                                    String target = StringArgumentType.getString(commandContext, "name");
                                    Collection<GameProfile> profiles = WrapperGameProfileArgument.getGameProfiles(commandContext, "skin");
                                    return DollCommandSource.execute(commandContext, new Create(target, profiles));
                                })))
                .build();
        builtRoot.addChild(create);
    }
    // Despawn
    static {
        LiteralCommandNode<Object> despawn = literal("despawn")
                .requires(o -> testPermission(o, player -> player.hasPermission("playerdoll.command.despawn")))
                .then(getDollTarget()
                        .executes(commandContext -> {
                            String target = StringArgumentType.getString(commandContext, "target");
                            Player targetPlayer = Bukkit.getPlayerExact(DollManager.dollFullName(target));
                            return DollCommandSource.execute(commandContext, new Despawn(targetPlayer));
                        }))
                .build();
        builtRoot.addChild(despawn);
    }
    // Dismount
    static {
        LiteralCommandNode<Object> dismount = literal("dismount")
                .requires(o -> testPermission(o, player -> player.hasPermission("playerdoll.command.mount")))
                .then(getDollTarget()
                        .executes(commandContext -> simpleActionExecute(commandContext, EntityPlayerActionPack::dismount, FlagConfig.PersonalFlagType.MOUNT)))
                .build();
        builtRoot.addChild(dismount);
    }
    // EChest
    static {
        LiteralCommandNode<Object> eChest = literal("echest")
                .requires(o -> testPermission(o, player -> player.hasPermission("playerdoll.command.echest")))
                .then(getDollTarget()
                        .executes(commandContext -> {
                            String target = StringArgumentType.getString(commandContext, "target");
                            Player targetPlayer = Bukkit.getPlayerExact(DollManager.dollFullName(target));
                            return DollCommandSource.execute(commandContext, new EChest(targetPlayer));
                        }))
                .build();
        builtRoot.addChild(eChest);
    }
    // Exp
    static {
        LiteralCommandNode<Object> exp = literal("exp")
                .requires(o -> testPermission(o, player -> player.hasPermission("playerdoll.command.exp")))
                .then(getDollTarget()
                        .executes(commandContext -> performExp(commandContext, 1))
                        .then(literal("all").executes(commandContext -> performExp(commandContext, -1)))
                        .then(argument("levels", IntegerArgumentType.integer(1))
                                .executes(commandContext -> performExp(commandContext, IntegerArgumentType.getInteger(commandContext, "levels")))))
                .build();
        builtRoot.addChild(exp);
    }
    // Give
    static {
        LiteralCommandNode<Object> give = literal("give")
                .requires(o -> testPermission(o, player -> player.hasPermission("playerdoll.command.give")))
                .then(getDollTarget()
                        .then(argument("players", WrapperGameProfileArgument.gameProfile)
                                .suggests(suggestOnlinePlayer())
                                .executes(commandContext -> {
                                    String target = StringArgumentType.getString(commandContext, "target");
                                    Player targetPlayer = Bukkit.getPlayerExact(DollManager.dollFullName(target));
                                    Collection<GameProfile> profiles = WrapperGameProfileArgument.getGameProfiles(commandContext, "players");
                                    return DollCommandSource.execute(commandContext, new Give(targetPlayer, profiles));
                                })))
                .build();
        builtRoot.addChild(give);
    }
    // GSet
    static {
        LiteralCommandNode<Object> gSet = literal("gset")
                .requires(o -> testPermission(o, player -> player.hasPermission("playerdoll.command.gset")))
                .then(getDollTarget()
                        .then(playerSetOption(FlagConfig.PersonalFlagType.ADMIN, false))
                        .then(playerSetOption(FlagConfig.PersonalFlagType.ATTACK, false))
                        //.then(playerSetOption(FlagConfig.PersonalFlagType.COPY, false))
                        .then(playerSetOption(FlagConfig.PersonalFlagType.DESPAWN, false))
                        //.then(playerSetOption(FlagConfig.PersonalFlagType.DISMOUNT, false))
                        .then(playerSetOption(FlagConfig.PersonalFlagType.DROP, false))
                        .then(playerSetOption(FlagConfig.PersonalFlagType.ECHEST, false))
                        .then(playerSetOption(FlagConfig.PersonalFlagType.EXP, false))
                        .then(playerSetOption(FlagConfig.PersonalFlagType.GSET, false))
                        .then(playerSetOption(FlagConfig.PersonalFlagType.INFO, false))
                        .then(playerSetOption(FlagConfig.PersonalFlagType.INV, false))
                        .then(playerSetOption(FlagConfig.PersonalFlagType.JUMP, false))
                        .then(playerSetOption(FlagConfig.PersonalFlagType.LOOK, false))
                        .then(playerSetOption(FlagConfig.PersonalFlagType.LOOKAT, false))
                        .then(playerSetOption(FlagConfig.PersonalFlagType.MENU, false))
                        .then(playerSetOption(FlagConfig.PersonalFlagType.MOUNT, false))
                        .then(playerSetOption(FlagConfig.PersonalFlagType.MOVE, false))
                        .then(playerSetOption(FlagConfig.PersonalFlagType.PSET, false))
                        .then(playerSetOption(FlagConfig.PersonalFlagType.SET, false))
                        .then(playerSetOption(FlagConfig.PersonalFlagType.SLOT, false))
                        .then(playerSetOption(FlagConfig.PersonalFlagType.SNEAK, false))
                        .then(playerSetOption(FlagConfig.PersonalFlagType.SPAWN, false))
                        .then(playerSetOption(FlagConfig.PersonalFlagType.SPRINT, false))
                        .then(playerSetOption(FlagConfig.PersonalFlagType.STOP, false))
                        //.then(playerSetOption(FlagConfig.PersonalFlagType.STRAFE, false))
                        .then(playerSetOption(FlagConfig.PersonalFlagType.SWAP, false))
                        .then(playerSetOption(FlagConfig.PersonalFlagType.TP, false))
                        .then(playerSetOption(FlagConfig.PersonalFlagType.TURN, false))
                        .then(playerSetOption(FlagConfig.PersonalFlagType.UNSNEAK, false))
                        .then(playerSetOption(FlagConfig.PersonalFlagType.UNSPRINT, false))
                        .then(playerSetOption(FlagConfig.PersonalFlagType.USE, false))
                        .executes(commandContext -> {
                                    String target = StringArgumentType.getString(commandContext, "target");
                                    Player targetPlayer = Bukkit.getPlayerExact(DollManager.dollFullName(target));
                                    //Collection<GameProfile> profiles = WrapperGameProfileArgument.getGameProfiles(commandContext, "players");
                                    return DollCommandSource.execute(commandContext, new GSet(targetPlayer));
                                }))
                .build();
        builtRoot.addChild(gSet);
    }
    // Info
    static {
        LiteralCommandNode<Object> info = literal("info")
                .requires(o -> testPermission(o, player -> player.hasPermission("playerdoll.command.info")))
                .then(getDollTarget()
                        .executes(commandContext -> {
                            String target = StringArgumentType.getString(commandContext, "target");
                            Player targetPlayer = Bukkit.getPlayerExact(DollManager.dollFullName(target));
                            return DollCommandSource.execute(commandContext, new Info(targetPlayer));
                        }))
                .build();
        builtRoot.addChild(info);
    }
    // Inv
    static {
        LiteralCommandNode<Object> inv = literal("inv")
                .requires(o -> testPermission(o, player -> player.hasPermission("playerdoll.command.inv")))
                .then(getDollTarget()
                        .executes(commandContext -> {
                            String target = StringArgumentType.getString(commandContext, "target");
                            Player targetPlayer = Bukkit.getPlayerExact(DollManager.dollFullName(target));
                            return DollCommandSource.execute(commandContext, new Inv(targetPlayer));
                        }))
                .build();
        builtRoot.addChild(inv);
    }
    // Look
    static {
        FlagConfig.PersonalFlagType type = FlagConfig.PersonalFlagType.LOOK;
        LiteralCommandNode<Object> look = literal("look")
                .requires(o -> testPermission(o, player -> player.hasPermission("playerdoll.command.look")))
                .then(getDollTarget()
                        .then(literal("up").executes(commandContext -> simpleActionExecute(commandContext, action -> action.look(WrapperDirection.UP), type)))
                        .then(literal("down").executes(commandContext -> simpleActionExecute(commandContext, action -> action.look(WrapperDirection.DOWN), type)))
                        .then(literal("north").executes(commandContext -> simpleActionExecute(commandContext, action -> action.look(WrapperDirection.NORTH), type)))
                        .then(literal("east").executes(commandContext -> simpleActionExecute(commandContext, action -> action.look(WrapperDirection.EAST), type)))
                        .then(literal("south").executes(commandContext -> simpleActionExecute(commandContext, action -> action.look(WrapperDirection.SOUTH), type)))
                        .then(literal("west").executes(commandContext -> simpleActionExecute(commandContext, action -> action.look(WrapperDirection.WEST), type)))
                        .then(literal("at").then(argument("position", WrapperVec3Argument.vec3)
                                .executes(commandContext -> simpleActionExecute(commandContext, action -> action.lookAt(WrapperVec3Argument.getVec3(commandContext, "position")), type))))
                        .then(argument("direction", WrapperRotationArgument.rotation)
                                .executes(commandContext -> simpleActionExecute(commandContext, action -> action.look(WrapperRotationArgument.getRotation(commandContext, "direction")), type)))

                )
                .build();
        builtRoot.addChild(look);
    }
    // Menu
    static {
        LiteralCommandNode<Object> menu = literal("menu")
                .requires(o -> testPermission(o, player -> player.hasPermission("playerdoll.command.menu")))
                .then(getDollTarget()
                        .executes(commandContext -> {
                            String target = StringArgumentType.getString(commandContext, "target");
                            Player targetPlayer = Bukkit.getPlayerExact(DollManager.dollFullName(target));
                            return DollCommandSource.execute(commandContext, new Menu(targetPlayer));
                        }))
                .build();
        builtRoot.addChild(menu);
    }
    // Mount
    static {
        LiteralCommandNode<Object> mount = literal("mount")
                .requires(o -> testPermission(o, player -> player.hasPermission("playerdoll.command.mount")))
                .then(getDollTarget()
                        .executes(commandContext -> simpleActionExecute(commandContext, action -> action.mount(true), FlagConfig.PersonalFlagType.MOUNT)))
                .build();
        builtRoot.addChild(mount);
    }
    // Move
    static {
        FlagConfig.PersonalFlagType type = FlagConfig.PersonalFlagType.MOVE;
        LiteralCommandNode<Object> move = literal("move")
                .requires(o -> testPermission(o, player -> player.hasPermission("playerdoll.command.move")))
                .then(getDollTarget()
                        .executes(commandContext -> simpleActionExecute(commandContext, EntityPlayerActionPack::stopMovement, type))
                        .then(literal("forward").executes(commandContext -> simpleActionExecute(commandContext, action -> action.setForward(1), type)))
                        .then(literal("backward").executes(commandContext -> simpleActionExecute(commandContext, action -> action.setForward(-1), type)))
                        .then(literal("left").executes(commandContext -> simpleActionExecute(commandContext, action -> action.setStrafing(1), type)))
                        .then(literal("right").executes(commandContext -> simpleActionExecute(commandContext, action -> action.setStrafing(-1), type))))
                .build();
        builtRoot.addChild(move);
    }
    // PSet
    static {
        LiteralCommandNode<Object> pSet = literal("pset")
                .requires(o -> testPermission(o, player -> player.hasPermission("playerdoll.command.pset")))
                .then(getDollTarget()
                        .then(argument("players", WrapperGameProfileArgument.gameProfile)
                                .suggests(suggestOnlinePlayer())
                                .then(playerSetOption(FlagConfig.PersonalFlagType.ADMIN, true))
                                .then(playerSetOption(FlagConfig.PersonalFlagType.ATTACK, true))
                                //.then(playerSetOption(FlagConfig.PersonalFlagType.COPY, true))
                                .then(playerSetOption(FlagConfig.PersonalFlagType.DESPAWN, true))
                                .then(playerSetOption(FlagConfig.PersonalFlagType.DISMOUNT, true))
                                .then(playerSetOption(FlagConfig.PersonalFlagType.DROP, true))
                                .then(playerSetOption(FlagConfig.PersonalFlagType.ECHEST, true))
                                .then(playerSetOption(FlagConfig.PersonalFlagType.EXP, true))
                                .then(playerSetOption(FlagConfig.PersonalFlagType.GSET, true))
                                .then(playerSetOption(FlagConfig.PersonalFlagType.INFO, true))
                                .then(playerSetOption(FlagConfig.PersonalFlagType.INV, true))
                                .then(playerSetOption(FlagConfig.PersonalFlagType.JUMP, true))
                                .then(playerSetOption(FlagConfig.PersonalFlagType.LOOK, true))
                                .then(playerSetOption(FlagConfig.PersonalFlagType.LOOKAT, true))
                                .then(playerSetOption(FlagConfig.PersonalFlagType.MENU, true))
                                .then(playerSetOption(FlagConfig.PersonalFlagType.MOUNT, true))
                                .then(playerSetOption(FlagConfig.PersonalFlagType.MOVE, true))
                                .then(playerSetOption(FlagConfig.PersonalFlagType.PSET, true))
                                .then(playerSetOption(FlagConfig.PersonalFlagType.SET, true))
                                .then(playerSetOption(FlagConfig.PersonalFlagType.SLOT, true))
                                .then(playerSetOption(FlagConfig.PersonalFlagType.SNEAK, true))
                                .then(playerSetOption(FlagConfig.PersonalFlagType.SPAWN, true))
                                .then(playerSetOption(FlagConfig.PersonalFlagType.SPRINT, true))
                                .then(playerSetOption(FlagConfig.PersonalFlagType.STOP, true))
                                //.then(playerSetOption(FlagConfig.PersonalFlagType.STRAFE, true))
                                .then(playerSetOption(FlagConfig.PersonalFlagType.SWAP, true))
                                .then(playerSetOption(FlagConfig.PersonalFlagType.TP, true))
                                .then(playerSetOption(FlagConfig.PersonalFlagType.TURN, true))
                                .then(playerSetOption(FlagConfig.PersonalFlagType.UNSNEAK, true))
                                .then(playerSetOption(FlagConfig.PersonalFlagType.UNSPRINT, true))
                                .then(playerSetOption(FlagConfig.PersonalFlagType.USE, true))
                                .executes(commandContext -> {
                                            String target = StringArgumentType.getString(commandContext, "target");
                                            Player targetPlayer = Bukkit.getPlayerExact(DollManager.dollFullName(target));
                                            Collection<GameProfile> profiles = WrapperGameProfileArgument.getGameProfiles(commandContext, "players");
                                            return DollCommandSource.execute(commandContext, new PSet(targetPlayer, profiles));
                                        })))
                .build();
        builtRoot.addChild(pSet);
    }
    // Remove
    static {
        LiteralCommandNode<Object> remove = literal("remove")
                .requires(o -> testPermission(o, player -> player.hasPermission("playerdoll.command.remove")))
                .then(argument("target", StringArgumentType.word())
                        .suggests((commandContext, suggestionsBuilder) -> {
                            FileUtil fileUtil = FileUtil.INSTANCE;
                            String[] fileNames = fileUtil.getDollDir().toFile().list((dir, name) -> name.endsWith(".yml"));
                            if (fileNames == null) {
                                return suggestionsBuilder.buildFuture();
                            }
                            for (String fileName : fileNames) {
                                String dollName = fileName.substring(0, fileName.length() - ".yml".length());
                                DollConfig config = DollConfig.getTemporaryConfig(dollName);
                                Predicate<CommandSender> predicate = player -> {
                                    if (!(player instanceof Player playerSender)) {
                                        return true;
                                    }
                                    return SubCommand.isOwnerOrOp(playerSender, config);
                                };
                                if (testPermission(commandContext.getSource(), predicate)) {
                                    suggestionsBuilder.suggest(dollName);
                                }
                            }
                            return suggestionsBuilder.buildFuture();
                        })
                        .executes(commandContext -> {
                            String target = StringArgumentType.getString(commandContext, "target");
                            //Player targetPlayer = Bukkit.getPlayerExact(DollManager.dollFullName(target));
                            return DollCommandSource.execute(commandContext, new Remove(DollManager.dollFullName(target)));
                        }))
                .build();
        builtRoot.addChild(remove);
    }
    // Rename
//    static {
//        LiteralCommandNode<Object> rename = literal("rename")
//                .requires(o -> testPermission(o, player -> player.hasPermission("playerdoll.command.rename")))
//                .then(getDollTarget()
//                        .executes(commandContext -> {
//                            String target = StringArgumentType.getString(commandContext, "target");
//                            Player targetPlayer = Bukkit.getPlayerExact(DollManager.dollFullName(target));
//                            return DollCommandSource.execute(commandContext, new Rename(targetPlayer));
//                        }))
//                .build();
//        builtRoot.addChild(rename);
//    }
    // Set
    static {
        LiteralCommandNode<Object> set = literal("set")
                .requires(o -> testPermission(o, player -> player.hasPermission("playerdoll.command.set")))
                .then(getDollTarget()
                        //.then(dollSetOption(FlagConfig.GlobalFlagType.ECHEST))
                        .then(dollSetOption(FlagConfig.GlobalFlagType.GLOW))
                        .then(dollSetOption(FlagConfig.GlobalFlagType.GRAVITY))
                        .then(dollSetOption(FlagConfig.GlobalFlagType.HOSTILITY))
                        //.then(dollSetOption(FlagConfig.GlobalFlagType.INV))
                        .then(dollSetOption(FlagConfig.GlobalFlagType.INVULNERABLE))
                        .then(dollSetOption(FlagConfig.GlobalFlagType.JOIN_AT_START))
                        .then(dollSetOption(FlagConfig.GlobalFlagType.LARGE_STEP_SIZE))
                        .then(dollSetOption(FlagConfig.GlobalFlagType.PHANTOM))
                        .then(dollSetOption(FlagConfig.GlobalFlagType.PICKABLE))
                        .then(dollSetOption(FlagConfig.GlobalFlagType.PUSHABLE))
                        .then(dollSetOption(FlagConfig.GlobalFlagType.REAL_PLAYER_TICK_UPDATE))
                        .then(dollSetOption(FlagConfig.GlobalFlagType.REAL_PLAYER_TICK_ACTION))
                        .executes(commandContext -> {
                            String target = StringArgumentType.getString(commandContext, "target");
                            Player targetPlayer = Bukkit.getPlayerExact(DollManager.dollFullName(target));
                            return DollCommandSource.execute(commandContext, new Set(targetPlayer));
                        }))
                .build();
        builtRoot.addChild(set);
    }
    // Slot
    static {
        LiteralCommandNode<Object> slot = literal("slot")
                .requires(o -> testPermission(o, player -> player.hasPermission("playerdoll.command.slot")))
                .then(getDollTarget()
                        .then(argument("slots", IntegerArgumentType.integer(1,9))
                                .executes(commandContext -> simpleActionExecute(commandContext, action -> action.setSlot(IntegerArgumentType.getInteger(commandContext, "slots")), FlagConfig.PersonalFlagType.SLOT))))
                .build();
        builtRoot.addChild(slot);
    }
    // Sneak
    static {
        LiteralCommandNode<Object> sneak = literal("sneak")
                .requires(o -> testPermission(o, player -> player.hasPermission("playerdoll.command.sneak")))
                .then(getDollTarget()
                        .executes(commandContext -> simpleActionExecute(commandContext, action -> action.setSneaking(true), FlagConfig.PersonalFlagType.SNEAK)))
                .build();
        builtRoot.addChild(sneak);
    }
    // Spawn
    static {
        LiteralCommandNode<Object> spawn = literal("spawn")
                .requires(o -> testPermission(o, player -> player.hasPermission("playerdoll.command.spawn")))
                .then(argument("target", StringArgumentType.word())
                        .suggests((commandContext, suggestionsBuilder) -> {
                            FileUtil fileUtil = FileUtil.INSTANCE;
                            String[] fileNames = fileUtil.getDollDir().toFile().list((dir, name) -> name.endsWith(".yml"));
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
                                Predicate<CommandSender> predicate = player -> {
                                    if (!(player instanceof Player playerSender)) {
                                        return true;
                                    }
                                    return SubCommand.hasDollPermission(playerSender, config, FlagConfig.PersonalFlagType.SPAWN);
                                };
                                if (testPermission(commandContext.getSource(), predicate)) {
                                    suggestionsBuilder.suggest(dollName);
                                }
                            }
                            return suggestionsBuilder.buildFuture();
                        })
                        .executes(commandContext -> {
                            String target = StringArgumentType.getString(commandContext, "target");
                            return DollCommandSource.execute(commandContext, new Spawn(DollManager.dollFullName(target)));
                        }))
                .build();
        builtRoot.addChild(spawn);
    }
    // Sprint
    static {
        LiteralCommandNode<Object> sprint = literal("sprint")
                .requires(o -> testPermission(o, player -> player.hasPermission("playerdoll.command.sprint")))
                .then(getDollTarget()
                        .executes(commandContext -> simpleActionExecute(commandContext, action -> action.setSprinting(true), FlagConfig.PersonalFlagType.SPRINT)))
                .build();
        builtRoot.addChild(sprint);
    }
    // Stop
    static {
        LiteralCommandNode<Object> stop = literal("stop")
                .requires(o -> testPermission(o, player -> player.hasPermission("playerdoll.command.stop")))
                .then(argument("target", StringArgumentType.word())
                        .suggests((commandContext, suggestionsBuilder) -> {
                            if (BasicConfig.get().convertPlayer.getValue()) {
                                suggestionsBuilder.suggest(SELF_INDICATION, () -> "Self");
                            }
                            return setDollSuggestion(suggestionsBuilder);
                        })
                        .executes(CommandBuilder::actionStopExecute))
                .build();
        builtRoot.addChild(stop);
    }
    // Tp
    static {
        LiteralCommandNode<Object> tp = literal("tp")
                .requires(o -> testPermission(o, player -> player.hasPermission("playerdoll.command.tp")))
                .then(getDollTarget()
                        .executes(commandContext -> performTp(commandContext, false))
                        .then(literal("center").executes(commandContext -> performTp(commandContext, true))))
                .build();
        builtRoot.addChild(tp);
    }
    // Turn
    static {
        FlagConfig.PersonalFlagType type = FlagConfig.PersonalFlagType.TURN;
        LiteralCommandNode<Object> turn = literal("turn")
                .requires(o -> testPermission(o, player -> player.hasPermission("playerdoll.command.turn")))
                .then(getDollTarget()
                        .then(literal("back").executes(commandContext -> simpleActionExecute(commandContext, action -> action.turn(180, 0), type)))
                        .then(literal("left").executes(commandContext -> simpleActionExecute(commandContext, action -> action.turn(-90, 0), type)))
                        .then(literal("right").executes(commandContext -> simpleActionExecute(commandContext, action -> action.turn(90, 0), type)))
                        .then(argument("rotation", WrapperRotationArgument.rotation)
                                .executes(commandContext -> simpleActionExecute(commandContext, action -> action.turn(WrapperRotationArgument.getRotation(commandContext, "rotation")), type))))
                .build();
        builtRoot.addChild(turn);
    }

    // Un-Sneak
    static {
        LiteralCommandNode<Object> unSneak = literal("unSneak")
                .requires(o -> testPermission(o, player -> player.hasPermission("playerdoll.command.sneak")))
                .then(getDollTarget().executes(commandContext -> simpleActionExecute(commandContext, action -> action.setSneaking(false), FlagConfig.PersonalFlagType.SNEAK)))
                .build();
        builtRoot.addChild(unSneak);
    }
    // Un-Sprint
    static {
        LiteralCommandNode<Object> unSprint = literal("unSprint")
                .requires(o -> testPermission(o, player -> player.hasPermission("playerdoll.command.sprint")))
                .then(getDollTarget().executes(commandContext -> simpleActionExecute(commandContext, action -> action.setSprinting(false), FlagConfig.PersonalFlagType.SPRINT)))
                .build();
        builtRoot.addChild(unSprint);
    }







    // Redirect alias
    static {
        LiteralCommandNode<Object> prefixedRoot = literal("playerdoll:doll")
                .requires(o -> testPermission(o, player -> player.hasPermission("playerdoll.doll")))
                .redirect(builtRoot)
                .build();

        LiteralCommandNode<Object> managePrefixedRoot = literal("playerdoll:dollmanage")
                .requires(o -> testPermission(o, ServerOperator::isOp))
                .redirect(builtRoot)
                .build();
        LiteralCommandNode<Object> manageRoot = literal("dollmanage")
                .requires(o -> testPermission(o, ServerOperator::isOp))
                .redirect(builtRoot)
                .build();

        COMMANDS.add(prefixedRoot);
        COMMANDS.add(managePrefixedRoot);
        COMMANDS.add(manageRoot);
    }
    private static boolean testPermission(Object commandSourceStack, Predicate<CommandSender> predicate) {
        // When testing permission, No getSource() is required
        CommandSender sender = toCommandSender(commandSourceStack);
        return predicate.test(sender);
    }

    private static CompletableFuture<Suggestions> setDollSuggestion(SuggestionsBuilder builder) {
        DollManager.ONLINE_DOLLS.values().forEach(d -> builder.suggest(d.getBukkitPlayer().getName(),() -> "Doll"));
        return builder.buildFuture();
    }

    private static int actionExecute(CommandContext<Object> context, EntityPlayerActionPack.ActionType type, EntityPlayerActionPack.Action actionMode, FlagConfig.PersonalFlagType flagType) {
        String target = StringArgumentType.getString(context, "target");
        Player targetPlayer = Bukkit.getPlayerExact(DollManager.dollFullName(target));
        return DollCommandSource.execute(context, new ActionCommand(targetPlayer, type, actionMode, flagType));
    }
    private static int actionDropExecute(CommandContext<Object> context, int slot) {
        String target = StringArgumentType.getString(context, "target");
        boolean targetAsSelf = target.equals(SELF_INDICATION);
        Player targetPlayer = Bukkit.getPlayerExact(DollManager.dollFullName(target));
        return DollCommandSource.execute(context, new ActionDrop(targetPlayer, slot, targetAsSelf));
    }
    private static int actionStopExecute(CommandContext<Object> context) {
        String target = StringArgumentType.getString(context, "target");
        boolean targetAsSelf = target.equals(SELF_INDICATION);
        Player targetPlayer = Bukkit.getPlayerExact(DollManager.dollFullName(target));
        return DollCommandSource.execute(context, new ActionStop(targetPlayer, targetAsSelf));
    }
    private static int simpleActionExecute(CommandContext<Object> context, Consumer<EntityPlayerActionPack> consumer, FlagConfig.PersonalFlagType flagType) {
        String target = StringArgumentType.getString(context, "target");
        Player targetPlayer = Bukkit.getPlayerExact(DollManager.dollFullName(target));
        return DollCommandSource.execute(context, new SimpleActionCommand(targetPlayer, consumer, flagType));
    }

    private static int performTp(CommandContext<Object> context, boolean center) {
        String target = StringArgumentType.getString(context, "target");
        Player targetPlayer = Bukkit.getPlayerExact(DollManager.dollFullName(target));
        return DollCommandSource.execute(context, new Tp(targetPlayer, center));
    }
    private static int performExp(CommandContext<Object> context, int level) {
        String target = StringArgumentType.getString(context, "target");
        Player targetPlayer = Bukkit.getPlayerExact(DollManager.dollFullName(target));
        return DollCommandSource.execute(context, new Exp(targetPlayer, level));
    }
    private static RequiredArgumentBuilder<Object, String> getDollTarget() {
        return argument("target", StringArgumentType.word())
                .suggests((commandContext, suggestionsBuilder) -> setDollSuggestion(suggestionsBuilder));
    }
    private static RequiredArgumentBuilder<Object, String> complexAction(EntityPlayerActionPack.ActionType type, FlagConfig.PersonalFlagType flagType) {
           return argument("target", StringArgumentType.word())
                    .suggests((commandContext, suggestionsBuilder) -> {
                        if (BasicConfig.get().convertPlayer.getValue()) {
                            suggestionsBuilder.suggest(SELF_INDICATION, () -> "Self");
                        }
                        return setDollSuggestion(suggestionsBuilder);
                    })
                    .executes(commandContext -> actionExecute(commandContext, type, EntityPlayerActionPack.Action.once(), flagType))
                    .then(literal("once").executes(commandContext -> actionExecute(commandContext, type, EntityPlayerActionPack.Action.once(), flagType)))
                    .then(literal("continuous").executes(commandContext -> actionExecute(commandContext, type, EntityPlayerActionPack.Action.continuous(), flagType)))
                    .then(literal("interval")
                            .executes(commandContext -> actionExecute(commandContext, type, EntityPlayerActionPack.Action.once(), flagType))
                            .then(argument("intervals", IntegerArgumentType.integer(1))
                                    .executes(commandContext -> {
                                        int interval = IntegerArgumentType.getInteger(commandContext, "intervals");
                                        return actionExecute(commandContext, type, EntityPlayerActionPack.Action.interval(interval), flagType);
                                    })
                                    .then(argument("initialdelays",IntegerArgumentType.integer(1))
                                            .executes(commandContext -> {
                                                int interval = IntegerArgumentType.getInteger(commandContext, "initialdelays");
                                                int offset = IntegerArgumentType.getInteger(commandContext, "initialdelays");
                                                return actionExecute(commandContext, type, EntityPlayerActionPack.Action.interval(interval, offset), flagType);
                                            }))));
    }

    private static LiteralArgumentBuilder<Object> dollSetOption(FlagConfig.GlobalFlagType flagType) {
        return literal(flagType.name().toLowerCase())
                .requires(o -> testPermission(o, player -> player.hasPermission(flagType.getPermission())))
                .then(argument("toggle", BoolArgumentType.bool())
                        .executes(commandContext -> {
                            String target = StringArgumentType.getString(commandContext, "target");
                            Player targetPlayer = Bukkit.getPlayerExact(DollManager.dollFullName(target));
                            return DollCommandSource.execute(commandContext, new Set(targetPlayer, flagType, BoolArgumentType.getBool(commandContext, "toggle")));
                        })
                );
    }

    private static LiteralArgumentBuilder<Object> playerSetOption(FlagConfig.PersonalFlagType flagType, boolean pset) {
        return literal(flagType.name().toLowerCase())
                .requires(o -> testPermission(o, player -> player.hasPermission(flagType.getPermission())))
                .then(argument("toggle", BoolArgumentType.bool())
                        .executes(commandContext -> {
                            String target = StringArgumentType.getString(commandContext, "target");
                            Player targetPlayer = Bukkit.getPlayerExact(DollManager.dollFullName(target));
                            DollCommandExecutor executor;
                            if (pset) {
                                Collection<GameProfile> profiles = WrapperGameProfileArgument.getGameProfiles(commandContext, "players");
                                executor = new PSet(targetPlayer, profiles, flagType, BoolArgumentType.getBool(commandContext, "toggle"));
                            } else {
                                executor = new GSet(targetPlayer, flagType, BoolArgumentType.getBool(commandContext, "toggle"));
                            }
                            return DollCommandSource.execute(commandContext, executor);
                        })
                );
    }

    private static SuggestionProvider<Object> suggestOnlinePlayer() {
        return (commandContext, suggestionsBuilder) -> {
            Bukkit.getOnlinePlayers().forEach(p -> suggestionsBuilder.suggest(p.getName(), () -> "player"));
            return suggestionsBuilder.buildFuture();
        };
    }


/*
    private static <T> T tryGetArgument(CommandContext<?> context, String name, Class<T> type) {
        try {
            return context.getArgument(name, type);
        } catch (IllegalArgumentException ignored) {
            return null;
        }
    }

 */
}
