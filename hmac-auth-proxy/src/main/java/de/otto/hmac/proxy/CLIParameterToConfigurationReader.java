package de.otto.hmac.proxy;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;

public class CLIParameterToConfigurationReader {

    private static class InternalProxyConfiguration {
        @Parameter(names = {"-tp", "--targetPort"}, description = "Port of target server")
        int port = 80;

        @Parameter(names = {"-h", "--host"}, description = "Hostname of target server")
        String targetHost = "develop.lhotse.ov.otto.de";

        @Parameter(names = {"-u", "--user"}, description = "User that is allowed to set stuff on target server")
        String user = "";

        @Parameter(names = {"-p", "--password"}, description = "The password that identifies the user on the target server")
        String password = "";

        @Parameter(names = {"-v", "--verbose"}, description = "Enable logging.")
        boolean verbose = false;

        @Parameter(names = "--help", help = true)
        private boolean help;
    }

    public static void toConfiguration(String[] strings) {

        InternalProxyConfiguration internalProxyConfiguration = new InternalProxyConfiguration();

        new JCommander(internalProxyConfiguration, strings);

        ProxyConfiguration.setTargetHost(internalProxyConfiguration.targetHost);
        ProxyConfiguration.setPort(internalProxyConfiguration.port);
        ProxyConfiguration.setUser(internalProxyConfiguration.user);
        ProxyConfiguration.setPassword(internalProxyConfiguration.password);
        ProxyConfiguration.setHelp(internalProxyConfiguration.help);
        ProxyConfiguration.setVerbose(internalProxyConfiguration.verbose);
    }

}
