package de.otto.hmac.proxy;

import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.client.apache.config.DefaultApacheHttpClientConfig;
import de.otto.hmac.authentication.HMACJerseyClient;

import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Path("/")
public class ProxyResource {

    public static final String ACCEPT_ENCODING = "accept-encoding";

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
        ClientResponse clientResponse = createBuilder(uriInfo, request.getMethod(), headers, HttpHeaders.CONTENT_LENGTH).delete(ClientResponse.class);
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

    private static Response clientResponseToResponse(ClientResponse clientResponse) {
        Response.ResponseBuilder rb = Response.status(clientResponse.getStatus());
        copyResponseHeaders(clientResponse, rb);
        String content = clientResponse.getStatus() != 204 ? clientResponse.getEntity(String.class) : null;
        System.out.println(String.format("Retrieved answer: HTTP-Code [%d]\nContent: \n%s\n\n", clientResponse.getStatus(), content));
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

    private static void copyRequestHeaders(HttpHeaders headers, WebResource.Builder builder, List<String> ignoreHeaders) {
        if (headers == null) {
            return;
        }

        for (Map.Entry<String, List<String>> entry : headers.getRequestHeaders().entrySet()) {
            if (ignoreHeaders.contains(entry.getKey().toLowerCase())) {
                continue;
            }
            for (String value : entry.getValue()) {
                builder.header(entry.getKey(), value);
            }
        }
    }


    private WebResource.Builder createBuilder(UriInfo uriInfo, String body, String method, HttpHeaders headers, String... ignoreHeaders) {
        URI targetUri = withTargetHostAndPort(uriInfo.getRequestUriBuilder());
        System.out.println("Sending request to " + targetUri);
        WebResource.Builder builder = webResourceWithAuth(body, method, targetUri);

        ArrayList<String> allIgnoreHeaders = of(ignoreHeaders);
        allIgnoreHeaders.add(ACCEPT_ENCODING.toLowerCase());

        copyRequestHeaders(headers, builder, allIgnoreHeaders);

        return builder;
    }

    private static ArrayList<String> of(String[] ignoreHeaders) {
        ArrayList<String> allIgnoreHeaders = new ArrayList<String>();
        for (String ignoreHeader : ignoreHeaders) {
            allIgnoreHeaders.add(ignoreHeader.toLowerCase());
        }
        return allIgnoreHeaders;
    }

    protected WebResource.Builder createBuilder(UriInfo uriInfo, String method, HttpHeaders headers, String... ignoreHeaders) {
        return createBuilder(uriInfo, "", method, headers, ignoreHeaders);
    }


    protected WebResource.Builder webResourceWithAuth(String body, String method, URI targetUri) {
        WebResource.Builder builder = HMACJerseyClient
                .create(new DefaultApacheHttpClientConfig())
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
