package client;

import utils.Utils;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by n_buga on 01.06.16.
 */
abstract public class Client {
    protected List<Long> timeQueryHandler = new ArrayList<>();
    protected List<Long> timeQueryCount = new ArrayList<>();

    abstract public void createConnection(int port, String ip);

    abstract public void closeConnection();

    abstract public List<Integer> sortArray(List<Integer> array);

    public double getHandlerTimeQuery() {
        return ((double) timeQueryHandler.stream().reduce(0L, (a, b) -> a + b))/ timeQueryHandler.size();
    }

    public double getHandlerCountQuery() {
        return ((double) timeQueryCount.stream().reduce(0L, (a, b) -> a + b))/ timeQueryCount.size();
    }
}