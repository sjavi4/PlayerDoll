package me.autobot.playerdoll.api.command;

import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import com.mojang.brigadier.tree.LiteralCommandNode;
import me.autobot.playerdoll.api.FileUtil;
import me.autobot.playerdoll.api.LangFormatter;
import me.autobot.playerdoll.api.PlayerDollAPI;
import me.autobot.playerdoll.api.action.Action;
import me.autobot.playerdoll.api.action.ActionTypeHelper;
import me.autobot.playerdoll.api.action.pack.ActionPack;
import me.autobot.playerdoll.api.command.subcommand.builtin.Set;
import me.autobot.playerdoll.api.command.subcommand.builtin.actionpack.ActionCommand;
import me.autobot.playerdoll.api.command.subcommand.builtin.actionpack.ActionDrop;
import me.autobot.playerdoll.api.command.subcommand.builtin.actionpack.SimpleActionCommand;
import me.autobot.playerdoll.api.command.subcommand.builtin.misc.Version;
import me.autobot.playerdoll.api.config.impl.BasicConfig;
import me.autobot.playerdoll.api.doll.DollNameUtil;
import me.autobot.playerdoll.api.doll.DollStorage;
import me.autobot.playerdoll.api.inv.button.FlagButton;
import me.autobot.playerdoll.api.inv.button.GlobalFlagButton;
import me.autobot.playerdoll.api.inv.button.PersonalFlagButton;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.ServerLoadEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

import static com.mojang.brigadier.builder.LiteralArgumentBuilder.literal;
import static com.mojang.brigadier.builder.RequiredArgumentBuilder.argument;
import static me.autobot.playerdoll.api.command.DollCommandSource.toCommandSender;

public class CommandBuilder implements Listener {

    private static final List<LiteralCommandNode<Object>> COMMANDS = new ArrayList<>();
    //public static final List<CommandBuilderAPI> commandImpl = new ArrayList<>();

    private final LiteralCommandNode<Object> builtRoot;
    private final String selfIndicator;
    private final String dollIndicator;
    private final boolean shouldQuoteDollName;
    private final boolean convertPlayer;


    // Register Command
    @EventHandler
    private void onServerLoad(ServerLoadEvent event) {
        COMMANDS.forEach(CommandRegisterHelper::registerCommand);
        Bukkit.getOnlinePlayers().forEach(Player::updateCommands);
    }

    public CommandBuilder() {
        String quoteRegex = "^[a-zA-Z0-9+-.]";
        BasicConfig basicConfig = PlayerDollAPI.getConfigLoader().getBasicConfig();
        selfIndicator = "_";
        dollIndicator = basicConfig.dollIdentifier.getValue();
        shouldQuoteDollName = !dollIndicator.matches(quoteRegex);
        convertPlayer = basicConfig.convertPlayer.getValue();

        builtRoot = literal("doll")
                .requires(o -> testStaticPermission(o, player -> player.hasPermission("playerdoll.doll")))
                .build();

        LiteralCommandNode<Object> prefixedRoot = literal("playerdoll:doll")
                .requires(o -> testStaticPermission(o, player -> player.hasPermission("playerdoll.doll")))
                .redirect(builtRoot)
                .build();

        LiteralCommandNode<Object> managePrefixedRoot = literal("playerdoll:dollmanage")
                .requires(o -> testStaticPermission(o, this::isManager))
                .redirect(builtRoot)
                .build();
        LiteralCommandNode<Object> manageRoot = literal("dollmanage")
                .requires(o -> testStaticPermission(o, this::isManager))
                .redirect(builtRoot)
                .build();

        COMMANDS.add(builtRoot);
        COMMANDS.add(prefixedRoot);
        COMMANDS.add(managePrefixedRoot);
        COMMANDS.add(manageRoot);

        setMiscNode();
    }

    private void setMiscNode() {
        LiteralCommandNode<Object> misc = literal("misc")
                .then(literal("version").executes(commandContext -> performNoTargetCommand(commandContext, new Version())))
                .then(literal("reload")
                                .requires(o -> testStaticPermission(o, player -> player.hasPermission("playerdoll.command.reload")))
                        .then(literal("plugin"))
                        .then(literal("config"))
                )
                .build();

        builtRoot.addChild(misc);
    }
    public boolean testRuntimePermission(Object commandSourceStack, Predicate<CommandSender> predicate) {
        // Must call with getSource();
        return testStaticPermission(commandSourceStack, predicate);
    }
    public boolean testStaticPermission(Object commandSourceStack, Predicate<CommandSender> predicate) {
        // No getSource() is required
        CommandSender sender = toCommandSender(commandSourceStack);
        return predicate.test(sender);
    }


