package me.autobot.playerdoll.netty.protocol;


import io.netty.buffer.ByteBuf;
import me.autobot.playerdoll.util.ReflectionUtil;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;

public abstract class Post1_20_R4_Protocols {
    public static final Object CLIENT_LOGIN_PROTOCOL;
    public static final Object CLIENT_CONFIGURATION_PROTOCOL;
    public static final Object CLIENT_PLAY_PROTOCOL;

    public static final Object SERVER_LOGIN_PROTOCOL;
    public static final Object SERVER_CONFIGURATION_PROTOCOL;
    public static final Object SERVER_PLAY_PROTOCOL;


    private static final Method codecMethod;
    private static final Method decodeMethod;
    private static final Method encodeMethod;

    static {
        final String s = "net.minecraft.network.protocol.";

        Class<?> protocolInfoClass = ReflectionUtil.getClass("net.minecraft.network.ProtocolInfo");
        Objects.requireNonNull(protocolInfoClass, "ProtocolInfo.class");

        codecMethod = Arrays.stream(protocolInfoClass.getDeclaredMethods())
                .filter(method -> {
                    if (method.getGenericReturnType() instanceof ParameterizedType type) {
                        Type[] types = type.getActualTypeArguments();
                        return types.length == 2 && types[0] == ByteBuf.class;
                    }
                    return false;
                })
                .findFirst().orElseThrow();
        codecMethod.setAccessible(true);

        Class<?> streamDecoderClass = ReflectionUtil.getClass("net.minecraft.network.codec.StreamDecoder");
        Objects.requireNonNull(streamDecoderClass, "StreamDecoder.class");

        decodeMethod = streamDecoderClass.getMethods()[0];
        decodeMethod.setAccessible(true);

        Class<?> streamEncoderClass = ReflectionUtil.getClass("net.minecraft.network.codec.StreamEncoder");
        Objects.requireNonNull(streamEncoderClass, "StreamEncoder.class");

        encodeMethod = streamEncoderClass.getMethods()[0];
        encodeMethod.setAccessible(true);

        Class<?> loginProtocolsClass = ReflectionUtil.getClass(s + "login.LoginProtocols");
        Class<?> configProtocolsClass = ReflectionUtil.getClass(s + "configuration.ConfigurationProtocols");
        Class<?> playProtocolsClass = ReflectionUtil.getClass(s + "game.GameProtocols");

        Objects.requireNonNull(loginProtocolsClass, "LoginProtocols.class");
        Objects.requireNonNull(configProtocolsClass, "ConfigurationProtocols.class");
        Objects.requireNonNull(playProtocolsClass, "GameProtocols.class");


        Class<?> clientPacketListenerClass = ReflectionUtil.getClass("net.minecraft.network.ClientboundPacketListener");
        Objects.requireNonNull(clientPacketListenerClass, "ClientboundPacketListener.class");


        Class<?> serverPacketListenerClass = ReflectionUtil.getClass("net.minecraft.network.protocol.game.ServerPacketListener");
        Objects.requireNonNull(serverPacketListenerClass, "ServerPacketListener.class");


        BiFunction<Class<?>, Boolean, Field> getProtocol = (c, b) -> {
            Field field = Arrays.stream(c.getDeclaredFields()).filter(f -> {
                        if (f.getGenericType() instanceof ParameterizedType type) {
                            Type[] types = type.getActualTypeArguments();
                            Class<?> bound = b ? clientPacketListenerClass : serverPacketListenerClass;
                            return types.length == 1 && bound.isAssignableFrom((Class<?>) types[0]);
                        }
                        return false;
                    })
                    .findFirst()
                    .orElseThrow();
            field.setAccessible(true);
            return field;
        };

        CLIENT_LOGIN_PROTOCOL = ReflectionUtil.getField(getProtocol.apply(loginProtocolsClass, true), null);
        CLIENT_CONFIGURATION_PROTOCOL = ReflectionUtil.getField(getProtocol.apply(configProtocolsClass, true), null);
        SERVER_LOGIN_PROTOCOL = ReflectionUtil.getField(getProtocol.apply(loginProtocolsClass, false), null);
        SERVER_CONFIGURATION_PROTOCOL = ReflectionUtil.getField(getProtocol.apply(configProtocolsClass, false), null);


        Class<?> registryFrozenClass = ReflectionUtil.getClass("net.minecraft.core.IRegistryCustom$Dimension");
        Method registryFrozenMethod = Arrays.stream(ReflectionUtil.getDedicatedServerClass().getSuperclass().getDeclaredMethods())
                .filter(method -> method.getReturnType() == registryFrozenClass)
                .findFirst().orElseThrow();
        registryFrozenMethod.setAccessible(true);
        Object registryFrozen = ReflectionUtil.invokeMethod(registryFrozenMethod, ReflectionUtil.getDedicatedServerInstance());

        BiFunction<Class<?>, Boolean, Field> getGameProtocolTemplate = (c, b) -> {
            Field field = Arrays.stream(c.getDeclaredFields()).filter(f -> {
                        if (f.getGenericType() instanceof ParameterizedType type) {
                            Type[] types = type.getActualTypeArguments();
                            Class<?> bound = b ? clientPacketListenerClass : serverPacketListenerClass;
                            return bound.isAssignableFrom((Class<?>) types[0]);
                        }
                        return false;
                    })
                    .findFirst()
                    .orElseThrow();
            field.setAccessible(true);
            return field;
        };

        Class<?> protocolInfoUnboundClass = ReflectionUtil.getClass("net.minecraft.network.ProtocolInfo$a");
        Objects.requireNonNull(protocolInfoUnboundClass, "ProtocolInfo$Unbound.class");
        Method unboundBindMethod = Arrays.stream(protocolInfoUnboundClass.getDeclaredMethods())
                .filter(method -> method.getReturnType() == protocolInfoClass)
                .findFirst()
                .orElseThrow();
        unboundBindMethod.setAccessible(true);

        Class<?> registryFriendlyByteBufClass = ReflectionUtil.getClass("net.minecraft.network.RegistryFriendlyByteBuf");
        Objects.requireNonNull(registryFriendlyByteBufClass, "RegistryFriendlyByteBuf.class");
        Method decoratorMethod = Arrays.stream(registryFriendlyByteBufClass.getDeclaredMethods())
                .filter(method -> method.getReturnType() == Function.class)
                .findFirst().orElseThrow();
        decoratorMethod.setAccessible(true);

        Object clientPlayProtocolTemplate = ReflectionUtil.getField(getGameProtocolTemplate.apply(playProtocolsClass, true), null);
        Object serverPlayProtocolTemplate = ReflectionUtil.getField(getGameProtocolTemplate.apply(playProtocolsClass, false), null);
        Object decoratorFunction = ReflectionUtil.invokeMethod(decoratorMethod, null, registryFrozen);

        CLIENT_PLAY_PROTOCOL = ReflectionUtil.invokeMethod(unboundBindMethod, clientPlayProtocolTemplate, decoratorFunction);
        SERVER_PLAY_PROTOCOL = ReflectionUtil.invokeMethod(unboundBindMethod, serverPlayProtocolTemplate, decoratorFunction);
    }


    public static Object getCodec(Object protocol) {
        return ReflectionUtil.invokeMethod(codecMethod, protocol);
    }

    public static Object getDecode(Object codec, ByteBuf byteBuf) {
        return ReflectionUtil.invokeMethod(decodeMethod, codec, byteBuf);
    }
    public static void getEncode(Object codec, ByteBuf byteBuf, Object packet) {
        ReflectionUtil.invokeMethod(encodeMethod, codec, byteBuf, packet);
    }
}
