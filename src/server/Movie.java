package server;

import java.io.Serializable;

/**
 * Movie Item.
 */
public class Movie implements Serializable {
    private static final long serialVersionUID = 1L;

    private static int idCounter = 0;
    private int id;
    private String name;
    private float rating;

    public Movie(String name, float rating) {
        synchronized(this) {
            this.id = idCounter;
            idCounter += 1;
        }
        this.name = name;
        this.rating = rating;
    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public static int getIdCounter() {
        return idCounter;
    }

    public static void setIdCounter(int idCounter) {
        Movie.idCounter = idCounter;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    @Override
    public String toString() {
        return "Movie " + name + " #" + id + " with rating " + rating;
    }
}
