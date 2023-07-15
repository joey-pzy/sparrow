package ink.joey.web.config;

public class DataSourceContext {

    private static final ThreadLocal<String> CURRENT_DATA_SOURCE = new ThreadLocal<>();

    private DataSourceContext() {
    }

    public static String get() {
        return CURRENT_DATA_SOURCE.get();
    }

    public static void set(String dataSourceCode) {
        CURRENT_DATA_SOURCE.set(dataSourceCode);
    }

    public static void remove() {
        CURRENT_DATA_SOURCE.remove();
    }
}