    public CompletableFuture<Suggestions> setDollSuggestion(SuggestionsBuilder builder, Function<Player, String> dollHoverText) {
        DollStorage.ONLINE_DOLLS.values().forEach(d -> builder.suggest(convertQuotedDollName(DollNameUtil.dollShortName(d.getBukkitPlayer().getName())),() -> dollHoverText.apply(d.getBukkitPlayer())));
        return builder.buildFuture();
    }
    public CompletableFuture<Suggestions> setDollSuggestion(SuggestionsBuilder builder, String simpleHoverText) {
        DollStorage.ONLINE_DOLLS.values().forEach(d -> builder.suggest(convertQuotedDollName(DollNameUtil.dollShortName(d.getBukkitPlayer().getName())),() -> simpleHoverText));
        return builder.buildFuture();
    }

    public int performNoTargetCommand(CommandContext<Object> context, DollCommandExecutor command) {
        return DollCommandSource.execute(context, command);
    }
    public int performCommand(CommandContext<Object> context, Function<Player, DollCommandExecutor> command) {
        String target = StringArgumentType.getString(context, "target");
        Player targetPlayer = Bukkit.getPlayerExact(DollNameUtil.dollFullName(target));
        return DollCommandSource.execute(context, command.apply(targetPlayer));
    }
    public int performCommandSelf(CommandContext<Object> context, BiFunction<Player, Boolean, ? extends DollCommandExecutor> command) {
        String target = StringArgumentType.getString(context, "target");
        boolean targetAsSelf = target.equals(selfIndicator);
        Player targetPlayer = Bukkit.getPlayerExact(DollNameUtil.dollFullName(target));
        return DollCommandSource.execute(context, command.apply(targetPlayer, targetAsSelf));
    }

    public int actionExecute(CommandContext<Object> context, ActionTypeHelper.Defaults type, Action actionMode, PersonalFlagButton flagType) {
        return performCommand(context, player -> new ActionCommand(player, type, actionMode, flagType));
    }

    public int actionDropExecute(CommandContext<Object> context, int slot) {
        return performCommandSelf(context, (player, aBoolean) -> new ActionDrop(player, slot, aBoolean));
    }
    public int simpleActionExecute(CommandContext<Object> context, Consumer<ActionPack> consumer, PersonalFlagButton flagType) {
        return performCommand(context, player -> new SimpleActionCommand(player, consumer, flagType));
    }
    public RequiredArgumentBuilder<Object, String> getDollTarget() {
        return argument("target", StringArgumentType.word())
                .suggests((commandContext, suggestionsBuilder) -> setDollSuggestion(suggestionsBuilder, "doll"));
    }
    public RequiredArgumentBuilder<Object, String> getDollTarget(Function<Player, String> dollHoverText) {
        return argument("target", StringArgumentType.word())
                .suggests((commandContext, suggestionsBuilder) -> setDollSuggestion(suggestionsBuilder, dollHoverText));
    }
    public RequiredArgumentBuilder<Object, String> complexAction(ActionTypeHelper.Defaults type, PersonalFlagButton flagType) {
           return argument("target", StringArgumentType.word())
                    .suggests((commandContext, suggestionsBuilder) -> {
                        if (convertPlayer) {
                            suggestionsBuilder.suggest(selfIndicator, () -> "self");
                        }
                        return setDollSuggestion(suggestionsBuilder, this::getHoverHandItems);
                    })
                    .executes(commandContext -> actionExecute(commandContext, type, Action.once(), flagType))
                    .then(literal("once").executes(commandContext -> actionExecute(commandContext, type, Action.once(), flagType)))
                    .then(literal("continuous").executes(commandContext -> actionExecute(commandContext, type, Action.continuous(), flagType)))
                    .then(literal("interval")
                            .executes(commandContext -> actionExecute(commandContext, type, Action.once(), flagType))
                            .then(argument("intervals", IntegerArgumentType.integer(1))
                                    .executes(commandContext -> {
                                        int interval = IntegerArgumentType.getInteger(commandContext, "intervals");
                                        return actionExecute(commandContext, type, Action.interval(interval), flagType);
                                    })
                                    .then(argument("initialdelays",IntegerArgumentType.integer(1))
                                            .executes(commandContext -> {
                                                int interval = IntegerArgumentType.getInteger(commandContext, "intervals");
                                                int offset = IntegerArgumentType.getInteger(commandContext, "initialdelays");
                                                return actionExecute(commandContext, type, Action.interval(interval, offset), flagType);
                                            }))))
                   .then(literal("perTick")
                           .executes(commandContext -> actionExecute(commandContext, type, Action.once(), flagType))
                           .then(argument("counts", IntegerArgumentType.integer(1, 40))
                                   .executes(commandContext -> {
                                       int counts = IntegerArgumentType.getInteger(commandContext, "counts");
                                       return actionExecute(commandContext, type, Action.perTick(counts), flagType);
                                   })
                                   .then(argument("initialdelays",IntegerArgumentType.integer(1))
                                           .executes(commandContext -> {
                                               int counts = IntegerArgumentType.getInteger(commandContext, "counts");
                                               int offset = IntegerArgumentType.getInteger(commandContext, "initialdelays");
                                               return actionExecute(commandContext, type, Action.perTick(counts, offset), flagType);
                                           }))));
    }

