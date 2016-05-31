package servers;

/**
 * Created by n_buga on 28.05.16.
 */
public interface BaseServer {
    int TIMEOUT = 50;
    void start();
    void close();
    int getPort();
}