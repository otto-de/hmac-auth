package de.otto.hmac.authentication.jersey;

import com.google.common.io.ByteSource;
import org.testng.Assert;
import org.testng.annotations.Test;

public class HMACJerseyClientTest {

    @Test
    public void shouldFailOnGetWithoutUser() throws Exception {
        final HMACJerseyClient client = HMACJerseyClient.create();
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
    public void shouldFailOnGetWithoutSecret() throws Exception {
        final HMACJerseyClient client = HMACJerseyClient.create();
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
    public void shouldFailOnGetWithoutUri() throws Exception {
        final HMACJerseyClient client = HMACJerseyClient.create();
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
    public void shouldFailWithoutMethod() throws Exception {
        final HMACJerseyClient client = HMACJerseyClient.create();
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
    public void shouldGetSuccessfulWithAllInformation() throws Exception {
        final HMACJerseyClient client = HMACJerseyClient.create();
        client.withMethod("GET").withUri("testUri").auth("user", "secret").authenticatedResource("/targetUri");
    }

    @Test
    public void shouldPutSuccessfulWithAllInformation() throws Exception {
        final HMACJerseyClient client = HMACJerseyClient.create();
        client.withMethod("PUT").withUri("testUri").withBody(ByteSource.wrap("body".getBytes())).auth("user", "secret").authenticatedResource("/targetUri");
    }

    @Test
    public void shouldPostSuccessfulWithAllInformation() throws Exception {
        final HMACJerseyClient client = HMACJerseyClient.create();
        client.withMethod("POST").withUri("testUri").withBody(ByteSource.wrap("body".getBytes())).auth("user", "secret").authenticatedResource("/targetUri");
    }

    @Test
    public void shouldDeleteSuccessfulWithAllInformation() throws Exception {
        final HMACJerseyClient client = HMACJerseyClient.create();
        client.withMethod("DELETE").withUri("testUri").auth("user", "secret").authenticatedResource("/targetUri");
    }
}