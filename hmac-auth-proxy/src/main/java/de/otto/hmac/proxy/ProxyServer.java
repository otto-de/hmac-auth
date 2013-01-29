package de.otto.hmac.proxy;

import com.sun.jersey.api.container.grizzly2.GrizzlyServerFactory;
import com.sun.jersey.api.core.PackagesResourceConfig;
import com.sun.jersey.api.core.ResourceConfig;
import org.glassfish.grizzly.http.server.HttpServer;

import javax.ws.rs.core.UriBuilder;
import java.io.IOException;
import java.net.URI;
import java.util.logging.Level;
import java.util.logging.Logger;

import static de.otto.hmac.proxy.CLIParameterToConfigurationReader.toConfiguration;

public class ProxyServer {

    private static URI getBaseURI() {
        return UriBuilder.fromUri("http://localhost/").port(9998).build();
    }

    public static final URI BASE_URI = getBaseURI();

    protected static HttpServer startServer() throws IOException {
        ResourceConfig rc = new PackagesResourceConfig("de.otto.hmac.proxy");
        return GrizzlyServerFactory.createHttpServer(BASE_URI, rc);
    }

    public static void main(String[] args) throws IOException {
        toConfiguration(args);

        if (!ProxyConfiguration.isVerbose()) {
            Logger.getLogger("").setLevel(Level.OFF);
        }


        if (ProxyConfiguration.isHelp()) {
            System.out.println("A local proxy server that forwards all requests to the given target host and port, \n" +
                    "but with hmac-authentication headers appended.\n" +
                    "USAGE:\n" +
                    "    -u --user USER             authenticated username\n" +
                    "    -p --password PASSWORD     password of user\n" +
                    "    -h --host HOST             target host\n" +
                    "    -tp --targetPort PORT      target port\n" +
                    "    -v --verbose               display more stuff\n"
            );
            return;
        }

        HttpServer httpServer = startServer();


        System.out.println("HMAC-Proxy listens to [localhost:9998]");
        System.out.println(String.format("HMAC-Proxy forwards to [%s:%d]", ProxyConfiguration.getTargetHost(), ProxyConfiguration.getPort()));
        System.out.println(String.format("As user [%s]", ProxyConfiguration.getUser()));
        System.out.println("Hit enter to stop proxy...");
        System.in.read();
        httpServer.stop();
    }
}