package de.otto.hmac.proxy;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;

public class CLIParameterToConfigurationReader {

    private static class InternalProxyConfiguration {
        @Parameter(names = { "-p", "--port" }, description = "Port of target server")
        int port;

        @Parameter(names = { "-h", "--host" }, description = "Hostname of target server")
        String targetHost;

        @Parameter(names = { "-u", "--user" }, description = "User that is allowed to set stuff on target server")
        String user;

        @Parameter(names = { "-s", "--secret" }, description = "The secret that identifies the user on the target server")
        String password;

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
    }

}
