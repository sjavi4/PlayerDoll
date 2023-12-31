package me.autobot.playerdoll.Command;

import me.autobot.playerdoll.Configs.LangFormatter;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import oshi.util.tuples.Pair;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;

public class CommandHandler implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if (args == null || args.length == 0) {
            return false;
        }
        ArrayList<Object> commands = argumentBreakdown(args);

        if (commands.size() == 0) return false;
        //Map<String,Object> commandMap = argumentBreakdown(args);
        /*
        if (commandMap == null) {
            return false;
        }

         */
        String success = execute(sender, commands);
        if (!success.isBlank()) sender.sendMessage(success);
        return true;
    }
    private ArrayList<Object> argumentBreakdown(String[] args) {
        if (args.length < 2) {
            return new ArrayList<>();
        }
        ArrayList<Object> commands = new ArrayList<>();
        commands.add(args[0]); //command
        commands.add(args[1].toLowerCase()); //target
        String[] subArgs = null;
        if (args.length >= 3) {
            subArgs = new String[args.length - 2];
            System.arraycopy(args, 2, subArgs, 0, subArgs.length);
        }
        commands.add(subArgs);
        return commands;
        /*
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

         */
    }
    private String execute(CommandSender sender, ArrayList<Object> commands) {
        try {
            Class<?> clazz = Class.forName(this.getClass().getPackageName() + ".Execute." + commands.get(0));
            //System.out.println(this.getClass().getPackageName() + ".Execute." + commandMap.get("command"));
            clazz.getConstructor(CommandSender.class, Object.class, Object.class).newInstance(sender, commands.get(1), commands.get(2));
        } catch (ClassNotFoundException ignored) {
            return LangFormatter.YAMLReplaceMessage("CommandNotExist",'&', new Pair<>("%a%",(String)commands.get(0)));
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
            return LangFormatter.YAMLReplaceMessage("WrongArgument",'&');
        } catch (IllegalAccessException | InstantiationException ignored) {
        } catch (InvocationTargetException e) {
            e.printStackTrace();
            return LangFormatter.YAMLReplaceMessage("InvokeCommandFail",'&');
        }
        return "";
    }
    /*
    private String execute(CommandSender sender , Map<String,Object> commandMap) {
        try {
            Class<?> clazz = Class.forName(this.getClass().getPackageName() + ".Execute." + commandMap.get("command"));
            //System.out.println(this.getClass().getPackageName() + ".Execute." + commandMap.get("command"));
            clazz.getConstructor(CommandSender.class, Object.class, Object.class).getInstance(sender, commandMap.get("target"), commandMap.get("args"));
        } catch (ClassNotFoundException ignored) {
            return LangFormatter.YAMLReplaceMessage("CommandNotExist",'&', new Pair<>("%a%",(String)commandMap.get("command")));
        } catch (NoSuchMethodException ignored) {
            ignored.printStackTrace();
            return LangFormatter.YAMLReplaceMessage("WrongArgument",'&');
        } catch (IllegalAccessException | InstantiationException ignored ) {
        } catch (InvocationTargetException ignored) {
            ignored.printStackTrace();
            return LangFormatter.YAMLReplaceMessage("InvokeCommandFail",'&');
        }
        return "";
    }

     */
}