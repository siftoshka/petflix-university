package client;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

/**
 * Implementation of Petflix Client.
 */
public class PetflixClientImpl extends UnicastRemoteObject implements PetflixClient {
    private static final long serialVersionUID = 1L;
    private String name;

    public PetflixClientImpl() throws RemoteException {
        super();
        this.name = "Name not set";
    }

    public PetflixClientImpl(String name) throws RemoteException {
        super();
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public void callback(String message) {
        System.out.println(message);
    }

    @Override
    public String toString() {
        return name;
    }

}
