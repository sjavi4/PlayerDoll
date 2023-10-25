package me.autobot.playerdoll.newCommand;

import me.autobot.playerdoll.Configs.LangFormatter;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import oshi.util.tuples.Pair;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

public class CommandHandler implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if (args == null || args.length == 0) {
            return false;
        }
        Map<String,Object> commandMap = breakdown(args);
        if (commandMap == null) {
            return false;
        }
        String success = execute(sender, commandMap);
        if (!success.isEmpty()) sender.sendMessage(success);
        return true;
    }
    private Map<String,Object> breakdown(String[] args) {
        if (args.length < 2) {
            return null;
        }
        Map<String,Object> commandMap = new HashMap<>();
        commandMap.put("target", args[0]);
        commandMap.put("command", args[1].toLowerCase());
        String[] subArgs = null;
        if (args.length >= 3) {
            subArgs = new String[args.length - 2];
            System.arraycopy(args, 2, subArgs, 0, subArgs.length);
        }
        commandMap.put("args", subArgs);
        return commandMap;
    }
    private String execute(CommandSender sender , Map<String,Object> commandMap) {
        try {
            Class<?> clazz = Class.forName(this.getClass().getPackageName() + ".Execute." + commandMap.get("command"));
            //System.out.println(this.getClass().getPackageName() + ".Execute." + commandMap.get("command"));
            clazz.getConstructor(CommandSender.class, Object.class, Object.class).newInstance(sender, commandMap.get("target"), commandMap.get("args"));
        } catch (ClassNotFoundException ignored) {
            return LangFormatter.YAMLReplace("CommandNotExist",'&', new Pair<>("%a%",(String)commandMap.get("command")));
        } catch (NoSuchMethodException ignored) {
            return LangFormatter.YAMLReplace("WrongArgument",'&');
        } catch (IllegalAccessException | InstantiationException ignored ) {
        } catch (InvocationTargetException ignored) {
            return LangFormatter.YAMLReplace("InvokeCommandFail",'&');
        }
        return "";
    }
}