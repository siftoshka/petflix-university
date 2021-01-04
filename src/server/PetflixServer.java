package server;

import client.PetflixClient;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Petflix Server.
 */
public interface PetflixServer extends Remote {

    /**
     * Create a movie item
     * @param name movie name
     * @param rating movie rating
     * @return success/error message
     * @throws RemoteException
     */
    String createMovieItem(String name, float rating) throws RemoteException;

    /**
     * Make a rent
     * @param client client
     * @param movieId movie id
     * @return success/error message
     * @throws RemoteException
     */
    String buy(PetflixClient client, int movieId) throws RemoteException;

    /**
     * Returns a nicely formatted string that contains a list of available movies
     * @return list of available movies
     * @throws RemoteException
     */
    String getAvailableMovies() throws RemoteException;

    /**
     * Returns a nicely formatted string that contains a history
     * @return history of rent movies
     * @throws RemoteException
     */
    String getHistory() throws RemoteException;

    /**
     * Probes the server to check if alive.
     * @throws RemoteException
     */
    void probe() throws RemoteException;
}
