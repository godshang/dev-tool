package com.github.godshang.devtool.page;

import lombok.SneakyThrows;

import java.lang.ref.WeakReference;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class PageFactory {

    private static Map<Class, WeakReference<Object>> weakReferenceInstances = new ConcurrentHashMap<>();

    @SneakyThrows
    public static <T> T getWeakReferenceInstance(Class<T> clazz) {
        WeakReference<Object> reference = weakReferenceInstances.get(clazz);
        Object instance = reference == null ? null : reference.get();
        if (instance == null) {
            synchronized (PageFactory.class) {
                reference = weakReferenceInstances.get(clazz);
                instance = reference == null ? null : reference.get();
                if (instance == null) {
                    instance = clazz.getDeclaredConstructor().newInstance();
                    weakReferenceInstances.put(clazz, new WeakReference<>(instance));
                }
            }
        }
        return (T) instance;
    }

    public static <T> void removeWeakReferenceInstance(Class<T> clazz) {
        weakReferenceInstances.remove(clazz);
    }

}
