package com.osmantusx.event;

import com.osmantusx.OsmanTusX;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * A small, reflection-based publish/subscribe event bus.
 *
 * <p>Listeners register themselves (typically a module) and expose one or more
 * {@link EventHandler}-annotated methods. Registrations are cached per class so
 * repeated enable/disable cycles do not repeatedly reflect over the same type.</p>
 */
public final class EventBus {

    /** A single resolved listener method bound to an owner instance. */
    private record Subscription(Object owner, Method method, int priority) {
        void invoke(Event event) {
            try {
                method.invoke(owner, event);
            } catch (ReflectiveOperationException e) {
                OsmanTusX.LOGGER.error("Failed to dispatch {} to {}",
                        event.getClass().getSimpleName(), owner.getClass().getSimpleName(), e);
            }
        }
    }

    private final Map<Class<?>, CopyOnWriteArrayList<Subscription>> subscriptions = new ConcurrentHashMap<>();
    private final Map<Class<?>, List<Method>> handlerCache = new ConcurrentHashMap<>();

    /** Registers all {@link EventHandler} methods declared on {@code listener}. */
    public void register(Object listener) {
        for (Method method : resolveHandlers(listener.getClass())) {
            Class<?> eventType = method.getParameterTypes()[0];
            Subscription subscription = new Subscription(listener, method,
                    method.getAnnotation(EventHandler.class).priority());
            subscriptions.computeIfAbsent(eventType, key -> new CopyOnWriteArrayList<>()).add(subscription);
            subscriptions.get(eventType).sort(Comparator.comparingInt(Subscription::priority).reversed());
        }
    }

    /** Removes every subscription previously created for {@code listener}. */
    public void unregister(Object listener) {
        for (CopyOnWriteArrayList<Subscription> list : subscriptions.values()) {
            list.removeIf(subscription -> subscription.owner() == listener);
        }
    }

    /**
     * Dispatches {@code event} to all registered listeners in priority order.
     *
     * @return the same event instance for convenient inline cancellation checks.
     */
    public <T extends Event> T post(T event) {
        CopyOnWriteArrayList<Subscription> list = subscriptions.get(event.getClass());
        if (list == null) {
            return event;
        }
        for (Subscription subscription : list) {
            subscription.invoke(event);
        }
        return event;
    }

    private List<Method> resolveHandlers(Class<?> type) {
        return handlerCache.computeIfAbsent(type, key -> {
            List<Method> methods = new ArrayList<>();
            // Walk the whole hierarchy so handlers declared on a base class (e.g.
            // the shared HUD render method on HudModule) are picked up too. The
            // subclass is visited first, so an overridden handler shadows the
            // inherited one and is not registered twice.
            Set<String> seen = new HashSet<>();
            for (Class<?> current = key; current != null && current != Object.class;
                 current = current.getSuperclass()) {
                for (Method method : current.getDeclaredMethods()) {
                    if (!method.isAnnotationPresent(EventHandler.class)) {
                        continue;
                    }
                    if (!seen.add(method.getName() + Arrays.toString(method.getParameterTypes()))) {
                        continue;
                    }
                    if (method.getParameterCount() != 1 || !Event.class.isAssignableFrom(method.getParameterTypes()[0])) {
                        OsmanTusX.LOGGER.warn("Ignoring invalid @EventHandler {}#{}", current.getName(), method.getName());
                        continue;
                    }
                    if (Modifier.isStatic(method.getModifiers())) {
                        OsmanTusX.LOGGER.warn("Ignoring static @EventHandler {}#{}", current.getName(), method.getName());
                        continue;
                    }
                    method.setAccessible(true);
                    methods.add(method);
                }
            }
            return methods;
        });
    }
}
