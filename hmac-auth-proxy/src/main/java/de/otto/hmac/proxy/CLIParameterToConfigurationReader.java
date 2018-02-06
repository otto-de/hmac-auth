package de.otto.hmac.proxy;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;

public class CLIParameterToConfigurationReader {

    private static class InternalProxyConfiguration {
        @Parameter(names = {"-tp", "--targetPort"}, description = "Port of target server")
        int port = 8080;

        @Parameter(names = {"-sp", "--sourcePort"}, description = "Port of proxy server")
        int sourcePort = 9998;

        @Parameter(names = {"-h", "--host"}, description = "Hostname of target server")
        String targetHost = "localhost";

        @Parameter(names = {"-u", "--user"}, description = "User that is allowed to set stuff on target server")
        String user = "";

        @Parameter(names = {"-p", "--password"}, description = "The password that identifies the user on the target server")
        String password = "";

        @Parameter(names = {"-v", "--verbose"}, description = "Enable logging.")
        boolean verbose = false;

        @Parameter(names = {"-s", "--secure"}, description = "Use secure (HTTPS).")
        boolean secure  = false;

        @Parameter(names = "--help", help = true)
        private boolean help;

        @Parameter(names = {"-d", "--daemon"}, description = "Enable daemon mode")
        private boolean useAsDaemon = false;
    }

    public static void toConfiguration(String[] strings) {

        InternalProxyConfiguration internalProxyConfiguration = new InternalProxyConfiguration();

        new JCommander(internalProxyConfiguration, strings);

        ProxyConfiguration.setTargetHost(internalProxyConfiguration.targetHost);
        ProxyConfiguration.setPort(internalProxyConfiguration.port);
        ProxyConfiguration.setSecure(internalProxyConfiguration.secure);
        ProxyConfiguration.setSourcePort(internalProxyConfiguration.sourcePort);
        ProxyConfiguration.setUser(internalProxyConfiguration.user);
        ProxyConfiguration.setPassword(internalProxyConfiguration.password);
        ProxyConfiguration.setHelp(internalProxyConfiguration.help);
        ProxyConfiguration.setVerbose(internalProxyConfiguration.verbose);
        ProxyConfiguration.setDaemon(internalProxyConfiguration.useAsDaemon);
    }

}
