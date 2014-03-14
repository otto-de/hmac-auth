package de.otto.hmac.authentication.jersey;

import com.sun.jersey.client.apache.config.DefaultApacheHttpClientConfig;
import org.testng.Assert;
import org.testng.annotations.Test;

public class HMACJerseyClientTest {

    @Test
    public void shouldFailOnGetWithoutUser() {
        final HMACJerseyClient client = HMACJerseyClient.create(new DefaultApacheHttpClientConfig());
        try {
            client.withMethod("GET").withUri("testUri").auth(null, "key").authenticatedResource("/targetUri");
            Assert.fail("Exception expected");
        }
        catch (final IllegalArgumentException e) {
            // everything fine.
        }
        try {
            client.withMethod("GET").withUri("testUri").auth("", "key").authenticatedResource("/targetUri");
            Assert.fail("Exception expected");
        }
        catch (final IllegalArgumentException e) {
            // everything fine.
        }
    }

    @Test
    public void shouldFailOnGetWithoutSecret() {
        final HMACJerseyClient client = HMACJerseyClient.create(new DefaultApacheHttpClientConfig());
        try {
            client.withMethod("GET").withUri("testUri").auth("user", null).authenticatedResource("/targetUri");
            Assert.fail("Exception expected");
        }
        catch (final IllegalArgumentException e) {
            // everything fine.
        }
        try {
            client.withMethod("GET").withUri("testUri").auth("user", "").authenticatedResource("/targetUri");
            Assert.fail("Exception expected");
        }
        catch (final IllegalArgumentException e) {
            // everything fine.
        }
    }

    @Test
    public void shouldFailOnGetWithoutUri() {
        final HMACJerseyClient client = HMACJerseyClient.create(new DefaultApacheHttpClientConfig());
        try {
            client.withMethod("GET").auth("user", "secret").authenticatedResource("/targetUri");
            Assert.fail("Exception expected");
        }
        catch (final IllegalArgumentException e) {
            // everything fine.
        }
        try {
            client.withMethod("GET").withUri("").auth("user", "secret").authenticatedResource("/targetUri");
            Assert.fail("Exception expected");
        }
        catch (final IllegalArgumentException e) {
            // everything fine.
        }
    }

    @Test
    public void shouldFailWithoutMethod() {
        final HMACJerseyClient client = HMACJerseyClient.create(new DefaultApacheHttpClientConfig());
        try {
            client.withUri("testUri").auth("user", "secret").authenticatedResource("/targetUri");
            Assert.fail("Exception expected");
        }
        catch (final IllegalArgumentException e) {
            // everything fine.
        }
        try {
            client.withMethod("").withUri("testUri").auth("user", "secret").authenticatedResource("/targetUri");
            Assert.fail("Exception expected");
        }
        catch (final IllegalArgumentException e) {
            // everything fine.
        }
    }

    @Test
    public void shouldFailOnPutWithoutBody() {
        final HMACJerseyClient client = HMACJerseyClient.create(new DefaultApacheHttpClientConfig());
        try {
            client.withMethod("PUT").withUri("testUri").auth("user", "secret").authenticatedResource("/targetUri");
            Assert.fail("Exception expected");
        }
        catch (final IllegalArgumentException e) {
            // everything fine.
        }
        try {
            client.withMethod("PUT").withUri("testUri").withBody("").auth("user", "secret").authenticatedResource("/targetUri");
            Assert.fail("Exception expected");
        }
        catch (final IllegalArgumentException e) {
            // everything fine.
        }
    }

    @Test
    public void shouldGetSuccessfulWithAllInformation() {
        final HMACJerseyClient client = HMACJerseyClient.create(new DefaultApacheHttpClientConfig());
        client.withMethod("GET").withUri("testUri").auth("user", "secret").authenticatedResource("/targetUri");
    }

    @Test
    public void shouldPutSuccessfulWithAllInformation() {
        final HMACJerseyClient client = HMACJerseyClient.create(new DefaultApacheHttpClientConfig());
        client.withMethod("PUT").withUri("testUri").withBody("body").auth("user", "secret").authenticatedResource("/targetUri");
    }

    @Test
    public void shouldPostSuccessfulWithAllInformation() {
        final HMACJerseyClient client = HMACJerseyClient.create(new DefaultApacheHttpClientConfig());
        client.withMethod("POST").withUri("testUri").withBody("body").auth("user", "secret").authenticatedResource("/targetUri");
    }

    @Test
    public void shouldDeleteSuccessfulWithAllInformation() {
        final HMACJerseyClient client = HMACJerseyClient.create(new DefaultApacheHttpClientConfig());
        client.withMethod("DELETE").withUri("testUri").auth("user", "secret").authenticatedResource("/targetUri");
    }
}