package me.autobot.playerdoll.wrapper;

import net.md_5.bungee.api.connection.Connection;
import net.md_5.bungee.api.connection.PendingConnection;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class InitialHandler {

    public static final Class<?> initialHandlerClass;
    private static final Field channelWrapperField;
    private static final Method getHandleField;
    private static final List<Field> initialHandlerUUIDFields = new ArrayList<>();
    private static final Field nameField;
    private static final Field stateField;
    public static final Enum<?>[] stateEnums;
    private static final Method finishMethod;

    private static final Field unsafeField;

    static {
        try {
            initialHandlerClass = Class.forName("net.md_5.bungee.connection.InitialHandler");

            channelWrapperField = initialHandlerClass.getDeclaredField("ch");
            channelWrapperField.setAccessible(true);

            getHandleField = channelWrapperField.getType().getDeclaredMethod("getHandle");
            getHandleField.setAccessible(true);

            Arrays.stream(initialHandlerClass.getDeclaredFields())
                    .filter(field -> field.getType() == UUID.class)
                    .forEach(field -> {
                        field.setAccessible(true);
                        initialHandlerUUIDFields.add(field);
                    });

            nameField = initialHandlerClass.getDeclaredField("name");
            nameField.setAccessible(true);

            stateField = initialHandlerClass.getDeclaredField("thisState");
            stateField.setAccessible(true);

            finishMethod = initialHandlerClass.getDeclaredMethod("finish");
            finishMethod.setAccessible(true);



            Method method = stateField.getType().getMethod("values");
            method.setAccessible(true);
            stateEnums = (Enum<?>[]) method.invoke(null);

            unsafeField = initialHandlerClass.getDeclaredField("unsafe");
            unsafeField.setAccessible(true);
        } catch (ClassNotFoundException | NoSuchFieldException | NoSuchMethodException | InvocationTargetException |
                 IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
    private final Object initialHandler;
    public InitialHandler(PendingConnection pendingConnection) {
        initialHandler = pendingConnection;
    }

    public void setUUID(UUID uuid) {
        initialHandlerUUIDFields.forEach(field -> {
            try {
                field.set(initialHandler, uuid);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        });
    }

    public String getName() {
        try {
            return (String) nameField.get(initialHandler);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
    public void setName(String name) {
        try {
            nameField.set(initialHandler, name);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public void setState(Enum<?> state) {
        try {
            stateField.set(initialHandler, state);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public void finish() {
        try {
            finishMethod.invoke(initialHandler);
        } catch (InvocationTargetException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public ChannelWrapper channelWrapper() {
        try {
            return new ChannelWrapper(channelWrapperField.get(initialHandler));
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
    public void setUnsafe(Connection.Unsafe unsafe) {
        try {
            unsafeField.set(initialHandler, unsafe);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
