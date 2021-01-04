package server;

import client.PetflixClient;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Implementation of Petflix Server.
 */
public class PetflixServerImpl extends UnicastRemoteObject implements PetflixServer {
    static final long serialVersionUID = 1L;
    private static final Logger LOGGER = Logger.getLogger(PetflixServerImpl.class.getName());

    private final Map<Integer, Movie> movies;
    private final Map<Integer, History> history;
    private final LinkedList<Buy> rents;

    public PetflixServerImpl() throws RemoteException {
        super();
        movies = new ConcurrentHashMap<>();
        history = new HashMap<>();
        rents = new LinkedList<>();
        LOGGER.setLevel(Level.OFF);
    }

    @Override
    public String createMovieItem(String name, float rating) throws RemoteException {
        if (name == null) return ErrorCodes.NAME_NULL.MESSAGE;
        if (name.length() == 0) return ErrorCodes.NAME_EMPTY.MESSAGE;
        if (rating < 0) return ErrorCodes.NEGATIVE_RATING.MESSAGE;

        // Further validation in Movie item's constructor
        Movie movie = new Movie(name, rating);
        movies.put(movie.getId(), movie);
        return ErrorCodes.ITEM_CREATED.MESSAGE;
    }

    @Override
    public String buy(PetflixClient client, int movieId) throws RemoteException {
        if (client == null) return ErrorCodes.CLIENT_NULL.MESSAGE;

        // Get client name so we don't have to query multiple times and risk a RemoteException
        String clientName = client.getName();
        LOGGER.info("Client " + clientName + " is trying to buy a movie with ID #" + movieId);

        // Validate item exists and doesn't belong to another client
        Movie movie = movies.get(movieId);
        if (movie == null) return ErrorCodes.MOVIE_DOES_NOT_EXIST.MESSAGE;

        synchronized(this) {
            // Buy the movie (further validation is in buy Item's constructor)
            rents.push(new Buy(client, clientName, movie));
            history.put(movieId, new History(clientName, movie.getName()));
            movies.remove(movieId);
        }

        LOGGER.info(client.getName() + ", movie ID #" + movieId);
        return ErrorCodes.SUCCESS_BUY.MESSAGE;
    }

    @Override
    public String getAvailableMovies() throws RemoteException {
        if (movies.size() == 0) return "No available movies";
        StringBuilder result = new StringBuilder();
        String separator = "\n-----------------------\n";
        for (Movie movie : movies.values()) {
            result.append(movie.toString());
            result.append(separator);
        }
        result.delete(result.length() - separator.length(), result.length());
        return result.toString();
    }

    @Override
    public String getHistory() throws RemoteException {
        if (history.size() == 0) return "No any history";
        StringBuilder result = new StringBuilder();
        String separator = "\n-----------------------\n";
        for (History history : history.values()) {
            result.append("User ").append(history.getClientName()).append(" bought movie ").append(history.getMovieName());
            result.append(separator);
        }
        result.delete(result.length() - separator.length(), result.length());
        return result.toString();
    }

    @Override
    public void probe() throws RemoteException { }
}