    public LiteralArgumentBuilder<Object> dollSetOption(FlagButton flagType) {
        return literal(flagType.registerName().toLowerCase())
                .requires(o -> testStaticPermission(o, player -> player.hasPermission(flagType.getPermission())))
                .then(argument("toggle", BoolArgumentType.bool())
                        .executes(commandContext -> performCommand(commandContext, player -> new Set(player, (GlobalFlagButton) flagType, BoolArgumentType.getBool(commandContext, "toggle"))))
                );
    }

//    private static LiteralArgumentBuilder<Object> playerSetOption(PersonalFlagButton flagType, boolean pset) {
//        return literal(flagType.registerName().toLowerCase())
//                .requires(o -> testStaticPermission(o, player -> player.hasPermission(flagType.getPermission())))
//                .then(argument("toggle", BoolArgumentType.bool())
//                        .executes(commandContext -> {
//                            Function<Player, DollCommandExecutor> function;
//                            if (pset) {
//                                Collection<GameProfile> profiles = ArgGameProfile.getGameProfiles(commandContext, "players");
//                                function = player -> new PSet(player, profiles, flagType, BoolArgumentType.getBool(commandContext, "toggle"));
//                            } else {
//                                function = player -> new GSet(player, flagType, BoolArgumentType.getBool(commandContext, "toggle"));
//                            }
//                            return performCommand(commandContext, function);
//                        })
//                );
//    }

    public SuggestionProvider<Object> suggestOnlinePlayer() {
        return (commandContext, suggestionsBuilder) -> {
            Bukkit.getOnlinePlayers().forEach(p -> suggestionsBuilder.suggest(p.getName(), () -> "player"));
            return suggestionsBuilder.buildFuture();
        };
    }

    public String getHoverHandItems(Player player) {
        ItemStack mainHandItem = player.getInventory().getItemInMainHand();
        ItemStack offHandItem = player.getInventory().getItemInOffHand();

        String mText = LangFormatter.YAMLReplace("cmd-hover.action-main", mainHandItem.getType().name());
        String oText = LangFormatter.YAMLReplace("cmd-hover.action-off", offHandItem.getType().name());

        return mText + " " + oText;
    }
    public boolean isManager(CommandSender sender) {
        return sender.isOp() || sender.hasPermission("playerdoll.dollmanage");
    }

    public String[] getAllDollNames() {
        FileUtil fileUtil = PlayerDollAPI.getFileUtil();
        return fileUtil.getDollDir().toFile().list((dir, name) -> name.endsWith(".yml"));
    }

    public Predicate<CommandSender> canSuggestsDoll(Function<Player ,Boolean> test) {
        return player -> {
            if (!(player instanceof Player playerSender)) {
                return true;
            }
            return test.apply(playerSender);
        };
    }

    public String convertQuotedDollName(String dollName) {
        if (dollIndicator.isEmpty()) {
            return dollName;
        } else {
            return dollName.startsWith(dollIndicator) ? "\"" + dollName + "\"" : dollName;
        }
    }

    public LiteralCommandNode<Object> getRoot() {
        return builtRoot;
    }

    public String getSelfIndicator() {
        return selfIndicator;
    }

    public String getDollIndicator() {
        return dollIndicator;
    }

    public boolean shouldQuoteDollName() {
        return shouldQuoteDollName;
    }

    public boolean convertPlayer() {
        return convertPlayer;
    }
}
