package me.autobot.playerdoll.command.subcommand;

import com.mojang.brigadier.context.CommandContext;
import me.autobot.playerdoll.command.DollCommandExecutor;
import me.autobot.playerdoll.command.SubCommand;
import me.autobot.playerdoll.doll.config.DollConfig;
import me.autobot.playerdoll.util.LangFormatter;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.*;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class Info extends SubCommand implements DollCommandExecutor {
    private Player sender;
    public Info(String target) {
        super(target);
    }

    @Override
    public void execute() {
        DollConfig dollConfig = DollConfig.getTemporaryConfig(targetString);

        BaseComponent lineBreak = new TextComponent("\n");
        lineBreak.setColor(ChatColor.WHITE);

        BaseComponent dollNameComponent = new TextComponent(dollConfig.dollName.getValue());
        String hoverUUIDText = String.format("%s%s", LangFormatter.YAMLReplace("info-cmd.uuid"), dollConfig.dollUUID.getValue());
        dollNameComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(hoverUUIDText)));

        OfflinePlayer offlineDoll = Bukkit.getOfflinePlayer(UUID.fromString(dollConfig.dollUUID.getValue()));

        BaseComponent[] dollStatsComponent = new ComponentBuilder()
                .append(LangFormatter.YAMLReplace("info-cmd.name")).color(ChatColor.LIGHT_PURPLE)
                .append(dollNameComponent).color(ChatColor.WHITE)
                .append(" | ").color(ChatColor.WHITE).event((HoverEvent) null)
                .append(LangFormatter.YAMLReplace(offlineDoll.isOnline() ? "info-cmd.status-online" : "info-cmd.status-offline"))
                .color(offlineDoll.isOnline() ? ChatColor.GREEN : ChatColor.GRAY)
                .append(" | ").color(ChatColor.WHITE).event((ClickEvent) null)
                .append(LangFormatter.YAMLReplace("info-cmd.owner")).color(ChatColor.AQUA)
                .append(dollConfig.ownerName.getValue()).color(ChatColor.WHITE)
                .create();

        BaseComponent dollSettingComponent = new TextComponent(LangFormatter.YAMLReplace("info-cmd.doll-setting"));
        ComponentBuilder dollSettingBuilder = new ComponentBuilder();
        dollConfig.dollSetting.forEach((flagType, configKey) -> {
            String commandName = LangFormatter.YAMLReplace("set-menu." + flagType.getCommand().toLowerCase() + ".name");
            dollSettingBuilder.append(commandName)
                    .color(configKey.getValue() ? ChatColor.GREEN : ChatColor.RED);
            dollSettingBuilder.append(" ").color(ChatColor.WHITE);
        });
        dollSettingComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(dollSettingBuilder.create())));

        String suggestDollSetCmd = String.format("/playerdoll:doll set %s", targetString);
        dollSettingComponent.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, suggestDollSetCmd));

        BaseComponent generalSettingComponent = new TextComponent(LangFormatter.YAMLReplace("info-cmd.g-setting"));
        ComponentBuilder generalSettingBuilder = new ComponentBuilder();
        dollConfig.generalSetting.forEach((flagType, toggle) -> {
            String commandName = LangFormatter.YAMLReplace("set-menu." + flagType.getCommand().toLowerCase() + ".name");
            generalSettingBuilder.append(commandName)
                    .color(toggle ? ChatColor.GREEN : ChatColor.RED);
            generalSettingBuilder.append(" ").color(ChatColor.WHITE);
        });
        generalSettingComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(generalSettingBuilder.create())));

        String suggestGSetCmd = String.format("/playerdoll:doll gset %s", targetString);
        generalSettingComponent.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, suggestGSetCmd));

        ComponentBuilder playerSettingBuilder = new ComponentBuilder();
        dollConfig.playerSetting.forEach((uuid, personalFlagMap) -> {
            if (personalFlagMap.equals(dollConfig.generalSetting)) {
                // Skipping the same
                return;
            }
            OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(uuid);

            ComponentBuilder toggleBuilder = new ComponentBuilder();
            personalFlagMap.forEach((flagType, toggle) -> {
                String commandName = LangFormatter.YAMLReplace("set-menu." + flagType.getCommand().toLowerCase() + ".name");
                toggleBuilder.append(commandName)
                        .color(toggle ? ChatColor.GREEN : ChatColor.RED);
                toggleBuilder.append(" ").color(ChatColor.WHITE);
            });

            String suggestPSetCmd = String.format("/playerdoll:doll pset %s %s", targetString, offlinePlayer.getName());

            ComponentBuilder psetBuilder = new ComponentBuilder();
            psetBuilder.append(offlinePlayer.getName()).color(ChatColor.GOLD)
                    .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(toggleBuilder.create())))
                    .event(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, suggestPSetCmd));

            playerSettingBuilder.append(psetBuilder.create()).append(" ");
        });

        sender.spigot().sendMessage(lineBreak);
        sender.spigot().sendMessage(dollStatsComponent);
        sender.spigot().sendMessage(lineBreak);
        sender.spigot().sendMessage(dollSettingComponent, lineBreak, generalSettingComponent, lineBreak);
        sender.spigot().sendMessage(playerSettingBuilder.create());
        sender.spigot().sendMessage(lineBreak);
    }

    @Override
    public int onCommand(CommandSender sender, CommandContext<Object> context) {
        if (!(sender instanceof Player playerSender)) {
            sender.sendMessage(LangFormatter.YAMLReplaceMessage("require-player"));
            return 0;
        }
        this.sender = playerSender;
        if (targetString == null) {
            playerSender.sendMessage(LangFormatter.YAMLReplaceMessage("no-target"));
            return 0;
        }

        execute();

        return 1;
    }
}
