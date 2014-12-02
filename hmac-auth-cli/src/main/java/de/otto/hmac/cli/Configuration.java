package de.otto.hmac.cli;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

public class Configuration {

    @Parameter(required =  true)
    private String url;

    @Parameter(names = {"-H", "--headers"}, description =  "Custom header to pass to server (H)")
    private List<String> headers = new ArrayList<String>();

    @Parameter(names = { "-X", "--request"}, description = "COMMAND  Specify request command to use")
    private String method = "GET";

    @Parameter(names = {"--hmac-api-key"}, description = "the api key", required = true)
    private String apiKey = "";

    @Parameter(names = {"--hmac-secret-key"}, description = "the secret key", required =  true)
    private String secretKey = "";

    @Parameter(names = {"-v", "--verbose"}, description = "Enable logging.")
    private boolean verbose = false;

    @Parameter(names = "--help", help = true)
    private boolean help = false;

    public URI getUri() {
        return URI.create(url);
    }

    public List<String> getHeaders() {
        return headers;
    }

    public String getMethod() {
        return method;
    }

    public String getApiKey() {
        return apiKey;
    }

    public String getSecretKey() {
        return secretKey;
    }

    public boolean isVerbose() {
        return verbose;
    }

    public boolean isHelp() {
        return help;
    }

    public static Configuration toConfiguration(String[] strings) {

        Configuration churlConfiguration = new Configuration();

        new JCommander(churlConfiguration, strings);

        return churlConfiguration;
    }

}
