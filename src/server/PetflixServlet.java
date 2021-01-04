package server;

import java.io.*;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Timer;
import java.util.TimerTask;

/**
 * The main server Servlet.
 */
public class PetflixServlet {

    private final Timer timer = new Timer();
    private static final String DEFAULT_FILENAME = "file.srv";
    private static final long SAVE_DELAY = 1000 * 60 * 3;

    public static class SaveTask extends TimerTask {
        private final PetflixServer petflixServer;
        private final String fileName;

        SaveTask(PetflixServer petflixServer, String fileName) {
            this.petflixServer = petflixServer;
            this.fileName = fileName;
        }

        @Override
        public void run() {
            saveState(petflixServer, fileName);
        }
    }


    public static void main(String[] args) {
        PetflixServlet servlet = new PetflixServlet();

        String host = "localhost";
        int port = 666;

        if (args.length == 1) {
            port = Integer.parseInt(args[0]);
        } else if (args.length == 2) {
            host = args[0];
            port = Integer.parseInt(args[1]);
        }

        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        String fileName = "";

        try {
            PetflixServer petflixServer = null;
            System.out.println("Choose an option:");
            System.out.println("n - New server from scratch");
            System.out.println("l - Load server state from file");
            System.out.println("q - Quit");
            boolean loop = true;
            while (loop) {
                switch (br.readLine()) {
                    case "n":
                        System.out.print("File to save to (default: " + DEFAULT_FILENAME + "): ");
                        fileName = br.readLine();
                        if (fileName.equals("")) fileName = DEFAULT_FILENAME;
                        petflixServer = new PetflixServerImpl();
                        loop = false;
                        break;
                    case "l":
                        System.out.print("File to load from (default: " + DEFAULT_FILENAME + "): ");
                        fileName = br.readLine();
                        if (fileName.equals("")) fileName = DEFAULT_FILENAME;
                        petflixServer = loadState(fileName);
                        loop = false;
                        break;
                    case "q":
                        System.exit(0);
                        break;
                    default:
                        System.out.println(ErrorCodes.OPERATION_DOES_NOT_EXIST.MESSAGE);
                        break;
                }
            }

            LocateRegistry.createRegistry(port);
            Registry registry = LocateRegistry.getRegistry(host, port);
            registry.rebind("petflix", petflixServer);
            servlet.getTimer().schedule(new SaveTask(petflixServer, fileName), SAVE_DELAY);
            System.out.println("Server ready. Saving every " + (float) SAVE_DELAY / 1000 / 60 + " minutes to " + fileName);
            System.out.println("Press s to trigger save or q to quit");
            while (true) {
                String inp = br.readLine();
                if (inp.equals("s")) saveState(petflixServer, fileName);
                else if (inp.equals("q")) System.exit(0);
            }
        } catch (Exception e) {
            System.out.println("Server Error: " + e);
        }
    }

    public static PetflixServer loadState(String filename) {
        Object object = null;
        try {
            ObjectInputStream objectInputStream = new ObjectInputStream(new FileInputStream(filename));
            object = objectInputStream.readObject();
        } catch (IOException e) {
            System.err.println("Could not load file - " + e);
        } catch (ClassNotFoundException e) {
            System.err.println("Could not find class - " + e);
        }
        return (PetflixServer) object;
    }

    public static void saveState(PetflixServer server, String fileName) {
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(fileName);
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
            objectOutputStream.writeObject(server);
            objectOutputStream.close();
            System.out.println("Successfully saved server state to " + fileName);
        } catch (FileNotFoundException e) {
            System.err.println("Unable to find file " + e);
        } catch (IOException e) {
            System.err.println("Unable to write to file " + e);
        }
    }

    public Timer getTimer() {
        return timer;
    }

}
