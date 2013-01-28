package de.otto.hmac.proxy;

import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.client.apache.config.DefaultApacheHttpClientConfig;
import de.otto.hmac.authentication.HMACJerseyClient;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;
import java.net.URI;

@Path("/")
public class ProxyResource {

    private HMACJerseyClient client;

    public ProxyResource() {
        client = HMACJerseyClient.create(new DefaultApacheHttpClientConfig());
    }

    @Path("{resource:.*}")
    @GET
    public String getRequest(@Context UriInfo uriInfo, @Context Request request) {
        return createBuilder(uriInfo, request.getMethod()).get(String.class);
    }

    @Path("{resource:.*}")
    @POST
    public String postRequest(String body, @Context UriInfo uriInfo, @Context Request request) {
        return createBuilder(uriInfo, body, request.getMethod()).post(String.class);
    }

    @Path("{resource:.*}")
    @PUT
    public String putRequest(String body, @Context UriInfo uriInfo, @Context Request request) {
        return createBuilder(uriInfo, body, request.getMethod()).put(String.class);
    }


    @Path("{resource:.*}")
    @DELETE
    public String deleteRequest(@Context UriInfo uriInfo, @Context Request request) {
        return createBuilder(uriInfo, request.getMethod()).delete(String.class);
    }

    @Path("{resource:.*}")
    @HEAD
    public ClientResponse headRequest(@Context UriInfo uriInfo, @Context Request request) {
        return createBuilder(uriInfo, request.getMethod()).head();
    }

    @Path("{resource:.*}")
    @OPTIONS
    public String optionsRequest(@Context UriInfo uriInfo, @Context Request request) {
        return createBuilder(uriInfo, request.getMethod()).options(String.class);
    }


    private WebResource.Builder createBuilder(UriInfo uriInfo, String body, String method) {
        URI targetUri = withTargetHostAndPort(uriInfo.getRequestUriBuilder());
        return webResourceWithAuth(body, method, targetUri);
    }

    protected WebResource.Builder createBuilder(UriInfo uriInfo, String method) {
        return createBuilder(uriInfo, "", method);
    }


    protected WebResource.Builder webResourceWithAuth(String body, String method, URI targetUri) {
        WebResource.Builder builder = client
                .withMethod(method)
                .withUri(targetUri.getPath())
                .withBody(body)
                .auth(ProxyConfiguration.getUser(), ProxyConfiguration.getPassword())
                .authenticatedResource(targetUri.toString());

        return builder;
    }

    protected URI withTargetHostAndPort(UriBuilder uriBuilder) {
        uriBuilder.host(ProxyConfiguration.getTargetHost());
        uriBuilder.port(ProxyConfiguration.getPort());
        return uriBuilder.build();
    }


}
