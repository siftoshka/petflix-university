package client;

import server.ErrorCodes;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.rmi.RemoteException;

/**
 * The main client Servlet.
 */
public class ClientServlet {

    public static void main(String[] args) {
        String hostname = "localhost";
        int port = 666;

        if (args.length == 1)
            port = Integer.parseInt(args[0]);
        else if (args.length == 2) {
            hostname = args[0];
            port = Integer.parseInt(args[1]);
        }

        String connectionStr = "rmi://"+hostname+":"+port+"/petflix";
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

        try {
            ConnectionLayer connection = new ConnectionLayer(connectionStr);
            
            System.out.print("What is your username? ");
            PetflixClientImpl client = new PetflixClientImpl(br.readLine());
            System.out.println("Choose an option");
            System.out.println("l - List of movies");
            System.out.println("n - New movie");
            System.out.println("b - Buy");
            System.out.println("h - History");
            System.out.println("t - Check server load (average turnaround in ms)");
            System.out.println("q - Quit");

            boolean isAdmin = false;
            boolean end = false;
            while (!end) {
                String response = "";
                try {
                    switch (br.readLine().toLowerCase()) {
                        case "l":
                            response = connection.getServer().getAvailableMovies();
                            break;
                        case "n":
                            try {
                                if (isAdmin) {
                                    System.out.print("Movie name: ");
                                    String name = br.readLine();
                                    if (name.equals("")) throw new NumberFormatException();
                                    System.out.print("Rating: ");
                                    float rating = Float.parseFloat(br.readLine());
                                    response = connection.getServer().createMovieItem(name, rating);
                                } else {
                                    System.out.println("Only admins can add the movie\n Please, enter the password:");
                                    String password = br.readLine();

                                    if (password.equals("MIE-DSV")) {
                                        isAdmin = true;
                                        System.out.println("Welcome admin! Now you can add the movie!");
                                    }
                                    else System.err.println("You are not allowed to add a movie to Petflix Store!");
                                }
                            } catch (NumberFormatException nfe) {
                                System.err.println("Incorrect input format. Please try again.");
                            }
                            break;
                        case "b":
                            try {
                                System.out.print("Movie ID: ");
                                int movieId = Integer.parseInt(br.readLine());
                                response = connection.getServer().buy(client, movieId);
                            } catch (NumberFormatException nfe) {
                                System.err.println("Incorrect input format. Please try again.");
                            }
                            break;
                        case "h":
                            response = connection.getServer().getHistory();
                            break;
                        case "t":
                            response = "Average turnaround - " + connection.getFailureDetector().determineLoad() + "ms";
                            break;
                        case "q":
                            end = true;
                            break;
                        default:
                            response = ErrorCodes.OPERATION_DOES_NOT_EXIST.MESSAGE;
                            break;
                    }
                } catch (RemoteException exception) {
                    System.out.println(exception.toString());
                }
                System.out.println(response);
            }
            System.exit(0);
        } catch (IOException e) {
            System.err.println("Unable to parse your input " + e);
            System.exit(2);
        }
    }
}
