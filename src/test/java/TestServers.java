/**
 * Created by n_buga on 28.05.16.
 */
import client.ClientTCP;
import org.junit.Test;
import servers.BaseServer;
import servers.TCP.CachedThreadPoolServer;
import servers.NIO.OneThread;
import servers.TCP.OneClientOneThread;
import utils.Utils;

import static org.junit.Assert.*;

import java.util.*;

public class TestServers {

    @Test
    public void OneClientOneThread() {
        BaseServer baseServer = new OneClientOneThread();
        checkBaseServer(baseServer);
    }

    @Test
    public void CashedThreadPoolServer() {
        BaseServer baseServer = new CachedThreadPoolServer();
        checkBaseServer(baseServer);
    }

    @Test
    public void NonBlockingServer() {
        BaseServer baseServer = new OneThread();
        checkBaseServer(baseServer);
    }

    private void checkBaseServer(BaseServer baseServer) {
        baseServer.start();
        ClientTCP client = new ClientTCP();
        client.createConnection(baseServer.getPort(), "localhost");
        List<Integer> randomArray = Utils.createRandomArray(20);
        List<Integer> sortedArray = client.sortArray(randomArray);
        Collections.sort(randomArray);
        assertEquals(randomArray, sortedArray);
        client.closeConnection();

        client = new ClientTCP();
        client.createConnection(baseServer.getPort(), "localhost");
        randomArray = Utils.createRandomArray(30);
        sortedArray = client.sortArray(randomArray);
        Collections.sort(randomArray);
        assertEquals(randomArray, sortedArray);
        client.closeConnection();

        baseServer.close();
        baseServer.start();
        baseServer.close();
    }
}
