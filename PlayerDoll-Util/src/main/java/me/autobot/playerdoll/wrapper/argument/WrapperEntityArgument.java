package me.autobot.playerdoll.wrapper.argument;

public interface WrapperEntityArgument {
/*
    Method getPlayerMethod = Arrays.stream(ArgumentEntity.class.getMethods())
            .filter(method -> Modifier.isStatic(method.getModifiers()))
            .filter(method -> method.getReturnType() == EntityPlayer.class)
            .findFirst()
            .orElseThrow();

    ArgumentEntity player = ArgumentEntity.c();
    static WrapperServerPlayer getPlayer(CommandContext<Object> commandcontext, String s) throws CommandSyntaxException {
        try {
            return new WrapperServerPlayer(getPlayerMethod.invoke(null, commandcontext, s));
        } catch (InvocationTargetException | IllegalAccessException e) {
            throw ArgumentEntity.e.create();
        }
    }

 */
}
