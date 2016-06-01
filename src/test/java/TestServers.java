/**
 * Created by n_buga on 28.05.16.
 */
import client.ClientTCP;
import client.ClientUDP;
import org.junit.Test;
import servers.BaseServer;
import servers.NIO.CachedThreadPool;
import servers.NIO.FixedThreadPool;
import servers.NIO.NewQueryNewThread;
import servers.TCP.CachedThreadPoolServer;
import servers.NIO.OneThread;
import servers.TCP.NewQueryNewConnection;
import servers.TCP.OneClientOneThread;
import utils.Utils;

import static org.junit.Assert.*;

import java.util.*;

public class TestServers {

    @Test
    public void OneClientOneThread() {
        BaseServer baseServer = new OneClientOneThread();
        checkBaseServerTCP(baseServer);
    }

    @Test
    public void CashedThreadPoolServer() {
        BaseServer baseServer = new CachedThreadPoolServer();
        checkBaseServerTCP(baseServer);
    }

    @Test
    public void NIOOneThread() {
        BaseServer baseServer = new OneThread();
        checkBaseServerTCP(baseServer);
    }

    @Test
    public void NewQueryNewConnect() {
        BaseServer baseServer = new NewQueryNewConnection();
        checkBaseServerTCP(baseServer);
    }

    @Test
    public void NIOCachedThreadPool() {
        BaseServer baseServer = new CachedThreadPool();
        checkBaseServerTCP(baseServer);
    }

    @Test
    public void NIOFixedThreadPool() {
        BaseServer baseServer = new FixedThreadPool();
        checkBaseServerTCP(baseServer);
    }

    @Test
    public void NIONewQueryNewThread() {
        BaseServer baseServer = new NewQueryNewThread();
        checkBaseServerTCP(baseServer);
    }

    @Test
    public void UDPFixedThreadPool() {
        BaseServer baseServer = new servers.UDP.FixedThreadPool();
        checkBaseServerUDP(baseServer);
    }

    @Test
    public void UDPNewQueryNewThread() {
        BaseServer baseServer = new servers.UDP.NewQueryNewThread();
        checkBaseServerUDP(baseServer);
    }

    private void checkBaseServerUDP(BaseServer baseServer) {
        baseServer.start();
        ClientUDP client = new ClientUDP();
        client.createConnection(baseServer.getPort(), "localhost");
        List<Integer> randomArray = Utils.createRandomArray(20);
        List<Integer> sortedArray = client.sortArray(randomArray);
        Collections.sort(randomArray);
        assertEquals(randomArray, sortedArray);
        client.closeConnection();

        client = new ClientUDP();
        client.createConnection(baseServer.getPort(), "localhost");
        randomArray = Utils.createRandomArray(10000);
        sortedArray = client.sortArray(randomArray);
        Collections.sort(randomArray);
        assertEquals(randomArray, sortedArray);
        client.closeConnection();

        baseServer.close();
        baseServer.start();
        baseServer.close();
    }

    private void checkBaseServerTCP(BaseServer baseServer) {
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
        randomArray = Utils.createRandomArray(10000);
        sortedArray = client.sortArray(randomArray);
        Collections.sort(randomArray);
        assertEquals(randomArray, sortedArray);
        client.closeConnection();

        baseServer.close();
        baseServer.start();
        baseServer.close();
    }
}
