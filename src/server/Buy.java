package server;

import client.PetflixClient;

import java.io.Serializable;

/**
 * Buy Operation.
 */
public class Buy implements Serializable {
    private static final long serialVersionUID = 1L;

    private final PetflixClient client;
    private final String clientName;
    private final Movie movie;

    public Buy(PetflixClient client, String clientName, Movie movie) {
        this.client = client;
        this.clientName = clientName;
        this.movie = movie;
    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public PetflixClient getClient() {
        return client;
    }

    public String getClientName() {
        return clientName;
    }

    public Movie getMovie() {
        return movie;
    }
}
