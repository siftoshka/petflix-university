package server;

import java.io.Serializable;

/**
 * History Operation.
 */
public class History implements Serializable {
    private static final long serialVersionUID = 1L;

    private final String clientName;
    private final String movieName;

    public History(String clientName, String movieName) {
        this.clientName = clientName;
        this.movieName = movieName;
    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public String getClientName() {
        return clientName;
    }

    public String getMovieName() {
        return movieName;
    }
}
