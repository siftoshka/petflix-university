package client;

import server.PetflixServer;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

/**
 * Connection layer.
 */
public class ConnectionLayer {
    private final FailureDetector failureDetector;
    private final String connectionStr;

    private boolean connected = false;

    private PetflixServer server;

    public ConnectionLayer(String connectionStr) {
        this.connectionStr = connectionStr;
        connect();
        failureDetector = new FailureDetector(this);
    }

    public ConnectionLayer(String connectionStr, long period) {
        this.connectionStr = connectionStr;
        connect();
        failureDetector = new FailureDetector(this, period);
    }

    private void connect() {
        if (!isConnected()) {
            try {
                server = (PetflixServer) Naming.lookup(connectionStr);
                // Flag used by the servlet
                setConnected(true);
            } catch (MalformedURLException e) {
                System.err.println("Malformed URL - " + e);
            } catch (NotBoundException e) {
                System.err.println("Unable to bind the server - " + e);
            } catch (RemoteException e) {
                System.err.println("Unable to contact the server - " + e);
            }
        }
    }

    public void reconnect() {
        connect();
        if (isConnected()) System.out.println("Reconnected!");
    }

    public synchronized boolean isConnected() {
        return connected;
    }

    public synchronized void setConnected(boolean connected) {
        this.connected = connected;
    }

    public FailureDetector getFailureDetector() {
        return failureDetector;
    }

    public PetflixServer getServer() throws RemoteException {
        if (isConnected()) return server;
        else throw new RemoteException("Server is dead.");
    }

    public void setServer(PetflixServer server) {
        this.server = server;
    }
}
