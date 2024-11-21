package me.autobot.playerdoll.api.wrapper;

import me.autobot.playerdoll.api.PlayerDollAPI;
import me.autobot.playerdoll.api.ReflectionUtil;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.logging.Level;

public class WrapperRegistry {
    private static final Set<Class<? extends IWrapper<?>>> WRAPPERS = new HashSet<>();


    public static void put(Class<? extends IWrapper<?>> wrapper) {
        if (WRAPPERS.contains(wrapper)) {
            PlayerDollAPI.getLogger().log(Level.INFO, "Ignore Duplicated Wrapper class [{0}].", wrapper);
            return;
        }
        Wrapper annotation = wrapper.getAnnotation(Wrapper.class);
        Objects.requireNonNull(annotation, "Wrapper annotation is missing.");
        WRAPPERS.add(wrapper);
    }

    public static void remove(Class<? extends IWrapper<?>> wrapper) {
        WRAPPERS.remove(wrapper);
    }

    public static <T extends IWrapper<U>, U> Class<? extends T> getWrapper(Class<T> clazz, U wrappingInstance) {
        return getWrapper("", clazz, wrappingInstance);
    }

    @SuppressWarnings("unchecked")
    public static <T extends IWrapper<U>, U> Class<? extends T> getWrapper(String name, Class<T> clazz, U wrappingInstance) {
        return (Class<? extends T>) WRAPPERS.stream().filter(c -> {
                    Wrapper wrapper = c.getAnnotation(Wrapper.class);
                    // Check has superclass first
                    Class<?> wrappingClass = wrapper.wrapping();
                    boolean wrappingCheck;
                    if (wrappingClass.isInterface() || Modifier.isAbstract(wrappingClass.getModifiers())) {
                        wrappingCheck = wrappingClass.isInstance(wrappingInstance);
                    } else {
                        wrappingCheck = wrappingClass.isAssignableFrom(wrappingInstance.getClass());
                    }
                    return wrappingCheck && clazz.isAssignableFrom(c) && wrapper.name().equals(name);
                }).findAny().orElseThrow();
    }

    public static <T extends IWrapper<U>, U> T wrapFrom(Class<? extends T> wrapperClass, U args) {
        Wrapper wrapper = wrapperClass.getAnnotation(Wrapper.class);
        Method method;
        try {
            method = wrapperClass.getDeclaredMethod(wrapper.method(), wrapper.wrapping());
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
        Objects.requireNonNull(method, "Method with provided argument not found.");
        method.setAccessible(true);
        if (!Modifier.isStatic(method.getModifiers())) {
            throw new IllegalStateException("Expected static method");
        }
        return ReflectionUtil.invokeStaticMethod(wrapperClass, method, args);
    }

    public static <T> T getInstance(IWrapper<T> wrapperInstance) {
        return wrapperInstance.getInstance();
    }

    public static <T> T getInstance(IWrapper<?> wrapperInstance, Class<T> baseClass) {
        return baseClass.cast(getInstance(wrapperInstance));
    }
}
