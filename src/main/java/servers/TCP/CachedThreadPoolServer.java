package servers.TCP;

import servers.TCP.SocketIOServer;
import utils.Utils;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by n_buga on 28.05.16.
 */
public class CachedThreadPoolServer extends SocketIOServer {
    private final int PORT = 9995;
    private ExecutorService cachedThreadPool = Executors.newCachedThreadPool();

    public CachedThreadPoolServer() {
        super();
    }

    @Override
    public void close() {
        end = true;
        cachedThreadPool.shutdown();
        try {
            Thread.sleep(TIMEOUT);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getPort() {
        return PORT;
    }

    @Override
    protected void handlerConnection() {
        try (ServerSocket serverSocket = new ServerSocket(PORT, 100)){
            serverSocket.setSoTimeout(TIMEOUT);
            while (!end) {
                Socket socket;
                try {
                    socket = serverSocket.accept();
                } catch (SocketTimeoutException e) {
                    continue;
                }
                Utils.Connection connection = new Utils.Connection(socket);
                cachedThreadPool.submit(() -> handlerQuery(connection));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
