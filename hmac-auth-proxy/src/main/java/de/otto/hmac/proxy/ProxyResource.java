package de.otto.hmac.proxy;

import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.client.apache.config.DefaultApacheHttpClientConfig;
import de.otto.hmac.authentication.HMACJerseyClient;

import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.net.URI;
import java.util.List;
import java.util.Map;

@Path("/")
public class ProxyResource {

    private HMACJerseyClient client;

    public ProxyResource() {
        client = HMACJerseyClient.create(new DefaultApacheHttpClientConfig());
    }

    @Path("{resource:.*}")
    @GET
    public Response getRequest(@Context UriInfo uriInfo, @Context Request request, @Context HttpHeaders headers) {
        ClientResponse clientResponse = createBuilder(uriInfo, request.getMethod(), headers).get(ClientResponse.class);
        return clientResponseToResponse(clientResponse);
    }

    @Path("{resource:.*}")
    @POST
    public Response postRequest(String body, @Context UriInfo uriInfo, @Context Request request, @Context HttpHeaders headers) {
        ClientResponse clientResponse = createBuilder(uriInfo, body, request.getMethod(), headers).post(ClientResponse.class, body);
        return clientResponseToResponse(clientResponse);
    }

    @Path("{resource:.*}")
    @PUT
    public Response putRequest(String body, @Context UriInfo uriInfo, @Context Request request, @Context HttpHeaders headers) {

        ClientResponse clientResponse = createBuilder(uriInfo, body, request.getMethod(), headers).put(ClientResponse.class, body);
        return clientResponseToResponse(clientResponse);
    }

    @Path("{resource:.*}")
    @DELETE
    public Response deleteRequest(@Context UriInfo uriInfo, @Context Request request, @Context HttpHeaders headers) {
        ClientResponse clientResponse = createBuilder(uriInfo, request.getMethod(), headers).delete(ClientResponse.class);
        return clientResponseToResponse(clientResponse);
    }

    @Path("{resource:.*}")
    @HEAD
    public Response headRequest(@Context UriInfo uriInfo, @Context Request request, @Context HttpHeaders headers) {
        ClientResponse clientResponse = createBuilder(uriInfo, request.getMethod(), headers).head();
        return clientResponseToResponse(clientResponse);
    }


    @Path("{resource:.*}")
    @OPTIONS
    public Response optionsRequest(@Context UriInfo uriInfo, @Context Request request, @Context HttpHeaders headers) {
        ClientResponse clientResponse = createBuilder(uriInfo, request.getMethod(), headers).options(ClientResponse.class);
        return clientResponseToResponse(clientResponse);
    }

    private static Response clientResponseToResponse(ClientResponse r) {
        Response.ResponseBuilder rb = Response.status(r.getStatus());

        copyResponseHeaders(r, rb);

        String content = r.getStatus() != 204 ? r.getEntity(String.class) : "";

        System.out.println(String.format("Retrieved answer: HTTP-Code [%d]\nContent: \n%s\n\n", r.getStatus(), content));

        rb.entity(content);

        return rb.build();
    }

    private static void copyResponseHeaders(ClientResponse r, Response.ResponseBuilder rb) {
        for (Map.Entry<String, List<String>> entry : r.getHeaders().entrySet()) {
            for (String value : entry.getValue()) {
                rb.header(entry.getKey(), value);
            }
        }
    }

    private static void copyRequestHeaders(HttpHeaders headers, WebResource.Builder builder) {
        if (headers == null) {
            return;
        }

        for (Map.Entry<String, List<String>> entry : headers.getRequestHeaders().entrySet()) {
            builder.header(entry.getKey(), entry.getValue().get(0));
        }
    }


    private WebResource.Builder createBuilder(UriInfo uriInfo, String body, String method, HttpHeaders headers) {
        URI targetUri = withTargetHostAndPort(uriInfo.getRequestUriBuilder());
        System.out.println("Sending request to " + targetUri);
        WebResource.Builder builder = webResourceWithAuth(body, method, targetUri);

        copyRequestHeaders(headers, builder);

        return builder;
    }

    protected WebResource.Builder createBuilder(UriInfo uriInfo, String method, HttpHeaders headers) {
        return createBuilder(uriInfo, "", method, headers);
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
