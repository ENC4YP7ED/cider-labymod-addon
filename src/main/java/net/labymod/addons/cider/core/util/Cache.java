package net.labymod.addons.cider.core.util;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

/**
 * Simple cache with expiration
 */
public class Cache<T> {
    private final Map<String, CacheEntry<T>> cache = new HashMap<>();
    private final long expirationTime;
    private final Consumer<T> onExpire;

    public Cache(long expirationTime, Consumer<T> onExpire) {
        this.expirationTime = expirationTime;
        this.onExpire = onExpire;
    }

    public synchronized T get(String key) {
        CacheEntry<T> entry = cache.get(key);
        if (entry == null) {
            return null;
        }

        if (System.currentTimeMillis() - entry.timestamp > expirationTime) {
            cache.remove(key);
            if (onExpire != null) {
                onExpire.accept(entry.value);
            }
            return null;
        }

        return entry.value;
    }

    public synchronized void push(String key, T value) {
        cache.put(key, new CacheEntry<>(value, System.currentTimeMillis()));
    }

    public synchronized void clear() {
        if (onExpire != null) {
            cache.values().forEach(entry -> onExpire.accept(entry.value));
        }
        cache.clear();
    }

    private static class CacheEntry<T> {
        final T value;
        final long timestamp;

        CacheEntry(T value, long timestamp) {
            this.value = value;
            this.timestamp = timestamp;
        }
    }
}
