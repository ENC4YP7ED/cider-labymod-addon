package net.labymod.addons.cider.core.api;

/**
 * Factory for creating CiderAPI instances
 */
public class CiderAPIFactory {

    /**
     * Create a new CiderAPI instance with default settings
     */
    public static CiderAPI create() {
        return create(null, null);
    }

    /**
     * Create a new CiderAPI instance with custom URL and token
     */
    public static CiderAPI create(String apiUrl, String appToken) {
        return new CiderAPI(apiUrl, appToken);
    }
}
