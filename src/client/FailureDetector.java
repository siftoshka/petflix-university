package client;

import java.rmi.RemoteException;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Util for detect a failure.
 */
public class FailureDetector {
    private final static long DEFAULT_TIMEOUT = 5000, DEFAULT_PERIOD = 5000;
    private final static int DEFAULT_NO_OF_PROBES = 10000;

    private final ConnectionLayer connection;
    private final Timer timer = new Timer();
    private long timeout, period;

    private class ProbeTask extends TimerTask {
        @Override
        public void run() {
            try {
                if (!connection.isConnected()) connection.reconnect();
                connection.getServer().probe();
            } catch (RemoteException e) {
                System.err.println("Retrying in " + period + "ms");
                connection.setConnected(false);
            }
        }
    }

    public FailureDetector(ConnectionLayer connection) {
        this(connection, DEFAULT_TIMEOUT, DEFAULT_PERIOD);
    }

    public FailureDetector(ConnectionLayer connection, long period) {
        this(connection, DEFAULT_TIMEOUT, period);
    }

    public FailureDetector(ConnectionLayer connection, long timeout, long period) {
        this.connection = connection;
        this.timeout = timeout;
        this.period = period;
        this.timer.schedule(new ProbeTask(), 1, period);
    }

    public FailureDetector(ConnectionLayer connection, int noOfProbes, long sensitivity, long period) {
        this.connection = connection;
        try {
            this.timeout = determineTimeout(noOfProbes, sensitivity);
        } catch (RemoteException e) {
            System.err.println("Unable to contact the server in order to determine timeout. Setting default");
            this.timeout = DEFAULT_TIMEOUT;
        }
        timer.schedule(new ProbeTask(), 0, period);
    }

    private long determineTimeout(int noOfProbes, long sensitivity) throws RemoteException {
        return (long) determineLoad(noOfProbes) + sensitivity;
    }

    public float determineLoad() throws RemoteException {
        return determineLoad(DEFAULT_NO_OF_PROBES);
    }

    public float determineLoad(int noOfProbes) throws RemoteException {
        connection.getServer().probe();

        long start = System.currentTimeMillis();
        for (int i = 0; i < noOfProbes; i++) {
            connection.getServer().probe();
        }
        return (float) (System.currentTimeMillis() - start) / noOfProbes;
    }

    public long getTimeout() {
        return timeout;
    }

    public void setTimeout(long timeout) {
        this.timeout = timeout;
    }

    public long getPeriod() {
        return period;
    }

    public void setPeriod(long period) {
        this.period = period;
    }
}
