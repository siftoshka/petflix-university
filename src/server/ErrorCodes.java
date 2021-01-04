package server;

/**
 * Util for Error Codes.
 */
public enum ErrorCodes {
    CLIENT_NULL(-1, "The client cannot be null"),
    NAME_NULL(-2, "Item name cannot be null"),
    NAME_EMPTY(-3, "Item name cannot be an empty string"),
    NEGATIVE_RATING(-4, "Rating can't be less than 0"),
    MOVIE_DOES_NOT_EXIST(-5, "The movie does not exist"),
    OPERATION_DOES_NOT_EXIST(-6, "This operation does not exist"),
    SUCCESS_BUY(1, "Item was bought successfully"),
    ITEM_CREATED(2, "Item created successfully");

    public final int ID;
    public final String MESSAGE;
    ErrorCodes(int id, String msg) { this.ID = id; this.MESSAGE = msg; }

    @Override
    public String toString() {
        return "Error " + ID + ": " + MESSAGE;
    }
}
