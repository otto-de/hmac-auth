package de.otto.hmac.authentication.jersey2.test;

import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.grizzly2.servlet.GrizzlyWebContainerFactory;
import org.glassfish.jersey.test.DeploymentContext;
import org.glassfish.jersey.test.JerseyTest;
import org.glassfish.jersey.test.spi.TestContainer;
import org.glassfish.jersey.test.spi.TestContainerException;
import org.glassfish.jersey.test.spi.TestContainerFactory;

import jakarta.ws.rs.ProcessingException;
import java.io.IOException;
import java.net.URI;
import java.util.Collections;

/**
 * JerseyTest class extended for tests with suppport of
 * <code>
 *
 * @Context private HttpServletRequest request;
 * </code>
 * <p>
 * attributes.
 */
public class JerseyServletTest extends JerseyTest {

    @Override
    protected TestContainerFactory getTestContainerFactory() throws TestContainerException {
        return new TestContainerFactory() {
            @Override
            public TestContainer create(final URI baseUri, final DeploymentContext application) throws IllegalArgumentException {
                return new TestContainer() {
                    private HttpServer server;

                    @Override
                    public ClientConfig getClientConfig() {
                        return null;
                    }

                    @Override
                    public URI getBaseUri() {
                        return baseUri;
                    }

                    @Override
                    public void start() {
                        try {
                            this.server = GrizzlyWebContainerFactory.create(
                                    baseUri,
                                    Collections.singletonMap("jersey.config.server.provider.packages", "de.otto.hmac.authentication.jersey2.filter")
                            );
                        } catch (ProcessingException | IOException e) {
                            throw new TestContainerException(e);
                        }
                    }

                    @Override
                    public void stop() {
                        this.server.shutdownNow();
                    }
                };

            }
        };
    }
}
