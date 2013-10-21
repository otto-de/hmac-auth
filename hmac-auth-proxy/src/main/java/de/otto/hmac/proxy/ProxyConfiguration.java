package de.otto.hmac.proxy;


public class ProxyConfiguration {

    private static int port;
    private static String targetHost;
    private static String user;
    private static String password;
    private static boolean help;
    private static boolean verbose;
    private static boolean daemon;

    public static int getPort() {
        return port;
    }

    public static void setPort(int port) {
        ProxyConfiguration.port = port;
    }

    public static String getTargetHost() {
        return targetHost;
    }

    public static void setTargetHost(String targetHost) {
        ProxyConfiguration.targetHost = targetHost;
    }

    public static String getUser() {
        return user;
    }

    public static void setUser(String user) {
        ProxyConfiguration.user = user;
    }

    public static String getPassword() {
        return password;
    }

    public static void setPassword(String password) {
        ProxyConfiguration.password = password;
    }

    public static boolean isHelp() {
        return help;
    }

    public static void setHelp(boolean help) {
        ProxyConfiguration.help = help;
    }

    public static void setVerbose(boolean verbose) {
        ProxyConfiguration.verbose = verbose;
    }

    public static boolean isVerbose() {
        return verbose;
    }

    public static boolean isDaemon() {
        return daemon;
    }

    public static void setDaemon(boolean daemon) {
        ProxyConfiguration.daemon = daemon;
    }
}
