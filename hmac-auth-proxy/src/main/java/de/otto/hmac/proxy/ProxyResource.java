package de.otto.hmac.proxy;

import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.client.apache.config.DefaultApacheHttpClientConfig;
import de.otto.hmac.authentication.HMACJerseyClient;
import org.glassfish.grizzly.http.util.HttpStatus;

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
    public Response getRequest(@Context UriInfo uriInfo, @Context Request request) {
        ClientResponse clientResponse = createBuilder(uriInfo, request.getMethod()).get(ClientResponse.class);
        return clientResponseToResponse(clientResponse);
    }

    @Path("{resource:.*}")
    @POST
    public Response postRequest(String body, @Context UriInfo uriInfo, @Context Request request) {
        ClientResponse clientResponse = createBuilder(uriInfo, body, request.getMethod()).post(ClientResponse.class);
        return clientResponseToResponse(clientResponse);
    }

    @Path("{resource:.*}")
    @PUT
    public Response putRequest(String body, @Context UriInfo uriInfo, @Context Request request) {
        ClientResponse clientResponse = createBuilder(uriInfo, body, request.getMethod()).put(ClientResponse.class);
        return clientResponseToResponse(clientResponse);
    }

    @Path("{resource:.*}")
    @DELETE
    public Response deleteRequest(@Context UriInfo uriInfo, @Context Request request) {
        ClientResponse clientResponse = createBuilder(uriInfo, request.getMethod()).delete(ClientResponse.class);
        return clientResponseToResponse(clientResponse);
    }


    @Path("{resource:.*}")
    @HEAD
    public Response headRequest(@Context UriInfo uriInfo, @Context Request request) {
        ClientResponse clientResponse = createBuilder(uriInfo, request.getMethod()).head();
        return clientResponseToResponse(clientResponse);
    }

    @Path("{resource:.*}")
    @OPTIONS
    public Response optionsRequest(@Context UriInfo uriInfo, @Context Request request) {
        ClientResponse clientResponse = createBuilder(uriInfo, request.getMethod()).options(ClientResponse.class);
        return clientResponseToResponse(clientResponse);
    }

    private static Response clientResponseToResponse(ClientResponse r) {
        Response.ResponseBuilder rb = Response.status(r.getStatus());
        
        copyAllHeaders(r, rb);

        String content = r.getEntity(String.class);

        System.out.println(String.format("Retrieved answer: HTTP-Code [%d]\nContent: \n%s\n\n", r.getStatus(), content));
        
        rb.entity(content);

        return rb.build();
    }

    private static void copyAllHeaders(ClientResponse r, Response.ResponseBuilder rb) {
        for (Map.Entry<String, List<String>> entry : r.getHeaders().entrySet()) {
            for (String value : entry.getValue()) {
                rb.header(entry.getKey(), value);
            }
        }
    }


    private WebResource.Builder createBuilder(UriInfo uriInfo, String body, String method) {
        URI targetUri = withTargetHostAndPort(uriInfo.getRequestUriBuilder());
        System.out.println("Sending request to " + targetUri);
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
