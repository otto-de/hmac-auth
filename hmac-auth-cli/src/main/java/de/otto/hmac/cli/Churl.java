package de.otto.hmac.cli;

import com.beust.jcommander.JCommander;

public class Churl {
    private final Configuration configuration;

    public Churl(Configuration configuration) {
        this.configuration = configuration;
    }

    public static void main(String[] args) {

        Configuration configuration = new Configuration();
        JCommander jCommander = new JCommander(configuration, args);
        if(configuration.isHelp()) {
            jCommander.usage();
            System.exit(0);
        }

        new Churl(configuration).execute();
    }

    private void execute() {

    }

}
