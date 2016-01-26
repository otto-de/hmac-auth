/**
 * HMAC filter and interceptors for Jersey2 clients. Register a HMAC client request filter and writer interceptor combination
 * with the HmacJerseyHelper for example:
 * <code>
 * Client client = ClientBuilder.newBuilder().build();
 * HmacJerseyHelper.registerHmacFilter(client, "user", "hmacSecret");
 * </code>
 */
package de.otto.hmac.authentication.jersey2.filter;
